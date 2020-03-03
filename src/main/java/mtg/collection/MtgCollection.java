package mtg.collection;

import java.awt.EventQueue;
import java.io.IOException;

import org.openqa.selenium.chrome.ChromeDriverService;

import com.neovisionaries.ws.client.WebSocketException;

import mtg.collection.collection.CollectionController;
import mtg.collection.editions.EditionsController;
import mtg.collection.html.HtmlCollectionWriter;
import mtg.collection.view.MainWindow;

public class MtgCollection {

	public static void main(String[] args) throws IOException, WebSocketException, InterruptedException {
		if (System.getProperty("os.name").equals("Linux")) {
			System.setProperty(ChromeDriverService.CHROME_DRIVER_EXE_PROPERTY, "/home/morelli/drivers/chromedriver");
		} else {
			System.setProperty(ChromeDriverService.CHROME_DRIVER_EXE_PROPERTY, "./chromedriver");
		}
		/*
		Editions edition = Editions.un;
		SCGUtil util = new SCGUtil(edition);
		WebDriver driver = util.getChromeDriver();
		
		driver.get("https://starcitygames.com/shop/singles/english/commander-2016-edition/?sort=alphaasc&page=34");
		
		List<WebElement> lines = util.getLinesFromTable(driver, edition);

		for (final WebElement linha : lines) {
			SCGCard card = new SCGCard();
			
			card.name = util.getCardName(linha.findElement(By.className("search_results_1")).getText());
			card.foil = util.isFoil(card.name, linha.findElement(By.className("search_results_2")).getText());
			card.price = util.getPrice(driver, linha.findElement(By.className("search_results_9")));
			
			System.out.println(card);
		}
		
		
		//driver.quit();
		*/
		CollectionController.readCollection();
		
		EditionsController.getInstance().fetchEditionsInfo();
		EditionsController.getInstance().readEditions();
		EditionsController.getInstance().writeAtTranslateFile();
		EditionsController.getInstance().fillPtNames();
				
		HtmlCollectionWriter.writeFiles();
				
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					new MainWindow();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

}
