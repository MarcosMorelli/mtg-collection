package mtg.collection.view.decks;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;

public class DecksWindow extends JFrame {

	private static final long serialVersionUID = 1L;

	private JTabbedPane tabbedPane;

	private JPanel openDeckPanel;
	private JTable decksTable;
	private JPanel openDeckRightPanel;

	private JPanel newDeckPanel;
	private JTextField importDeckField;

	public DecksWindow() {
		setTitle("Decks - Mtg Collection - by Morelli");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setSize(1024, 768);
		setLocationRelativeTo(null);
		setLayout(new BorderLayout());

		openDeckPanel = new JPanel(new GridLayout(1, 2));

		decksTable = new JTable(new DecksListTableModel());
		final JButton openDeckButton = new JButton("Abrir Deck");
		openDeckButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				openDeckPanel.remove(openDeckRightPanel);
				openDeckRightPanel = getDeckRightPanel();
				openDeckPanel.add(openDeckRightPanel);
				tabbedPane.repaint();
			}
		});

		final JPanel openDeckLeftPanel = new JPanel(new BorderLayout());
		openDeckLeftPanel.add(new JScrollPane(decksTable), BorderLayout.CENTER);
		openDeckLeftPanel.add(openDeckButton, BorderLayout.SOUTH);

		openDeckRightPanel = new JPanel();

		openDeckPanel.add(openDeckLeftPanel);
		openDeckPanel.add(openDeckRightPanel);

		newDeckPanel = new JPanel(new GridLayout(1, 2));

		final JPanel leftNewDeckPanel = new JPanel(new BorderLayout());
		leftNewDeckPanel.add(new JLabel("Link para importar:"), BorderLayout.NORTH);

		importDeckField = new JTextField();
		leftNewDeckPanel.add(importDeckField, BorderLayout.CENTER);

		newDeckPanel.add(leftNewDeckPanel);

		tabbedPane = new JTabbedPane();
		tabbedPane.addTab("Abrir Deck", openDeckPanel);
		tabbedPane.addTab("Novo Deck", newDeckPanel);

		add(tabbedPane, BorderLayout.CENTER);
		setVisible(true);
	}

	private JPanel getDeckRightPanel() {
		if (decksTable.getSelectedRow() == -1) {
			return new JPanel();
		}

		final String selectedDeck = (String) decksTable.getModel().getValueAt(decksTable.getSelectedRow(), 0);

		final JPanel panel = new JPanel(new GridLayout(2, 1));

		final JPanel mainDeckPanel = new JPanel(new BorderLayout());
		final JTable mainDeckTable = new JTable(new DeckTableModel(selectedDeck, false));
		mainDeckTable.getColumnModel().getColumn(0).setMaxWidth(40);
		mainDeckTable.getColumnModel().getColumn(0).setMinWidth(40);
		mainDeckPanel.add(new JLabel("Main Deck:"), BorderLayout.NORTH);
		mainDeckPanel.add(new JScrollPane(mainDeckTable), BorderLayout.CENTER);

		final JPanel sideDeckPanel = new JPanel(new BorderLayout());
		final JTable sideDeckTable = new JTable(new DeckTableModel(selectedDeck, true));
		sideDeckTable.getColumnModel().getColumn(0).setMaxWidth(40);
		sideDeckTable.getColumnModel().getColumn(0).setMinWidth(40);
		sideDeckPanel.add(new JLabel("Sideboard:"), BorderLayout.NORTH);
		sideDeckPanel.add(new JScrollPane(sideDeckTable), BorderLayout.CENTER);

		panel.add(mainDeckPanel);
		panel.add(sideDeckPanel);

		return panel;
	}

}
