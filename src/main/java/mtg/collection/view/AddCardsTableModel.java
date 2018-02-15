package mtg.collection.view;

import java.util.Collection;

import javax.swing.table.AbstractTableModel;

import mtg.collection.CollectionManager;
import mtg.collection.MagicCard;
import mtg.collection.editions.EditionsController;

public class AddCardsTableModel extends AbstractTableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	String[] columnNames = { "Number", "English Name", "Portuguese Name", "Type", "Mana", "Rarity", "Edition",
			"Quantity" };
	Object[][] data = readData();

	private Object[][] readData() {
		Collection<MagicCard> allCards = EditionsController.getInstance().getAllCards();
		int rows = allCards.size();
		int cols = columnNames.length;
		final Object[][] data = new Object[rows][cols];
		int i = 0;

		for (final MagicCard card : allCards) {
			data[i][0] = card.getNumber();
			data[i][1] = card.getEnName();
			data[i][2] = card.getPtName();
			data[i][3] = card.getType();
			data[i][4] = card.getMana();
			data[i][5] = card.getRarity();
			data[i][6] = card.getEdition();
			data[i++][7] = CollectionManager.getQuantity(card);
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

	public void updateCell(String enName, String edition, String quantity) {
		for (int i = 0; i < data.length; i++) {
			if (data[i][1].equals(enName) && data[i][6].equals(edition)) {
				data[i][7] = quantity;
				fireTableCellUpdated(i, 7);
				return;
			}
		}

	}

}
