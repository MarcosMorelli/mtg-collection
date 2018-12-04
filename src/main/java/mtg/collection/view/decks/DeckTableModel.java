package mtg.collection.view.decks;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.table.AbstractTableModel;

import org.apache.commons.io.FileUtils;

import mtg.collection.collection.CollectionController;
import mtg.collection.editions.EditionsController;
import mtg.collection.editions.MagicCard;

public class DeckTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;

	private final List<String> columnNames;
	private ConcurrentHashMap<Integer, Object[]> data;

	public static final String QTD = "Qtd";
	public static final String CARD_NAME = "Card Name";
	public static final String TYPE = "Type";
	public static final String COLLECTION = "Col";

	private final boolean side;

	public DeckTableModel(final String deckName, final boolean side) {
		columnNames = Arrays.asList(QTD, CARD_NAME, TYPE, COLLECTION);
		this.side = side;
		try {
			data = readData(
					FileUtils.readLines(new File("decks/" + deckName), Charset.defaultCharset()).toArray());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public DeckTableModel(final String[] deckList, final boolean side) {
		columnNames = Arrays.asList(QTD, CARD_NAME, TYPE, COLLECTION);
		this.side = side;
		data = readData(deckList);
	}

	public int getColumnIndex(final String column) {
		return columnNames.indexOf(column);
	}

	private ConcurrentHashMap<Integer, Object[]> readData(final Object[] deckList) {
		final ConcurrentHashMap<Integer, Object[]> data = new ConcurrentHashMap<Integer, Object[]>();
		for (int i = 0; i < deckList.length; i++) {
			String line = (String) deckList[i];
			
			if (line.length() < 2) {
				continue;
			}
			
			String[] tokens = line.split("[ ]+");
			String firstToken = tokens[side ? 1 : 0];

			try {
				Integer.valueOf(firstToken);
			} catch (final NumberFormatException ignored) {
				continue;
			}

			final String cardName = line.substring(line.indexOf(firstToken) + firstToken.length()).trim().replaceAll("’", "'");
			final MagicCard card = EditionsController.getInstance().getCard(cardName);

			data.put(data.size(), Arrays.asList(firstToken, cardName, card == null ? "" : card.getType(),
					CollectionController.getQuantity(cardName, false)).toArray());
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
