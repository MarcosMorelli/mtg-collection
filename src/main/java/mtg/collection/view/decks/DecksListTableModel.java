package mtg.collection.view.decks;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.table.AbstractTableModel;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;

public class DecksListTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;

	private final List<String> columnNames;
	private ConcurrentHashMap<Integer, Object[]> data;

	public static final String DECK_NAME = "Deck Name";

	public DecksListTableModel() {
		columnNames = Arrays.asList(DECK_NAME);
		data = readData();
	}

	public int getColumnIndex(final String column) {
		return columnNames.indexOf(column);
	}

	private ConcurrentHashMap<Integer, Object[]> readData() {
		final ConcurrentHashMap<Integer, Object[]> data = new ConcurrentHashMap<Integer, Object[]>();
		final Collection<File> deckList = FileUtils.listFiles(new File("decks"), TrueFileFilter.INSTANCE,
				TrueFileFilter.INSTANCE);
		deckList.forEach(deckFile -> {
			data.put(data.size(), Arrays.asList(deckFile.getName()).toArray());
		});
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
	
	public void addRow(final Object[] value) {
		data.put(data.size(), value);
		fireTableDataChanged();
	}

	public void removeRow(final int row) {
		data.remove(row);
		
		ConcurrentHashMap<Integer, Object[]> tempData = new ConcurrentHashMap<Integer, Object[]>();
		data.values().forEach(obj -> {
			tempData.put(tempData.size(), obj);
		});
		
		data = tempData;
		fireTableDataChanged();
	}
}
