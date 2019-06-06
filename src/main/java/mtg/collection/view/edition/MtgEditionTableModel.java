package mtg.collection.view.edition;

import java.util.Arrays;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import mtg.collection.collection.CollectionController;
import mtg.collection.editions.EditionsController;
import mtg.collection.editions.MagicCard;

public class MtgEditionTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;

	private final List<String> columnNames;
	private final Object[][] data;

	public static final String EN_NAME = "English Name";
	public static final String PT_NAME = "Portuguese Name";
	public static final String TYPE = "Type";
	public static final String MANA = "Mana";
	public static final String RARITY = "Rarity";
	public static final String EDITION = "Edition";
	public static final String QUANTITY = "Quantity";

	public MtgEditionTableModel(final String edition) {
		columnNames = Arrays.asList(EN_NAME, PT_NAME, TYPE, MANA, RARITY, QUANTITY);
		data = readData(edition);
	}

	public int getColumnIndex(final String column) {
		return columnNames.indexOf(column);
	}

	private Object[][] readData(final String edition) {
		final List<MagicCard> cards = EditionsController.getInstance().getEditionCards(edition);
		int rows = cards.size();
		int cols = columnNames.size();

		final Object[][] data = new Object[rows][cols];

		int i = 0;
		for (final MagicCard card : cards) {
			int j = 0;
			data[i][j++] = card.getEnName();
			data[i][j++] = card.getPtName();
			data[i][j++] = card.getType();
			data[i][j++] = card.getMana();
			data[i][j++] = card.getRarity();
			data[i++][j] = CollectionController.getQuantity(card.getEnName(), true);
		}

		return data;
	}

	@Override
	public int getRowCount() {
		return data.length;
	}

	@Override
	public int getColumnCount() {
		return columnNames.size();
	}

	@Override
	public Object getValueAt(final int row, final int column) {
		return data[row][column];
	}

	@Override
	public String getColumnName(final int column) {
		return columnNames.get(column);
	}

	@Override
	public boolean isCellEditable(final int row, final int column) {
		return false;
	}

	public void updateCell(final String enName, final String quantity) {
		for (int i = 0; i < data.length; i++) {
			if (data[i][getColumnIndex(EN_NAME)].equals(enName)) {
				data[i][getColumnIndex(QUANTITY)] = quantity;
				fireTableCellUpdated(i, getColumnIndex(QUANTITY));
				return;
			}
		}
	}
}
