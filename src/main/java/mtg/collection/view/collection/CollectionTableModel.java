package mtg.collection.view.collection;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import mtg.collection.collection.CollectionController;
import mtg.collection.collection.CollectionEntry;
import mtg.collection.editions.EditionsController;
import mtg.collection.editions.MagicCard;

public class CollectionTableModel extends AbstractTableModel {

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

	public CollectionTableModel() {
		columnNames = Arrays.asList(EN_NAME, PT_NAME, TYPE, MANA, RARITY, EDITION, PRICE, QUANTITY);
		data = readData();
	}

	public int getColumnIndex(final String column) {
		return columnNames.indexOf(column);
	}

	private Object[][] readData() {
		final Collection<CollectionEntry> collection = CollectionController.getIndividualEntrys();
		final int rows = collection.size();
		final int cols = columnNames.size();
		final Object[][] data = new Object[rows][cols];

		int i = 0;
		for (final CollectionEntry entry : collection) {
			final MagicCard card = EditionsController.getInstance().getCard(entry.enName, entry.edition);
			
			int j = 0;
			try {
			data[i][j++] = card.getEnName();
			data[i][j++] = card.getPtName();
			data[i][j++] = card.getType();
			data[i][j++] = card.getMana();
			data[i][j++] = card.getRarity();
			data[i][j++] = card.getEdition();
			data[i][j++] = card.getPrice();
			data[i++][j] = CollectionController.getQuantityConsiderEdition(card);
			} catch (Exception e) {
				System.err.println(entry.toString());
				e.printStackTrace();
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

}
