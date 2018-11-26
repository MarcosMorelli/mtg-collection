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
		float sum = 0;

		int promotionalCount = 0;
		int mythicCount = 0;
		int rareCount = 0;
		int uncommonCount = 0;
		int commonCount = 0;
		int fourtyNineCentsOrMoreCount = 0;

		final Set<String> keySet = CollectionController.collectionMap.keySet();
		for (final String key : keySet) {
			final NewCollectionEntry entry = CollectionController.collectionMap.get(key);

			int quantity = Integer.parseInt(entry.quantity);
			totalOfCards += quantity;

			final MagicCard card = EditionsController.getInstance().getCard(entry.enName, entry.edition);

			try {
				sum += (card.getPrice() * quantity);
				
				if (card.getPrice() > 0.48) {
					fourtyNineCentsOrMoreCount += quantity;
				}
			} catch (NullPointerException e) {
			}

			try {
				if (card.getRarity().equals("Mythic Rare")) {
					mythicCount += quantity;
				} else if (card.getRarity().equals("Rare")) {
					rareCount += quantity;
				} else if (card.getRarity().equals("Uncommon")) {
					uncommonCount += quantity;
				} else if (card.getRarity().equals("Common")) {
					commonCount += quantity;
				} else if (card.getRarity().equals("Promotional")) {
					promotionalCount += quantity;
				} else {
					System.err.println(entry.enName);
					System.err.println(entry.edition);
					System.err.println(entry.quantity);
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
		builder.append(totalOfCards).append("<br>Número de Míticas: ").append(mythicCount)
				.append("<br>Número de Raras: ").append(rareCount).append("<br>Número de Incomuns: ")
				.append(uncommonCount).append("<br>Número de Comuns: ").append(commonCount)
				.append("<br>Número de Promotional: ").append(promotionalCount)
				.append("<br>Cartas que valem $0.49 ou mais: ").append(fourtyNineCentsOrMoreCount)
				.append("<br><br>Valor total: ").append(sum);

		panel.add(new JTextFieldLabel(builder.toString()));
		setVisible(true);
	}

}
