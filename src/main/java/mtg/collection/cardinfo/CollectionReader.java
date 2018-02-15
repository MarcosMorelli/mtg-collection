package mtg.collection.cardinfo;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import com.fasterxml.jackson.databind.ObjectMapper;

import mtg.collection.MagicCard;
import mtg.collection.editions.Editions;

public class CollectionReader {

	public static MagicCard[] readCollection(Editions edition) {
		final String fileName = "editions/" + edition.name().replaceAll("_", "");
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.readValue(
					new ByteArrayInputStream(
							FileUtils.readFileToString(new File(fileName), Charset.defaultCharset()).getBytes("UTF-8")),
					MagicCard[].class);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static void writeCollection(final MagicCard[] list, final Editions edition) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(list);
		FileUtils.write(new File("editions/" + edition.name().replaceAll("_", "")), json, Charset.defaultCharset());
	}

	public static void fetchCollection(Editions edition) throws IOException {
		final String fileName = "editions/" + edition.name().replaceAll("_", "");
		if (FileUtils.getFile(fileName).exists()) {
			return;
		}

		List<MagicCard> magicCards = new ArrayList<MagicCard>();
		List<String> cardInfos = new ArrayList<String>();

		ChromeDriver driver = new ChromeDriver();
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		driver.manage().window().maximize();

		String link = "http://magiccards.info/query?q=e%3A" + edition.name().replaceAll("_", "")
				+ "%2Fen&v=list&s=cname";
		driver.get(link);

		WebElement table = driver.findElement(By.xpath("/html/body/table[3]"));
		List<WebElement> trs = table.findElements(By.tagName("tr"));
		for (WebElement tr : trs) {
			List<WebElement> tds = tr.findElements(By.tagName("td"));
			for (WebElement td : tds) {
				cardInfos.add(td.getText());

				if (cardInfos.size() == 2) {
					cardInfos.add(td.findElement(By.tagName("a")).getAttribute("href"));
				} else if (cardInfos.size() == 8) {
					if (!cardInfos.get(7).startsWith("From the Vault") && !cardInfos.get(7).equals("Arena League")
							&& !cardInfos.get(7).equals("Media Inserts")
							&& !cardInfos.get(7).equals("Zendikar Expeditions")
							&& !cardInfos.get(7).equals("Kaladesh Inventions") && !cardInfos.get(7).equals("Grand Prix")
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
			for (WebElement tr : trs) {
				List<WebElement> tds = tr.findElements(By.tagName("td"));

				tds: for (WebElement td : tds) {
					cardInfos.add(td.getText());

					if (cardInfos.size() == 2) {
						for (final MagicCard card : magicCards) {
							if (card.getNumber().equals(cardInfos.get(0))) {
								card.setPtName(cardInfos.get(1));
								break tds;
							}
						}
					}

				}
			}
		} catch (final NoSuchElementException editionDontHavePtLang) {
		}

		driver.quit();

		ObjectMapper mapper = new ObjectMapper();
		String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(magicCards);
		FileUtils.write(new File(fileName), json, Charset.defaultCharset());
	}

}
