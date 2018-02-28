package mtg.collection.view.prices;

import java.io.File;
import java.sql.Date;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.table.AbstractTableModel;

import mtg.collection.editions.Editions;

public class PricesTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;

	private final List<String> columnNames;
	private final ConcurrentHashMap<Integer, Object[]> data;

	public static final String EN_NAME = "English Name";
	public static final String LAST_UPDATE = "Last Update";

	public PricesTableModel() {
		columnNames = Arrays.asList(EN_NAME, LAST_UPDATE);
		data = readData();
	}

	public int getColumnIndex(final String column) {
		return columnNames.indexOf(column);
	}

	private ConcurrentHashMap<Integer, Object[]> readData() {
		Editions[] editions = Editions.values();

		final ConcurrentHashMap<Integer, Object[]> data = new ConcurrentHashMap<Integer, Object[]>();
		int i = 0;

		for (final Editions edition : editions) {
			data.put(Integer.valueOf(i++), Arrays
					.asList(edition.getName(), new Date(new File(edition.getFileName()).lastModified())).toArray());
		}

		return data;
	}

	@Override
	public int getRowCount() {
		return data.size();
	}

	@Override
	public int getColumnCount() {
		return columnNames.size();
	}

	@Override
	public Object getValueAt(final int row, final int column) {
		if (data.containsKey(Integer.valueOf(row))) {
			return data.get(Integer.valueOf(row))[column];
		}
		return null;
	}

	@Override
	public String getColumnName(final int column) {
		return columnNames.get(column);
	}

	@Override
	public boolean isCellEditable(final int row, final int column) {
		return false;
	}

	public void removeRow(final int row) {
		data.remove(Integer.valueOf(row));
	}
}
