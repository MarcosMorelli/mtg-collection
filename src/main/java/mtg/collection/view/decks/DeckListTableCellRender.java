package mtg.collection.view.decks;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import mtg.collection.scg.SCGUtil;

public class DeckListTableCellRender extends DefaultTableCellRenderer {

	private static final long serialVersionUID = 1L;

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {

		final DeckTableModel model = (DeckTableModel) table.getModel();
		
		final String cardName = (String) model.getValueAt(row, model.getColumnIndex(DeckTableModel.CARD_NAME));
		boolean isBasicLand = false;
				
		for (int i = 0; i < SCGUtil.BASIC_LANDS.size(); i++) {
			if (cardName.equalsIgnoreCase(SCGUtil.BASIC_LANDS.get(i))) {
				isBasicLand = true;
				break;
			}
		}
		
		final int quantityNeeded = Integer
				.valueOf((String) model.getValueAt(row, model.getColumnIndex(DeckTableModel.QTD)));
		final int collection = Integer
				.valueOf((String) model.getValueAt(row, model.getColumnIndex(DeckTableModel.COLLECTION)));

		if (collection >= quantityNeeded || isBasicLand) {
			setBackground(Color.WHITE);
		} else {
			setBackground(Color.YELLOW);
		}

		setFont(getFont().deriveFont(Font.PLAIN));
		setText(value != null ? value.toString() : "");
		return this;
	}

}
