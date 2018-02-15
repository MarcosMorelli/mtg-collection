package mtg.collection.view;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import javax.swing.table.AbstractTableModel;

import org.apache.commons.io.FileUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import mtg.collection.CollectionManager;
import mtg.collection.MagicCard;
import mtg.collection.Main;

public class MtgEditionTableModel extends AbstractTableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	String[] columnNames = { "Number", "English Name", "Portuguese Name", "Type", "Mana", "Rarity", "Quantity" };
	Object[][] data = readData();

	private Object[][] readData() {
		try {
			ObjectMapper mapper = new ObjectMapper();
			MagicCard[] cards = mapper.readValue(new ByteArrayInputStream(
					FileUtils.readFileToString(new File("editions\\" + Main.selectedEdition), Charset.defaultCharset())
							.getBytes("UTF-8")),
					MagicCard[].class);

			int rows = cards.length;
			int cols = columnNames.length;

			Object[][] data = new Object[rows][cols];

			int i = 0;
			for (MagicCard card : cards) {
				data[i][0] = card.getNumber();
				data[i][1] = card.getEnName();
				data[i][2] = card.getPtName();
				data[i][3] = card.getType();
				data[i][4] = card.getMana();
				data[i][5] = card.getRarity();
				data[i++][6] = CollectionManager.getQuantity(card.getEnName());
			}

			return data;
		} catch (IOException e1) {
			return null;
		}

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

	public void updateCell(String enName, String quantity) {
		for (int i = 0; i < data.length; i++) {
			if (data[i][1].equals(enName)) {
				data[i][6] = quantity;
				fireTableCellUpdated(i, 6);
				return;
			}
		}
	}
}
