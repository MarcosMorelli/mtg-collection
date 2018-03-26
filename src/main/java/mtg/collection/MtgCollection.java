package mtg.collection;

import java.awt.EventQueue;

import javax.swing.UIManager;

import org.openqa.selenium.chrome.ChromeDriverService;

import com.jgoodies.looks.windows.WindowsLookAndFeel;

import mtg.collection.collection.CollectionController;
import mtg.collection.editions.EditionsController;
import mtg.collection.view.MainWindow;

public class MtgCollection {

	public static void main(String[] args) {
		if (System.getProperty("os.name").equals("Linux")) {
			System.setProperty(ChromeDriverService.CHROME_DRIVER_EXE_PROPERTY, "/home/marcos/drivers/chromedriver");
		}
		
		EditionsController.getInstance().fetchEditionsInfo();
		EditionsController.getInstance().readEditions();
		EditionsController.getInstance().fillPtNames();
		CollectionController.readCollection();

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
