package mtg.collection.editions;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.neovisionaries.ws.client.WebSocketException;

import mtg.collection.scg.SCGCard;
import mtg.collection.scg.SCGUtil;

public class EditionsController {

	private static EditionsController INSTANCE;

	private ConcurrentHashMap<MagicCardKey, MagicCard> editionsCards = new ConcurrentHashMap<MagicCardKey, MagicCard>();

	private EditionsController() {
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
		Collections.list(editionsCards.keys()).iterator().forEachRemaining(key -> {
			if (key.getEdition().equals(edition)) {
				list.add(editionsCards.get(key));
			}
		});
		Collections.sort(list);
		return list;
	}

	public List<MagicCard> getEditionCards(final Editions edition) {
		return getEditionCards(edition.getName());
	}

	public MagicCard getCard(final String name) {
		for (MagicCard card : editionsCards.values()) {
			if (card.getEnName().equals(name)) {
				return card;
			}
		}

		return null;
	}

	public MagicCard getCard(final String name, final String edition) {
		return editionsCards.get(new MagicCardKey(name, edition));
	}

	public void setScgPrice(final Editions edition, final SCGCard card) {
		final MagicCardKey key = new MagicCardKey(card.toString(), edition.getName());
		if (editionsCards.containsKey(key) && !card.price.isEmpty()) {
			editionsCards.get(key).setPrice(Float.parseFloat(card.price));
		} else {
			System.err.println("CARD NOT FOUND: " + card.name + " " + card.price);
		}
	}

	public void fetchEditionsInfo() {
		final ArrayList<Editions> editions = new ArrayList<Editions>(Arrays.asList(Editions.values()));
		final Predicate<Editions> predicate = edition -> FileUtils.getFile(edition.getFileName()).exists();
		editions.removeIf(predicate);

		for (final Editions edition : editions) {
			fetchEditionInfo(edition);
		}

		System.err.println("fetchEditionsInfo: Done!");
	}

	public void fillPtNames() {
		ObjectMapper mapper = new ObjectMapper();
		try {
			final HashMap<String, String> dictionary = mapper.readValue(new File("translate.json"),
					TypeFactory.defaultInstance().constructMapLikeType(HashMap.class, String.class, String.class));

			boolean nameRefreshed = false;

			final ArrayList<MagicCard> list = new ArrayList<MagicCard>(editionsCards.values());
			final Predicate<MagicCard> predicate = card -> !card.getPtName().isEmpty();
			list.removeIf(predicate);
			for (MagicCard ptBlankNameCard : list) {

				final String enName = ptBlankNameCard.isFoil() ? ptBlankNameCard.getEnName().replace(" (FOIL)", "")
						: ptBlankNameCard.getEnName();
				if (dictionary.containsKey(enName)) {
					editionsCards.get(ptBlankNameCard.getKey()).setPtName(dictionary.get(enName));
					nameRefreshed = true;
				}
			}

			if (nameRefreshed) {
				writeEditions();
			}
		} catch (Exception e) {
		}
	}

	public void writeAtTranslateFile() {
		try {
			ArrayList<Editions> editions = new ArrayList<Editions>(Arrays.asList());
			
			if (editions.isEmpty()) {
				return;
			}

			ObjectMapper mapper = new ObjectMapper();
			final HashMap<String, String> dictionary = mapper.readValue(new File("translate.json"),
					TypeFactory.defaultInstance().constructMapLikeType(HashMap.class, String.class, String.class));

			final SCGUtil util = new SCGUtil();
			final ChromeDriver driver = util.getChromeDriver();

			editions.forEach(edition -> {
				System.err.println(edition.getName());
				driver.get("https://magiccards.info/" + edition.name().replace("_", "") + "/en.html");
				List<WebElement> tables = driver.findElements(By.tagName("tbody"));

				if (tables.size() < 4) {
					System.err.println("en tables < 4");
					return;
				}

				List<WebElement> links = tables.get(3).findElements(By.tagName("a"));
				HashMap<String, String> map = new HashMap<String, String>();
				links.forEach(element -> {
					String link = element.getAttribute("href");
					String number = link.replaceAll("[a-z/:.]*", "");
					map.put(number, element.getText());
				});

				driver.get("https://magiccards.info/" + edition.name().replace("_", "") + "/pt.html");
				tables = driver.findElements(By.tagName("tbody"));

				if (tables.size() < 4) {
					System.err.println("pt tables < 4");
					return;
				}

				links = tables.get(3).findElements(By.tagName("a"));
				links.forEach(element -> {
					String link = element.getAttribute("href");
					String number = link.replaceAll("[a-z/:.]*", "");
					String ptName = element.getText();
					String enName = map.get(number);

					if (!ptName.isEmpty() && !ptName.equals(enName) && !dictionary.containsKey(enName)) {
						dictionary.put(enName, ptName);
					}
				});
			});

			String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(dictionary);
			FileUtils.write(new File("translate.json"), json, Charset.forName("UTF-8"));
		} catch (IOException | WebSocketException | InterruptedException e1) {
			e1.printStackTrace();
		}
	}

	public void readEditions() {
		final ObjectMapper mapper = new ObjectMapper();
		for (final Editions edition : Editions.values()) {
			try {
				final MagicCard[] cards = mapper.readValue(new ByteArrayInputStream(FileUtils
						.readFileToString(new File(edition.getFileName()), Charset.defaultCharset()).getBytes("UTF-8")),
						MagicCard[].class);
				for (final MagicCard card : cards) {
					editionsCards.put(card.getKey(), card);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void writeEditions() {
		final ObjectMapper mapper = new ObjectMapper();
		for (final Editions edition : Editions.values()) {
			try {
				String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(getEditionCards(edition));
				FileUtils.write(new File(edition.getFileName()), json, Charset.forName("UTF-8"));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void fetchEditionInfo(final Editions edition) {
		final ArrayList<MagicCard> cardsList = new ArrayList<MagicCard>();

		final SCGUtil util = new SCGUtil(edition);
		ChromeDriver driver = null;
		try {
			driver = util.getChromeDriver();
			driver.get(edition.getScgLink(0));

			System.err.println(edition.getScgLink(0));

			int count = 0;
			while (util.isNextPage(driver)) {
				cardsList.addAll(readSCGPage(util, driver, edition));
				util.clickAtNextPage(driver);
				System.err.println("fetchEditionsInfo: Reading " + ++count);
			}
			cardsList.addAll(readSCGPage(util, driver, edition));

			final ObjectMapper mapper = new ObjectMapper();
			final String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(cardsList);
			FileUtils.write(new File(edition.getFileName()), json, Charset.defaultCharset());
		} catch (IOException | WebSocketException | InterruptedException e) {
			e.printStackTrace();
		} finally {
			driver.quit();
		}
	}

	private ArrayList<MagicCard> readSCGPage(final SCGUtil util, final WebDriver driver, final Editions edition)
			throws IOException {
		final ArrayList<MagicCard> list = new ArrayList<MagicCard>();

		final List<WebElement> linhas = util.getLinesFromTable(driver, edition);
		for (final WebElement linha : linhas) {
			MagicCard card = new MagicCard();

			final String cardName = linha.findElement(By.className("search_results_1")).getText();

			card.setFoil(util.isFoil(cardName, linha.findElement(By.className("search_results_2")).getText()));
			card.setEnName(util.getCardName(cardName), card.isFoil());
			card.setEdition(edition.getName());
			card.setMana(util.getManaCost(driver, linha.findElement(By.className("search_results_3"))));
			card.setType(util.getType(linha.findElement(By.className("search_results_4")).getText(),
					linha.findElement(By.className("search_results_5")).getText()));
			card.setRarity(util.getRarity(linha.findElement(By.className("search_results_6")).getText()));
			card.setPrice(util.getPrice(driver, linha.findElement(By.className("search_results_9"))));
			card.setCardLink(linha.findElement(By.className("search_results_1")).findElement(By.tagName("a"))
					.getAttribute("href"));
			try {
				String rel = linha.findElement(By.className("search_results_1")).findElement(By.tagName("a"))
						.getAttribute("rel");
				rel = rel.substring(rel.indexOf("src='") + 5);
				rel = rel.substring(0, rel.indexOf("'"));
				card.setCardImageHRef(rel);
			} catch (StringIndexOutOfBoundsException e) {
				System.err.println(e.getMessage());
				System.err.println(card.getEnName());

				card.setCardImageHRef("");
			}

			list.add(card);
		}

		return list;
	}

}
