package mtg.collection.view;

import java.awt.BorderLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JButton;
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

	AddCardsTableModel model;
	JTextField field;
	JTable table;

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
			public void keyTyped(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
			}

			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_DOWN) {
					table.requestFocus();
				} else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
					field.setText("");
					field.requestFocus();
				}

				TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(model);
				sorter.setRowFilter(RowFilter.regexFilter("(?i)" + field.getText()));
				table.setRowSorter(sorter);
				table.getRowSorter().toggleSortOrder(1);
			}
		});

		JButton button = new JButton(" Add ");
		button.addMouseListener(new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent e) {
			}

			@Override
			public void mousePressed(MouseEvent e) {
			}

			@Override
			public void mouseExited(MouseEvent e) {
			}

			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				if (table.getSelectedRow() != -1) {
					addCard();
				}
			}
		});

		JPanel panel = new JPanel(new BorderLayout());
		panel.add(field);
		panel.add(button, BorderLayout.LINE_END);

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

		JScrollPane tableScrollPane = new JScrollPane(table);

		add(panel, BorderLayout.PAGE_START);
		add(tableScrollPane);
		setVisible(true);
	}

	private void addCard() {
		String enName = table.getValueAt(table.getSelectedRow(), 0).toString();
		String edition = table.getValueAt(table.getSelectedRow(), 5).toString();
		CollectionManager
				.addCard(new NewCollectionEntry(CollectionManager.getQuantity(enName, edition), enName, edition));

		model.updateCell(enName, edition, CollectionManager.getQuantity(enName, edition));
	}

	private void removeCard() {
		String enName = table.getValueAt(table.getSelectedRow(), 0).toString();
		String edition = table.getValueAt(table.getSelectedRow(), 5).toString();
		CollectionManager
				.removeCard(new NewCollectionEntry(CollectionManager.getQuantity(enName, edition), enName, edition));

		model.updateCell(enName, edition, CollectionManager.getQuantity(enName, edition));
	}
}
