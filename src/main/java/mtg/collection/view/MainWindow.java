package mtg.collection.view;

import java.awt.GridLayout;

import javax.swing.JFrame;

public class MainWindow extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public MainWindow() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("Mtg Collection - by Morelli");
		setSize(600, 200);
		setLocationRelativeTo(null);
		setLayout(new GridLayout(2, 2));

		add(new EditionsButton());
		add(new CollectionButton());
		add(new AddCardsButton());
		add(new StatisticsButton());
		setVisible(true);
	}

}
