package mtg.collection.scg;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.fasterxml.jackson.databind.ObjectMapper;

import mtg.collection.editions.Editions;
import mtg.collection.editions.EditionsController;

public class SCGThread implements Runnable {

	private final Editions edition;
	private final SCGUtil util;

	private boolean running = false;
	private boolean finished = false;
	private int pageNumber = 1;
	private String message = "";

	public SCGThread(final Editions edition) {
		this.edition = edition;
		util = new SCGUtil(edition);
	}

	public void run() {
		running = true;
		WebDriver driver = null;
		try {
			final ArrayList<SCGCard> cardsList = new ArrayList<SCGCard>();
			driver = util.getChromeDriver();

			for (int i = 0; i < edition.getScgPromoNamesListSize(); i++) {
				driver.get(edition.getScgLink(i));

				while (util.isNextPage(driver)) {
					message = edition.getName() + " na pagina " + pageNumber++;
					cardsList.addAll(readSCGEditionPage(driver));
					util.clickAtNextPage(driver);
				}
				
				message = edition.getName() + " na pagina " + pageNumber++;
				cardsList.addAll(readSCGEditionPage(driver));
			}

			updateCollectionPrices(cardsList);

			EditionsController.getInstance().writeEditions();
			updateEditionPriceDate();
		} catch (final Exception e) {
			message = e.getMessage();
		} finally {
			if (driver != null) {
				driver.quit();
			}
			finished = true;
		}
	}

	public String getMessage() {
		return message;
	}

	public boolean isrRunning() {
		return running;
	}

	public boolean isFinished() {
		return finished;
	}

	private ArrayList<SCGCard> readSCGEditionPage(final WebDriver driver)
			throws IOException, InterruptedException, NoSuchElementException {
		final ArrayList<SCGCard> cardsLists = new ArrayList<SCGCard>();

		final List<WebElement> linhas = util.getLinesFromTable(driver, edition);
		for (final WebElement linha : linhas) {
			SCGCard card = new SCGCard();
			
			card.name = util.getCardName(linha.findElement(By.className("search_results_1")).getText());
			card.foil = util.isFoil(card.name, linha.findElement(By.className("search_results_2")).getText());
			card.price = util.getPrice(driver, linha.findElement(By.className("search_results_9")));

			cardsLists.add(card);
		}

		return cardsLists;
	}

	private void updateCollectionPrices(final ArrayList<SCGCard> cardsList) {
		for (final SCGCard card : cardsList) {
			EditionsController.getInstance().setScgPrice(edition, card);
		}
	}

	private void updateEditionPriceDate() {
		final ObjectMapper mapper = new ObjectMapper();
		final File editionsPricesFile = new File("editionsPriceDate.json");

		final SCGEditionPriceDate date = new SCGEditionPriceDate();
		date.editionName = edition.getName();
		date.time = new Date().getTime();

		try {
			if (!editionsPricesFile.exists()) {
				String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(Arrays.asList(date));
				FileUtils.write(editionsPricesFile, json, Charset.defaultCharset());
				return;
			}

			final ArrayList<SCGEditionPriceDate> list = new ArrayList<SCGEditionPriceDate>();
			list.addAll(Arrays.asList(mapper.readValue(
					new ByteArrayInputStream(
							FileUtils.readFileToString(editionsPricesFile, Charset.defaultCharset()).getBytes("UTF-8")),
					SCGEditionPriceDate[].class)));

			if (list.contains(date)) {
				list.remove(date);
			}

			list.add(date);

			String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(list);
			FileUtils.write(editionsPricesFile, json, Charset.defaultCharset());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
