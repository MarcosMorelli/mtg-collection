package mtg.collection.view;

import java.util.Collection;

import javax.swing.table.AbstractTableModel;

import mtg.collection.MagicCard;
import mtg.collection.collection.CollectionManager;
import mtg.collection.editions.EditionsController;

public class AddCardsTableModel extends AbstractTableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	String[] columnNames = { "English Name", "Portuguese Name", "Type", "Mana", "Rarity", "Edition", "Quantity" };
	Object[][] data = readData();

	private Object[][] readData() {
		Collection<MagicCard> allCards = EditionsController.getInstance().getAllCards();
		int rows = allCards.size();
		int cols = columnNames.length;
		final Object[][] data = new Object[rows][cols];
		int i = 0;

		for (final MagicCard card : allCards) {
			data[i][0] = card.getEnName();
			data[i][1] = card.getPtName();
			data[i][2] = card.getType();
			data[i][3] = card.getMana();
			data[i][4] = card.getRarity();
			data[i][5] = card.getEdition();
			data[i++][6] = CollectionManager.getQuantity(card);
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
			if (data[i][0].equals(enName) && data[i][5].equals(edition)) {
				data[i][6] = quantity;
				fireTableCellUpdated(i, 6);
				return;
			}
		}

	}

}
