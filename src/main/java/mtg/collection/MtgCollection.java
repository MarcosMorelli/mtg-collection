package mtg.collection;

import java.awt.EventQueue;
import java.io.IOException;

import org.openqa.selenium.chrome.ChromeDriverService;

import mtg.collection.collection.CollectionController;
import mtg.collection.editions.EditionsController;
import mtg.collection.view.MainWindow;

public class MtgCollection {

	public static void main(String[] args) throws IOException {
		if (System.getProperty("os.name").equals("Linux")) {
			System.setProperty(ChromeDriverService.CHROME_DRIVER_EXE_PROPERTY, "/home/morelli/drivers/chromedriver");
		}
		
		EditionsController.getInstance().fetchEditionsInfo();
		EditionsController.getInstance().readEditions();
		EditionsController.getInstance().writeAtTranslateFile();
		EditionsController.getInstance().fillPtNames();
		
		CollectionController.readCollection();
		
		//CollectionController.writeHtmlFiles();
		
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
