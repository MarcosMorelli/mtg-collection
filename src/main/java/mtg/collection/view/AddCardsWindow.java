package mtg.collection.view;

import java.awt.BorderLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Comparator;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import mtg.collection.collection.CollectionController;
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
				CollectionController.writeCollection();
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
				table.getRowSorter().toggleSortOrder(model.columnNames.indexOf(AddCardsTableModel.EN_NAME));
			}
		});

		final JPanel panel = new JPanel(new BorderLayout());
		panel.add(field);

		model = new AddCardsTableModel();
		table = new JTable(model);

		final TableRowSorter<TableModel> sorter = new TableRowSorter<>(table.getModel());
		sorter.setComparator(model.columnNames.indexOf(AddCardsTableModel.PRICE), new Comparator<Float>() {
			@Override
			public int compare(final Float x, final Float y) {
				return x.compareTo(y);
			}
		});

		table.setRowSorter(sorter);
		table.getRowSorter().toggleSortOrder(model.columnNames.indexOf(AddCardsTableModel.EN_NAME));

		table.getColumnModel().getColumn(model.columnNames.indexOf(AddCardsTableModel.EN_NAME)).setPreferredWidth(230);
		table.getColumnModel().getColumn(model.columnNames.indexOf(AddCardsTableModel.PT_NAME)).setPreferredWidth(230);
		table.getColumnModel().getColumn(model.columnNames.indexOf(AddCardsTableModel.TYPE)).setPreferredWidth(200);
		table.getColumnModel().getColumn(model.columnNames.indexOf(AddCardsTableModel.MANA)).setPreferredWidth(70);
		table.getColumnModel().getColumn(model.columnNames.indexOf(AddCardsTableModel.RARITY)).setPreferredWidth(80);
		table.getColumnModel().getColumn(model.columnNames.indexOf(AddCardsTableModel.EDITION)).setPreferredWidth(150);

		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		table.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(final KeyEvent e) {
			}

			@Override
			public void keyReleased(final KeyEvent e) {
			}

			@Override
			public void keyPressed(final KeyEvent e) {
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
		CollectionController
				.addCard(new NewCollectionEntry(CollectionController.getQuantity(enName, edition), enName, edition));

		model.updateCell(enName, edition, CollectionController.getQuantity(enName, edition));
	}

	private void removeCard() {
		final String enName = table.getValueAt(table.getSelectedRow(), 0).toString();
		final String edition = table.getValueAt(table.getSelectedRow(), 5).toString();
		CollectionController
				.removeCard(new NewCollectionEntry(CollectionController.getQuantity(enName, edition), enName, edition));

		model.updateCell(enName, edition, CollectionController.getQuantity(enName, edition));
	}
}
