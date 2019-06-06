package mtg.collection.view;

import java.awt.GridLayout;

import javax.swing.JFrame;

import mtg.collection.view.addcards.AddCardsButton;
import mtg.collection.view.collection.CollectionButton;
import mtg.collection.view.decks.DecksButton;
import mtg.collection.view.editions.EditionsButton;
import mtg.collection.view.prices.PricesButton;
import mtg.collection.view.statistics.StatisticsButton;

public class MainWindow extends JFrame {

	private static final long serialVersionUID = 1L;

	public MainWindow() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("Mtg Collection - by Morelli");
		setSize(600, 300);
		setLocationRelativeTo(null);
		setLayout(new GridLayout(3, 2));

		add(new EditionsButton());
		add(new CollectionButton());
		add(new AddCardsButton());
		add(new StatisticsButton());
		add(new PricesButton());
		add(new DecksButton());
		setVisible(true);
	}

}
