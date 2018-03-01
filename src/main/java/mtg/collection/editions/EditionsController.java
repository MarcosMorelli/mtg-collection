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
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import com.fasterxml.jackson.databind.ObjectMapper;

import mtg.collection.scg.SCGCard;

public class EditionsController {

	private static EditionsController INSTANCE;

	private final List<String> justFoilEditions;
	private final List<String> nonFoilEditions;
	private final List<String> basicLands;
	private final ConcurrentHashMap<String, List<String>> justFoilCardsOfEditions;

	private ConcurrentHashMap<MagicCardKey, MagicCard> editionsCards = new ConcurrentHashMap<MagicCardKey, MagicCard>();

	private EditionsController() {
		justFoilEditions = getJustFoilEditions();
		nonFoilEditions = getNonFoilEditions();
		basicLands = getBasicLands();
		justFoilCardsOfEditions = getJustFoilCardsOfEditions();
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
		final ArrayList<MagicCard> list = new ArrayList<MagicCard>();
		Collections.list(editionsCards.keys()).iterator().forEachRemaining(key -> {
			if (key.getEdition().equals(edition.getName())) {
				list.add(editionsCards.get(key));
			}
		});
		Collections.sort(list);
		return list;
	}

	public MagicCard getCard(final String name, final String edition) {
		return editionsCards.get(new MagicCardKey(name, edition));
	}

	public void setScgPrice(final Editions edition, final SCGCard card) {
		final MagicCardKey key = new MagicCardKey(card.toString(), edition.getName());
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
				FileUtils.write(new File(edition.getFileName()), json, Charset.defaultCharset());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private List<String> getJustFoilEditions() {
		return Arrays.asList("15th Anniversary", "Amonkhet Invocations", "Arena League", "Friday Night Magic",
				"Grand Prix", "Judge Gift Program", "Kaladesh Inventions", "Magic: The Gathering Launch Parties",
				"Media Inserts", "Prerelease Events", "Pro Tour", "Release Events", "Summer of Magic", "Super Series",
				"World Magic Cup Qualifiers", "WPN/Gateway", "Zendikar Expeditions");
	}

	private List<String> getNonFoilEditions() {
		return Arrays.asList("Antiquities", "Arabian Nights", "Champs", "Classic Sixth Edition", "Commander",
				"Commander 2013 Edition", "Commander 2014 Edition", "Commander 2015", "Commander 2016",
				"Commander 2017", "Commander Anthology", "Exodus", "Fallen Empires", "Fifth Edition", "Fourth Edition",
				"Homelands", "Ice Age", "Legends", "Limited Edition Alpha", "Limited Edition Beta",
				"Magic Game Day Cards", "Magic Player Rewards", "Mirage", "Planechase", "Planechase Anthology",
				"Planechase 2012 Edition", "Portal", "Portal Second Age", "Portal Three Kingdoms", "Revised Edition",
				"Starter 1999", "Stronghold", "Tempest", "The Dark", "Ugin's Fate", "Unlimited Edition", "Urza's Saga",
				"Visions", "Weatherlight");
	}

	private List<String> getBasicLands() {
		return Arrays.asList("Island", "Swamp", "Mountain", "Plains", "Forest");
	}

	private ConcurrentHashMap<String, List<String>> getJustFoilCardsOfEditions() {
		final ConcurrentHashMap<String, List<String>> returnMap = new ConcurrentHashMap<String, List<String>>();
		returnMap.put("Champs", Arrays.asList("Doran, the Siege Tower", "Groundbreaker", "Mutavault",
				"Niv-Mizzet, the Firemind", "Serra Avenger", "Voidslime"));

		returnMap.put("Commander 2016",
				Arrays.asList("Akiri, Line-Slinger", "Atraxa, Praetors' Voice", "Breya, Etherium Shaper",
						"Ikra Shidiqi, the Usurper", "Ishai, Ojutai Dragonspeaker", "Kydele, Chosen of Kruphix",
						"Kynaios and Tiro of Meletis", "Ludevic, Necro-Alchemist", "Ravos, Soultender",
						"Saskia the Unyielding", "Silas Renn, Seeker Adept", "Vial Smasher the Fierce",
						"Yidris, Maelstrom Wielder"));

		returnMap.put("Commander 2017",
				Arrays.asList("Arahbo, Roar of the World", "Edgar Markov", "Inalla, Archmage Ritualist",
						"Kess, Dissident Mage", "Licia, Sanguine Tribune", "Mairsil, the Pretender",
						"Mathas, Fiend Seeker", "Mirri, Weatherlight Duelist", "Nazahn, Revered Bladesmith",
						"O-Kagachi, Vengeful Kami", "Ramos, Dragon Engine", "The Ur-Dragon"));

		final String dda = "Duel Decks Anthology: ";
		returnMap.put(dda + "Divine vs. Demonic", Arrays.asList("Akroma, Angel of Wrath", "Lord of the Pit"));
		returnMap.put(dda + "Elves vs. Goblins", Arrays.asList("Ambush Commander", "Siege-Gang Commander"));
		returnMap.put(dda + "Garruk vs. Liliana", Arrays.asList("Garruk Wildspeaker", "Liliana Vess"));
		returnMap.put(dda + "Jace vs. Chandra", Arrays.asList("Jace Beleren", "Chandra Nalaar"));

		final String dd = "Duel Decks: ";
		returnMap.put(dd + "Ajani vs. Nicol Bolas", Arrays.asList("Ajani Vengeant", "Nicol Bolas, Planeswalker"));
		returnMap.put(dd + "Blessed vs. Cursed", Arrays.asList("Geist of Saint Traft", "Mindwrack Demon"));
		returnMap.put(dd + "Divine vs. Demonic", Arrays.asList("Akroma, Angel of Wrath", "Lord of the Pit"));
		returnMap.put(dd + "Elspeth vs. Tezzeret", Arrays.asList("Elspeth, Knight-Errant", "Tezzeret the Seeker"));
		returnMap.put(dd + "Elves vs. Goblins", Arrays.asList("Ambush Commander", "Siege-Gang Commander"));
		returnMap.put(dd + "Garruk vs. Liliana", Arrays.asList("Garruk Wildspeaker", "Liliana Vess"));
		returnMap.put(dd + "Heroes vs. Monsters", Arrays.asList("Sun Titan", "Polukranos, World Eater"));
		returnMap.put(dd + "Izzet vs. Golgari", Arrays.asList("Jarad, Golgari Lich Lord", "Niv-Mizzet"));
		returnMap.put(dd + "Jace vs. Chandra", Arrays.asList("Jace Beleren", "Chandra Nalaar"));
		returnMap.put(dd + "Jace vs. Vraska", Arrays.asList("Jace, Architect of Thought", "Vraska the Unseen"));
		returnMap.put(dd + "Kiora vs. Elspeth", Arrays.asList("Elspeth, Sun's Champion", "Kiora, the Crashing Wave"));
		returnMap.put(dd + "Knights vs. Dragons", Arrays.asList("Knight of the Reliquary", "Bogardan Hellkite"));
		returnMap.put(dd + "Merfolk vs. Goblins", Arrays.asList("Master of Waves", "Warren Instigator"));
		returnMap.put(dd + "Mind vs. Might", Arrays.asList("Jhoira of the Ghitu", "Lovisa Coldeyes"));
		returnMap.put(dd + "Nissa vs. Ob Nixilis", Arrays.asList("Nissa, Voice of Zendikar", "Ob Nixilis Reignited"));
		returnMap.put(dd + "Phyrexia vs. The Coalition", Arrays.asList("Phyrexian Negator", "Urza's Rage"));
		returnMap.put(dd + "Sorin vs. Tibalt", Arrays.asList("Sorin, Lord of Innistrad", "Tibalt, the Fiend-Blooded"));
		returnMap.put(dd + "Speed vs. Cunning", Arrays.asList("Arcanis the Omnipotent", "Zurgo Helmsmasher"));
		returnMap.put(dd + "Venser vs. Koth", Arrays.asList("Venser, the Sojourner", "Koth of the Hammer"));
		returnMap.put(dd + "Zendikar vs. Eldrazi", Arrays.asList("Avenger of Zendikar", "Oblivion Sower"));

		returnMap.put("Magic Game Day Cards",
				Arrays.asList("Black Sun's Zenith", "Chief Engineer", "Cryptborn Horror", "Dictate of Kruphix",
						"Dungrove Elder", "Elite Inquisitor", "Firemane Avenger", "Goblin Diplomats", "Hall of Triumph",
						"Jori En, Ruin Diver", "Killing Wave", "Languish", "Magmaquake", "Melek, Izzet Paragon",
						"Mitotic Slime", "Myr Superion", "Nighthowler", "Pain Seer", "Radiant Flames",
						"Reya Dawnbringer", "Supplant Form", "Suture Priest", "Tempered Steel", "Thunderbreak Regent",
						"Utter End", "Zombie Apocalypse"));

		returnMap.put("Magic Player Rewards",
				Arrays.asList("Cryptic Command", "Damnation", "Day of Judgment", "Hypnotic Specter", "Lightning Bolt",
						"Powder Keg", "Psychatog", "Voidmage Prodigy", "Wasteland", "Wrath of God"));
		return returnMap;
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
						final String editionName = cardInfos.get(7);
						final String cardName = cardInfos.get(1);

						if (nonFoilEditions.contains(editionName) || editionName.startsWith("Duel Decks")) {
							if (justFoilCardsOfEditions.containsKey(editionName)) {
								magicCards.add(new MagicCard(cardInfos,
										justFoilCardsOfEditions.get(editionName).contains(cardName)));
							} else {
								magicCards.add(new MagicCard(cardInfos, false));
							}
						} else if (justFoilEditions.contains(editionName) || editionName.startsWith("From the Vault")) {
							magicCards.add(new MagicCard(cardInfos, true));
						} else {
							magicCards.add(new MagicCard(cardInfos, false));
							magicCards.add(new MagicCard(cardInfos, true));
						}

						cardInfos.clear();
					}
				}
			}

			if (!edition.getName().equals("Limited Edition Alpha") && !edition.getName().equals("Starter 1999")) {

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
