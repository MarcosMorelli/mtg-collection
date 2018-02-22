package mtg.collection.view;

import java.util.List;

import javax.swing.table.AbstractTableModel;

import mtg.collection.MagicCard;
import mtg.collection.collection.CollectionManager;
import mtg.collection.editions.EditionsController;

public class MtgEditionTableModel extends AbstractTableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final static String[] columnNames = { "English Name", "Portuguese Name", "Type", "Mana", "Rarity",
			"Quantity" };

	private Object[][] data;

	public MtgEditionTableModel(final String edition) {
		data = readData(edition);
	}

	private Object[][] readData(final String edition) {
		final List<MagicCard> cards = EditionsController.getInstance().getEditionCards(edition);
		int rows = cards.size();
		int cols = columnNames.length;

		final Object[][] data = new Object[rows][cols];

		int i = 0;
		for (final MagicCard card : cards) {
			data[i][0] = card.getEnName();
			data[i][1] = card.getPtName();
			data[i][2] = card.getType();
			data[i][3] = card.getMana();
			data[i][4] = card.getRarity();
			data[i++][5] = CollectionManager.getQuantity(card.getEnName());
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
	public Object getValueAt(final int row, final int column) {
		return data[row][column];
	}

	@Override
	public String getColumnName(final int column) {
		return columnNames[column];
	}

	@Override
	public boolean isCellEditable(final int row, final int column) {
		return false;
	}

	public void updateCell(final String enName, final String quantity) {
		for (int i = 0; i < data.length; i++) {
			if (data[i][0].equals(enName)) {
				data[i][5] = quantity;
				fireTableCellUpdated(i, 5);
				return;
			}
		}
	}
}
