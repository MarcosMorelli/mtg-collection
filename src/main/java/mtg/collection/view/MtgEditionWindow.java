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

public class MtgEditionWindow extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final MtgEditionTableModel model;
	private final JTable table;

	public MtgEditionWindow(final String edition) {
		setTitle(edition + " - Mtg Collection - by Morelli");
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

		final JTextField field = new JTextField();
		field.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(final KeyEvent e) {
			}

			@Override
			public void keyReleased(final KeyEvent e) {
				final TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(model);
				sorter.setRowFilter(RowFilter.regexFilter("(?i)" + field.getText()));
				table.setRowSorter(sorter);
				table.getRowSorter().toggleSortOrder(0);
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

		model = new MtgEditionTableModel(edition);
		table = new JTable(model);

		table.setAutoCreateRowSorter(true);
		table.getRowSorter().toggleSortOrder(0);

		table.getColumnModel().getColumn(0).setPreferredWidth(250);
		table.getColumnModel().getColumn(1).setPreferredWidth(250);
		table.getColumnModel().getColumn(2).setPreferredWidth(150);

		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		final JScrollPane tableScrollPane = new JScrollPane(table);

		add(panel, BorderLayout.PAGE_START);
		add(tableScrollPane);
		setVisible(true);
	}

}
