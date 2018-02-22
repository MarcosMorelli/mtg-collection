package mtg.collection.editions;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import com.fasterxml.jackson.databind.ObjectMapper;

import mtg.collection.MagicCard;
import mtg.collection.scg.SCGCard;

public class EditionsController {

	private static EditionsController INSTANCE;

	private final List<String> justFoilEditions;
	private final List<String> basicLands;

	private ConcurrentHashMap<String, MagicCard> editionsCards = new ConcurrentHashMap<String, MagicCard>();

	private EditionsController() {
		justFoilEditions = getJustFoilEditions();
		basicLands = getBasicLands();
	}

	public static EditionsController getInstance() {
		if (INSTANCE != null) {
			return INSTANCE;
		}

		INSTANCE = new EditionsController();
		return INSTANCE;
	}

	public Collection<MagicCard> getAllCards() {
		return editionsCards.values();
	}

	public List<MagicCard> getEditionCards(final String edition) {
		final ArrayList<MagicCard> list = new ArrayList<MagicCard>();
		editionsCards.keys().asIterator().forEachRemaining(key -> {
			if (key.contains(edition)) {
				list.add(editionsCards.get(key));
			}
		});
		Collections.sort(list);
		return list;
	}
	
	public List<MagicCard> getEditionCards(final Editions edition) {
		final ArrayList<MagicCard> list = new ArrayList<MagicCard>();
		editionsCards.keys().asIterator().forEachRemaining(key -> {
			if (key.contains(edition.getName())) {
				list.add(editionsCards.get(key));
			}
		});
		Collections.sort(list);
		return list;
	}

	public MagicCard getCard(final String name, final String edition) {
		return editionsCards.get(name + edition);
	}

	public void setScgPrice(final Editions edition, final SCGCard card) {
		final String key = card.toString() + edition.getName();
		if (editionsCards.containsKey(key)) {
			editionsCards.get(key).setPrice(Float.parseFloat(card.price));
		}
	}

	public void fetchEditionsInfo() {
		final ArrayList<Editions> editions = new ArrayList<Editions>(Arrays.asList(Editions.values()));

		final Predicate<Editions> predicate = edition -> FileUtils.getFile(edition.getFileName()).exists();
		editions.removeIf(predicate);

		for (final Editions edition : editions) {
			fetchEditionInfo(edition);
		}
	}

	public void readEditions() {
		final Collection<File> editionsList = FileUtils.listFiles(new File("editions"), TrueFileFilter.INSTANCE, null);
		final ObjectMapper mapper = new ObjectMapper();
		for (final File edition : editionsList) {
			try {
				final MagicCard[] cards = mapper.readValue(
						new ByteArrayInputStream(
								FileUtils.readFileToString(edition, Charset.defaultCharset()).getBytes("UTF-8")),
						MagicCard[].class);
				for (final MagicCard card : cards) {
					editionsCards.put(card.toString(), card);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void writeEditions() {
		for (final Editions edition : Editions.values()) {
			final ObjectMapper mapper = new ObjectMapper();
			try {
				String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(getEditionCards(edition));
				FileUtils.write(new File("editions/" + edition.name().replaceAll("_", "")), json,
						Charset.defaultCharset());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private List<String> getJustFoilEditions() {
		return Arrays.asList("Arena League", "Media Inserts", "Zendikar Expeditions", "Kaladesh Inventions",
				"Grand Prix", "World Magic Cup Qualifiers", "Prerelease Events", "Friday Night Magic",
				"Judge Gift Program");
	}

	private List<String> getBasicLands() {
		return Arrays.asList("Island", "Swamp", "Mountain", "Plains", "Forest");
	}

	private void fetchEditionInfo(final Editions edition) {
		try {
			final ChromeDriver driver = getChromeDriver();
			final String link = "http://magiccards.info/query?q=e%3A" + edition.toString().replaceAll("_", "")
					+ "%2Fen&v=list&s=cname";

			final List<MagicCard> magicCards = new ArrayList<MagicCard>();
			final List<String> cardInfos = new ArrayList<String>();

			driver.get(link);

			WebElement table = driver.findElement(By.xpath("/html/body/table[3]"));
			List<WebElement> trs = table.findElements(By.tagName("tr"));
			for (final WebElement tr : trs) {
				final List<WebElement> tds = tr.findElements(By.tagName("td"));
				tds: for (final WebElement td : tds) {
					cardInfos.add(td.getText());

					if (cardInfos.size() == 2) {
						if (basicLands.contains(cardInfos.get(1))) {
							cardInfos.clear();
							break tds;
						}

						cardInfos.add(td.findElement(By.tagName("a")).getAttribute("href"));
					} else if (cardInfos.size() == 8) {
						if (!cardInfos.get(7).startsWith("From the Vault")
								&& !justFoilEditions.contains(cardInfos.get(7))) {
							magicCards.add(new MagicCard(cardInfos, false));
						}
						magicCards.add(new MagicCard(cardInfos, true));
						cardInfos.clear();
					}
				}
			}

			driver.get(link.replaceAll("Fen", "Fpt"));

			try {
				table = driver.findElement(By.xpath("/html/body/table[3]"));
				trs = table.findElements(By.tagName("tr"));
				for (final WebElement tr : trs) {
					final List<WebElement> tds = tr.findElements(By.tagName("td"));

					tds: for (final WebElement td : tds) {
						cardInfos.add(td.getText());

						if (cardInfos.size() == 2) {
							for (final MagicCard card : magicCards) {
								if (card.getNumber().equals(cardInfos.get(0))) {
									card.setPtName(cardInfos.get(1));
								}
							}
							cardInfos.clear();
							break tds;
						}

					}
				}
			} catch (final NoSuchElementException editionDontHavePtLang) {
			}

			driver.quit();

			final ObjectMapper mapper = new ObjectMapper();
			final String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(magicCards);
			FileUtils.write(new File(edition.getFileName()), json, Charset.defaultCharset());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private ChromeDriver getChromeDriver() {
		final ChromeOptions options = new ChromeOptions();
		options.addArguments(Arrays.asList("headless", "window-size=1920x1080"));

		final ChromeDriver driver = new ChromeDriver(options);
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		driver.manage().window().maximize();
		return driver;
	}

}
