package mtg.collection.view.decks;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class DecksWindow extends JFrame {

	private static final long serialVersionUID = 1L;

	private JTabbedPane tabbedPane;

	private JPanel openDeckPanel;
	private JTable decksTable;
	private JPanel openDeckRightPanel;

	private JPanel newDeckPanel;
	private JPanel leftNewDeckPanel;
	private JTextArea pasteDeckArea;
	private JPanel rightNewDeckPanel;

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

		newDeckPanel = new JPanel(new GridLayout(1, 0));
		configureNewDeckPanel();

		tabbedPane = new JTabbedPane();
		tabbedPane.addTab("Abrir Deck", openDeckPanel);
		tabbedPane.addTab("Novo Deck", newDeckPanel);

		add(tabbedPane, BorderLayout.CENTER);
		setVisible(true);
	}

	private void configureNewDeckPanel() {
		leftNewDeckPanel = new JPanel();
		leftNewDeckPanel.setLayout(new BoxLayout(leftNewDeckPanel, BoxLayout.Y_AXIS));

		final JPanel linkDeckPanel = new JPanel(new BorderLayout());
		linkDeckPanel.setMaximumSize(new Dimension(1000, 300));
		linkDeckPanel.add(new JLabel("Link do Deck:"), BorderLayout.NORTH);
		linkDeckPanel.add(new JTextField(), BorderLayout.CENTER);
		linkDeckPanel.add(new JButton("Importar Deck"), BorderLayout.SOUTH);
		leftNewDeckPanel.add(linkDeckPanel);

		pasteDeckArea = new JTextArea();
		pasteDeckArea.setLineWrap(true);

		final JPanel pasteDeckPanel = new JPanel(new BorderLayout());
		pasteDeckPanel.add(new JLabel("Lista do Deck:"), BorderLayout.NORTH);
		pasteDeckPanel.add(new JScrollPane(pasteDeckArea), BorderLayout.CENTER);
		final JButton readDeckButton = new JButton("Ler Deck");
		readDeckButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent action) {
				final String[] lines = pasteDeckArea.getText().split("\n");
				newDeckPanel.remove(rightNewDeckPanel);
				rightNewDeckPanel = getNewDeckRightPanel(lines);
				newDeckPanel.add(rightNewDeckPanel);
				tabbedPane.repaint();
			}
		});
		pasteDeckPanel.add(readDeckButton, BorderLayout.SOUTH);
		leftNewDeckPanel.add(pasteDeckPanel);

		rightNewDeckPanel = new JPanel();

		newDeckPanel.add(leftNewDeckPanel);
		newDeckPanel.add(rightNewDeckPanel);
	}

	private JPanel getDeckRightPanel() {
		if (decksTable.getSelectedRow() == -1) {
			return new JPanel();
		}

		final String selectedDeck = (String) decksTable.getModel().getValueAt(decksTable.getSelectedRow(), 0);

		final JPanel mainDeckPanel = new JPanel(new BorderLayout());
		final JTable mainDeckTable = new JTable(new DeckTableModel(selectedDeck, false));
		mainDeckTable.setDefaultRenderer(Object.class, new DeckListTableCellRender());
		mainDeckTable.getColumnModel().getColumn(0).setMaxWidth(40);
		mainDeckTable.getColumnModel().getColumn(0).setMinWidth(40);
		mainDeckPanel.add(new JLabel("Main Deck:"), BorderLayout.NORTH);
		mainDeckPanel.add(new JScrollPane(mainDeckTable), BorderLayout.CENTER);
		
		final JPanel tablesPanel = new JPanel(new GridLayout(0, 1));
		tablesPanel.add(mainDeckPanel);

		final JTable sideDeckTable = new JTable(new DeckTableModel(selectedDeck, true));
		if (sideDeckTable.getModel().getRowCount() > 0) {
			sideDeckTable.setDefaultRenderer(Object.class, new DeckListTableCellRender());
			sideDeckTable.getColumnModel().getColumn(0).setMaxWidth(40);
			sideDeckTable.getColumnModel().getColumn(0).setMinWidth(40);
			
			final JPanel sideDeckPanel = new JPanel(new BorderLayout());
			sideDeckPanel.add(new JLabel("Sideboard:"), BorderLayout.NORTH);
			sideDeckPanel.add(new JScrollPane(sideDeckTable), BorderLayout.CENTER);
			
			tablesPanel.add(sideDeckPanel);
		}

		final JPanel panel = new JPanel(new BorderLayout());
		panel.add(new JLabel((String) decksTable.getValueAt(decksTable.getSelectedRow(), 0)), BorderLayout.NORTH);
		panel.add(tablesPanel, BorderLayout.CENTER);

		return panel;
	}

	private JPanel getNewDeckRightPanel(final String[] lines) {
		for (int i = 0; i < lines.length; i++) {
			if (!lines[i].matches("(SB:)?( )*[0-9](x)? .*")) {
				return new JPanel();
			}
		}

		final JPanel panel = new JPanel(new GridLayout(2, 1));

		final JPanel mainDeckPanel = new JPanel(new BorderLayout());
		final JTable mainDeckTable = new JTable(new DeckTableModel(lines, false));
		mainDeckTable.setDefaultRenderer(Object.class, new DeckListTableCellRender());
		mainDeckTable.getColumnModel().getColumn(0).setMaxWidth(40);
		mainDeckTable.getColumnModel().getColumn(0).setMinWidth(40);
		mainDeckPanel.add(new JLabel("Main Deck:"), BorderLayout.NORTH);
		mainDeckPanel.add(new JScrollPane(mainDeckTable), BorderLayout.CENTER);

		final JPanel sideDeckPanel = new JPanel(new BorderLayout());
		final JTable sideDeckTable = new JTable(new DeckTableModel(lines, true));
		sideDeckTable.setDefaultRenderer(Object.class, new DeckListTableCellRender());
		sideDeckTable.getColumnModel().getColumn(0).setMaxWidth(40);
		sideDeckTable.getColumnModel().getColumn(0).setMinWidth(40);
		sideDeckPanel.add(new JLabel("Sideboard:"), BorderLayout.NORTH);
		sideDeckPanel.add(new JScrollPane(sideDeckTable), BorderLayout.CENTER);

		panel.add(mainDeckPanel);
		panel.add(sideDeckPanel);

		return panel;
	}

}
