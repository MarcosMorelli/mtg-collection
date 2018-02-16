package mtg.collection;

import java.awt.EventQueue;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.swing.UIManager;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.jgoodies.looks.windows.WindowsLookAndFeel;

import mtg.collection.collection.CollectionManager;
import mtg.collection.editions.EditionsController;
import mtg.collection.view.MainWindow;

public class Main {

	public static String selectedEdition;

	public static void main(String[] args) throws JsonParseException, JsonMappingException, UnsupportedEncodingException, IOException {
		//System.setProperty(ChromeDriverService.CHROME_DRIVER_EXE_PROPERTY, "/home/marcos/drivers/chromedriver");
		
		EditionsController.getInstance().fetchEditionsInfo();
		CollectionManager.readCollection();
		
		//CollectionManager.migrateCollection();
		//CollectionManager.writeCollection2();

		/*ConcurrentLinkedQueue<Editions> editionsList = new ConcurrentLinkedQueue<Editions>();
		//editionsList.add(Editions.lw);
		//editionsList.add(Editions.kld);

		SCGReader reader = new SCGReader(2, editionsList);
		reader.start();
*/
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					UIManager.setLookAndFeel(new WindowsLookAndFeel());
					new MainWindow();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

}
