package mtg.collection.view.statistics;

import java.awt.BorderLayout;
import java.util.Set;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;

import mtg.collection.collection.CollectionController;
import mtg.collection.collection.NewCollectionEntry;
import mtg.collection.editions.EditionsController;
import mtg.collection.editions.MagicCard;
import mtg.collection.view.JTextFieldLabel;

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
		int blankCount = 0;

		Set<String> keySet = CollectionController.newCollectionMap.keySet();
		for (final String key : keySet) {
			NewCollectionEntry entry = CollectionController.newCollectionMap.get(key);
			
			int quantity = Integer.parseInt(entry.quantity);
			totalOfCards += quantity;

			MagicCard card = EditionsController.getInstance().getCard(entry.enName, entry.edition);

			try {
			if (card.getRarity().equals("Special")) {
				specialCount += quantity;
			} else if (card.getRarity().equals("Mythic Rare")) {
				mythicCount += quantity;
			} else if (card.getRarity().equals("Rare")) {
				rareCount += quantity;
			} else if (card.getRarity().equals("Uncommon")) {
				uncommonCount += quantity;
			} else if (card.getRarity().equals("Common")) {
				commonCount += quantity;
			} else {
				System.err.println(entry.enName);
				System.err.println(entry.edition);
				System.err.println(entry.quantity);
				blankCount += quantity;
			}
			} catch (Exception e) {
				System.err.println(entry.enName);
				System.err.println(entry.edition);
				System.err.println(entry.quantity);
			}
		}

		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		add(panel);

		StringBuilder builder = new StringBuilder("Número de cartas cadastradas: ");
		builder.append(totalOfCards).append("<br>Número de Especiais: ").append(specialCount)
				.append("<br>Número de Míticas: ").append(mythicCount).append("<br>Número de Raras: ").append(rareCount)
				.append("<br>Número de Incomuns: ").append(uncommonCount).append("<br>Número de Comuns: ")
				.append(commonCount).append("<br>Número de Blanks: ").append(blankCount);

		panel.add(new JTextFieldLabel(builder.toString()));
		setVisible(true);
	}

}
