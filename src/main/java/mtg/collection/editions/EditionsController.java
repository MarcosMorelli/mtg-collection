package mtg.collection.editions;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

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
			final ArrayList<Editions> editions = new ArrayList<Editions>(Arrays.asList());

			if (editions.isEmpty()) {
				return;
			}

			final ObjectMapper mapper = new ObjectMapper();
			final HashMap<String, String> dictionary = mapper.readValue(new File("translate.json"),
					TypeFactory.defaultInstance().constructMapLikeType(HashMap.class, String.class, String.class));

			final SCGUtil util = new SCGUtil();
			final ChromeDriver driver = util.getChromeDriver();

			editions.forEach(edition -> {
				try {
					writeTranslationKeysFromLigaMagic(dictionary, driver, edition.toString());
				} catch (IOException | WebSocketException | InterruptedException e) {
					e.printStackTrace();
				}
			});

			final Comparator<Entry<String, String>> valueComparator = (e1, e2) -> e1.getKey().compareTo(e2.getKey());

			final Map<String, String> sortedMap = dictionary.entrySet().stream().sorted(valueComparator)
					.collect(Collectors.toMap(Entry::getKey, Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

			String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(sortedMap);
			FileUtils.write(new File("translate.json"), json, Charset.forName("UTF-8"));
		} catch (IOException | WebSocketException | InterruptedException e1) {
			e1.printStackTrace();
		}
	}

	public void writeTranslationKeysFromLigaMagic(final HashMap<String, String> dictionary, final ChromeDriver driver,
			final String edition) throws IOException, WebSocketException, InterruptedException {
		final String divider = "&aux=";
		for (int i = 1; i <= 11; i++) {
			System.out.println("Translating " + edition + " page " + i);
			driver.get("https://www.ligamagic.com.br/?view=cards/search&card=ed=" + edition + "&page=" + i);

			WebElement tbody = driver.findElement(By.id("cotacao-busca")).findElement(By.tagName("tbody"));
			List<WebElement> tds = tbody.findElements(By.className("col-1"));
			tds.forEach(td -> {
				try {
					String cleanLink = URLDecoder.decode(td.findElement(By.tagName("a")).getAttribute("href"), "UTF-8")
							.replace("https://www.ligamagic.com.br/?view=cards/card&card=", "");
					String enName = cleanLink.substring(0, cleanLink.indexOf("&"));
					String ptName = cleanLink.substring(cleanLink.indexOf(divider) + divider.length());

					if (enName.contains("(#")) {
						return;
					}

					if (SCGUtil.BASIC_LANDS.contains(enName)) {
						return;
					}

					if (!ptName.isEmpty() && !dictionary.containsKey(enName)) {
						dictionary.put(enName, ptName);
					}
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			});
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

			System.out.println(edition.getScgLink(0));

			int count = 0;
			while (util.isNextPage(driver)) {
				cardsList.addAll(readSCGPage(util, driver, edition));
				util.clickAtNextPage(driver);
				System.out.println("fetchEditionsInfo: Reading " + edition.toString() + " page " + ++count);
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
