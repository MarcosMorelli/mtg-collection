package mtg.collection.view.prices;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.table.AbstractTableModel;

import org.apache.commons.io.FileUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import mtg.collection.editions.Editions;
import mtg.collection.scg.SCGEditionPriceDate;

public class PricesTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;

	private final List<String> columnNames;
	private ConcurrentHashMap<Integer, Object[]> data;

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

		final ObjectMapper mapper = new ObjectMapper();
		final File editionsPricesFile = new File("editionsPriceDate.json");

		List<SCGEditionPriceDate> list;
		try {
			list = Arrays.asList(mapper.readValue(
					new ByteArrayInputStream(
							FileUtils.readFileToString(editionsPricesFile, Charset.defaultCharset()).getBytes("UTF-8")),
					SCGEditionPriceDate[].class));
		} catch (IOException e) {
			list = new ArrayList<SCGEditionPriceDate>();
			e.printStackTrace();
		}

		final ConcurrentHashMap<Integer, Object[]> data = new ConcurrentHashMap<Integer, Object[]>();
		int i = 0;

		for (final Editions edition : editions) {
			SCGEditionPriceDate date = new SCGEditionPriceDate();
			date.editionName = edition.getName();

			String lastUpdated = new String();
			if (list.contains(date)) {
				date = list.get(list.indexOf(date));
				lastUpdated = new SimpleDateFormat().format(new Date(date.time));
			}

			data.put(Integer.valueOf(i++), Arrays.asList(edition.getName(), lastUpdated).toArray());
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
