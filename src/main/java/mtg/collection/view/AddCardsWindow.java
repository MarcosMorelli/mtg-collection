package mtg.collection.view;

import java.awt.BorderLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import mtg.collection.collection.CollectionManager;
import mtg.collection.collection.NewCollectionEntry;

public class AddCardsWindow extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private AddCardsTableModel model;
	private JTextField field;
	private JTable table;

	public AddCardsWindow() {
		setTitle("Adding Cards - Mtg Collection - by Morelli");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setSize(1024, 768);
		setLocationRelativeTo(null);
		setLayout(new BorderLayout());

		addWindowListener(new WindowListener() {
			@Override
			public void windowOpened(WindowEvent e) {
			}

			@Override
			public void windowIconified(WindowEvent e) {
			}

			@Override
			public void windowDeiconified(WindowEvent e) {
			}

			@Override
			public void windowDeactivated(WindowEvent e) {
			}

			@Override
			public void windowClosing(WindowEvent e) {
				CollectionManager.writeCollection();
			}

			@Override
			public void windowClosed(WindowEvent e) {
			}

			@Override
			public void windowActivated(WindowEvent e) {
			}
		});

		field = new JTextField();
		field.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(final KeyEvent e) {
			}

			@Override
			public void keyReleased(final KeyEvent e) {
			}

			@Override
			public void keyPressed(final KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_DOWN) {
					table.requestFocus();
				} else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
					field.setText("");
					field.requestFocus();
				}

				final TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(model);
				sorter.setRowFilter(RowFilter.regexFilter("(?i)" + field.getText()));
				table.setRowSorter(sorter);
				table.getRowSorter().toggleSortOrder(0);
			}
		});

		final JPanel panel = new JPanel(new BorderLayout());
		panel.add(field);

		model = new AddCardsTableModel();
		table = new JTable(model);

		table.setAutoCreateRowSorter(true);
		table.getRowSorter().toggleSortOrder(0);

		table.getColumnModel().getColumn(0).setPreferredWidth(200);
		table.getColumnModel().getColumn(1).setPreferredWidth(200);
		table.getColumnModel().getColumn(2).setPreferredWidth(150);
		table.getColumnModel().getColumn(5).setPreferredWidth(150);

		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		table.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
			}

			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_SHIFT && table.getSelectedRow() != -1) {
					removeCard();
				} else if (e.getKeyCode() == KeyEvent.VK_SPACE && table.getSelectedRow() != -1) {
					addCard();
				} else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
					field.setText("");
					field.requestFocus();
				}
			}

		});

		final JScrollPane tableScrollPane = new JScrollPane(table);

		add(panel, BorderLayout.PAGE_START);
		add(tableScrollPane);
		setVisible(true);
	}

	private void addCard() {
		final String enName = table.getValueAt(table.getSelectedRow(), 0).toString();
		final String edition = table.getValueAt(table.getSelectedRow(), 5).toString();
		CollectionManager
				.addCard(new NewCollectionEntry(CollectionManager.getQuantity(enName, edition), enName, edition));

		model.updateCell(enName, edition, CollectionManager.getQuantity(enName, edition));
	}

	private void removeCard() {
		final String enName = table.getValueAt(table.getSelectedRow(), 0).toString();
		final String edition = table.getValueAt(table.getSelectedRow(), 5).toString();
		CollectionManager
				.removeCard(new NewCollectionEntry(CollectionManager.getQuantity(enName, edition), enName, edition));

		model.updateCell(enName, edition, CollectionManager.getQuantity(enName, edition));
	}
}
