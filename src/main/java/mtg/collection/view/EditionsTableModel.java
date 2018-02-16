package mtg.collection.view;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.Predicate;

import javax.swing.table.AbstractTableModel;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import mtg.collection.MagicCard;
import mtg.collection.collection.CollectionManager;

public class EditionsTableModel extends AbstractTableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	String[] columnNames = { "Edition Name", "Edition Code", "Edition Size", "Collection" };
	Object[][] data = readData();

	private Object[][] readData() {
		Collection<File> editionsList = FileUtils.listFiles(new File("editions"), TrueFileFilter.INSTANCE, null);
		int rows = editionsList.size();
		int cols = columnNames.length;

		Object[][] data = new Object[rows][cols];

		ObjectMapper mapper = new ObjectMapper();
		int i = 0;
		for (File editionFile : editionsList) {
			try {
				MagicCard[] cards = mapper.readValue(
						new ByteArrayInputStream(
								FileUtils.readFileToString(editionFile, Charset.defaultCharset()).getBytes("UTF-8")),
						MagicCard[].class);

				ArrayList<MagicCard> cardsList = new ArrayList<>(Arrays.asList(cards));
				Predicate<MagicCard> basicLands = (MagicCard card) -> card.getEnName().equals("Forest")
						|| card.getEnName().equals("Swamp") || card.getEnName().equals("Plains")
						|| card.getEnName().equals("Mountain") || card.getEnName().equals("Island");
				cardsList.removeIf(basicLands);

				String edition = cardsList.get(0).getEdition();

				int total = 0;
				for (MagicCard card : cardsList) {
					total += Integer.parseInt(CollectionManager.getQuantity(card.getEnName()));
				}

				data[i][0] = edition;
				data[i][1] = editionFile.toString().replace("editions\\", "");
				data[i][2] = cardsList.size() / 2;
				data[i++][3] = total + " / " + cardsList.size() * 2;
			} catch (IOException e) {
				e.printStackTrace();
			} catch (IndexOutOfBoundsException e) {
				System.err.println(editionFile.getName());
			}

		}

		return data;
	}

	@Override
	public int getRowCount() {
		return data.length;
	}

	@Override
	public int getColumnCount() {
		return columnNames.length;
	}

	@Override
	public Object getValueAt(int row, int column) {
		return data[row][column];
	}

	@Override
	public String getColumnName(int column) {
		return columnNames[column];
	}

	@Override
	public boolean isCellEditable(int row, int column) {
		return false;
	}

}
