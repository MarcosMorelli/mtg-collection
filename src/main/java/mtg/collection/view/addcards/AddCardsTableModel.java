package mtg.collection.view.addcards;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import mtg.collection.collection.CollectionController;
import mtg.collection.editions.EditionsController;
import mtg.collection.editions.MagicCard;

public class AddCardsTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;
	
	private final List<String> columnNames;
	private final Object[][] data;
	
	public static final String EN_NAME = "English Name";
	public static final String PT_NAME = "Portuguese Name";
	public static final String TYPE = "Type";
	public static final String MANA = "Mana";
	public static final String RARITY = "Rarity";
	public static final String EDITION = "Edition";
	public static final String PRICE = "Price";
	public static final String QUANTITY = "Quantity";

	public AddCardsTableModel() {
		columnNames = Arrays.asList(EN_NAME, PT_NAME, TYPE, MANA, RARITY, EDITION, PRICE, QUANTITY);
		data = readData();
	}
	
	public int getColumnIndex(final String column) {
		return columnNames.indexOf(column);
	}

	private Object[][] readData() {
		final Collection<MagicCard> allCards = EditionsController.getInstance().getAllCards();
		final int rows = allCards.size();
		final int cols = columnNames.size();
		final Object[][] data = new Object[rows][cols];
		int i = 0;

		for (final MagicCard card : allCards) {
			int j = 0;
			data[i][j++] = card.getEnName();
			data[i][j++] = card.getPtName();
			data[i][j++] = card.getType();
			data[i][j++] = card.getMana();
			data[i][j++] = card.getRarity();
			data[i][j++] = card.getEdition();
			data[i][j++] = card.getPrice();
			data[i++][j] = CollectionController.getQuantity(card);
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

	public void updateCell(final String enName, final String edition, final String quantity) {
		final int enNameIndex = getColumnIndex(EN_NAME);
		final int editionIndex = getColumnIndex(EDITION);
		final int quantityIndex = getColumnIndex(QUANTITY);

		for (int i = 0; i < data.length; i++) {
			if (data[i][enNameIndex].equals(enName) && data[i][editionIndex].equals(edition)) {
				data[i][quantityIndex] = quantity;
				fireTableCellUpdated(i, quantityIndex);
				return;
			}
		}

	}

}
