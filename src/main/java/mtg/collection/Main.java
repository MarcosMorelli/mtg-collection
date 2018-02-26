package mtg.collection;

import java.awt.EventQueue;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.swing.UIManager;

import org.openqa.selenium.chrome.ChromeDriverService;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.jgoodies.looks.windows.WindowsLookAndFeel;

import mtg.collection.collection.CollectionController;
import mtg.collection.editions.Editions;
import mtg.collection.editions.EditionsController;
import mtg.collection.scg.SCGReader;
import mtg.collection.view.MainWindow;

public class Main {

	public static void main(String[] args)
			throws JsonParseException, JsonMappingException, UnsupportedEncodingException, IOException {
		if (System.getProperty("os.name").equals("Linux")) {
			System.setProperty(ChromeDriverService.CHROME_DRIVER_EXE_PROPERTY, "/home/marcos/drivers/chromedriver");
		}

		EditionsController.getInstance().fetchEditionsInfo();
		EditionsController.getInstance().readEditions();
		CollectionController.readCollection();

		ConcurrentLinkedQueue<Editions> editionsList = new ConcurrentLinkedQueue<Editions>();
		//editionsList.add(Editions.c13);
		/*Arrays.asList(Editions.values()).forEach(edition -> {
			editionsList.add(edition);
		});*/

		SCGReader reader = new SCGReader(1, editionsList);
		reader.start();

		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					if (!System.getProperty("os.name").equals("Linux")) {
						UIManager.setLookAndFeel(new WindowsLookAndFeel());
					}
					new MainWindow();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

}
