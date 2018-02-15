package mtg.collection.editions;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import com.fasterxml.jackson.databind.ObjectMapper;

import mtg.collection.MagicCard;

public class EditionsController {

	private static EditionsController INSTANCE;

	private Collection<File> editionsList;
	private ConcurrentHashMap<String, MagicCard> editionsCards = new ConcurrentHashMap<String, MagicCard>();

	private EditionsController() {
		editionsList = FileUtils.listFiles(new File("editions"), TrueFileFilter.INSTANCE, null);
		readEditions();
	}

	public static EditionsController getInstance() {
		if (INSTANCE != null) {
			return INSTANCE;
		}

		INSTANCE = new EditionsController();
		return INSTANCE;
	}

	private void readEditions() {
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

	public Collection<MagicCard> getAllCards() {
		return editionsCards.values();
	}

	public void fetchEditionsInfo() {
		for (final Editions edition : Editions.values()) {
			fetchEditionInfo(edition);
		}
	}

	private void fetchEditionInfo(final Editions edition) {
		final String fileName = "editions/" + edition.toString().replaceAll("_", "");
		if (FileUtils.getFile(fileName).exists()) {
			return;
		}

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
				for (final WebElement td : tds) {
					cardInfos.add(td.getText());

					if (cardInfos.size() == 2) {
						cardInfos.add(td.findElement(By.tagName("a")).getAttribute("href"));
					} else if (cardInfos.size() == 8) {
						if (!cardInfos.get(7).startsWith("From the Vault") && !cardInfos.get(7).equals("Arena League")
								&& !cardInfos.get(7).equals("Media Inserts")
								&& !cardInfos.get(7).equals("Zendikar Expeditions")
								&& !cardInfos.get(7).equals("Kaladesh Inventions")
								&& !cardInfos.get(7).equals("Grand Prix")
								&& !cardInfos.get(7).equals("World Magic Cup Qualifiers")
								&& !cardInfos.get(7).equals("Prerelease Events")
								&& !cardInfos.get(7).equals("Friday Night Magic")
								&& !cardInfos.get(7).equals("Judge Gift Program")) {
							magicCards.add(new MagicCard(cardInfos));
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
			FileUtils.write(new File(fileName), json, Charset.defaultCharset());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private ChromeDriver getChromeDriver() {
		final ChromeDriver driver = new ChromeDriver();
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		driver.manage().window().maximize();
		return driver;
	}

}
