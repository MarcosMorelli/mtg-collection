package mtg.collection.view;

import java.awt.BorderLayout;
import java.util.Set;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;

import mtg.collection.CollectionEntry;
import mtg.collection.CollectionManager;
import mtg.collection.JTextFieldLabel;

public class StatisticsWindow extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public StatisticsWindow() {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setTitle("Estatísticas - Mtg Collection - by Morelli");
		setSize(1024, 768);
		setLocationRelativeTo(null);
		setLayout(new BorderLayout());

		int totalOfCards = 0;
		int specialCount = 0;
		int mythicCount = 0;
		int rareCount = 0;
		int uncommonCount = 0;
		int commonCount = 0;

		Set<String> keySet = CollectionManager.collectionMap.keySet();
		for (final String key : keySet) {
			CollectionEntry entry = CollectionManager.collectionMap.get(key);
			totalOfCards += entry.getQuantity();

			if (entry.rarity.equals("Special")) {
				specialCount += entry.getQuantity();
			} else if (entry.rarity.equals("Mythic Rare")) {
				mythicCount += entry.getQuantity();
			} else if (entry.rarity.equals("Rare")) {
				rareCount += entry.getQuantity();
			} else if (entry.rarity.equals("Uncommon")) {
				uncommonCount += entry.getQuantity();
			} else {
				commonCount += entry.getQuantity();
			}
		}

		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		add(panel);

		StringBuilder builder = new StringBuilder("Número de cartas cadastradas: ");
		builder.append(totalOfCards).append("<br>Número de Especiais: ").append(specialCount)
				.append("<br>Número de Míticas: ").append(mythicCount).append("<br>Número de Raras: ").append(rareCount)
				.append("<br>Número de Incomuns: ").append(uncommonCount).append("<br>Número de Comuns: ")
				.append(commonCount);

		panel.add(new JTextFieldLabel(builder.toString()));
		setVisible(true);
	}

}
