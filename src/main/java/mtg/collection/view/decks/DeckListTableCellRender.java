package mtg.collection.view.decks;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class DeckListTableCellRender extends DefaultTableCellRenderer {

	private static final long serialVersionUID = 1L;

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {

		final DeckTableModel model = (DeckTableModel) table.getModel();
		final int quantityNeeded = Integer
				.valueOf((String) model.getValueAt(row, model.getColumnIndex(DeckTableModel.QTD)));
		final int collection = Integer
				.valueOf((String) model.getValueAt(row, model.getColumnIndex(DeckTableModel.COLLECTION)));

		if (quantityNeeded > collection) {
			setBackground(Color.YELLOW);
		} else {
			setBackground(Color.WHITE);
		}

		setFont(getFont().deriveFont(Font.PLAIN));
		setText(value != null ? value.toString() : "");
		return this;
	}

}
