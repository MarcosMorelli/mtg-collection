package mtg.collection.view.editions;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import mtg.collection.collection.CollectionController;
import mtg.collection.editions.Editions;
import mtg.collection.editions.EditionsController;
import mtg.collection.editions.MagicCard;

public class EditionsTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;

	private final List<String> columnNames;
	private final Object[][] data;
	private int i;
	private int singles;
	private int total;

	public static final String EDITION_NAME = "Edition Name";
	public static final String EDITION_SIZE = "Edition Size";
	public static final String SINGLES = "Singles";
	public static final String TOTAL = "Total";

	public EditionsTableModel() {
		columnNames = Arrays.asList(EDITION_NAME, EDITION_SIZE, SINGLES, TOTAL);
		data = readData();
	}

	public int getColumnIndex(final String column) {
		return columnNames.indexOf(column);
	}

	private Object[][] readData() {
		final List<Editions> editionsList = Arrays.asList(Editions.values());

		final int rows = editionsList.size();
		final int cols = columnNames.size();
		final Object[][] data = new Object[rows][cols];

		i = 0;
		editionsList.forEach(edition -> {
			final List<MagicCard> cards = EditionsController.getInstance().getEditionCards(edition);

			HashSet<String> set = new HashSet<String>();
			total = 0;
			singles = 0;
			cards.forEach(card -> {
				final String key = card.getEnName().replaceAll(" \\(.*", "");
				final int quantity = Integer.parseInt(CollectionController.getQuantity(card.getEnName(), false));

				if (quantity > 0 && !set.contains(key)) {
					total += quantity;
					singles++;
				}

				set.add(key);
			});

			int j = 0;
			data[i][j++] = edition.getName();
			data[i][j++] = set.size() + " / " + (set.size() * 4);
			data[i][j++] = singles;
			data[i++][j] = total;
		});

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
