package mtg.collection.view.editions;

import java.awt.BorderLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Comparator;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import mtg.collection.view.edition.MtgEditionWindow;

public class EditionsWindow extends JFrame {

	private static final long serialVersionUID = 1L;

	final EditionsTableModel model = new EditionsTableModel();
	final JTable table = new JTable(model);

	public EditionsWindow() {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setTitle("Edicoes - Mtg Collection - by Morelli");
		setSize(1024, 768);
		setLocationRelativeTo(null);
		setLayout(new BorderLayout());

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
				table.getRowSorter().toggleSortOrder(model.getColumnIndex(EditionsTableModel.EDITION_NAME));
			}

			@Override
			public void keyPressed(final KeyEvent e) {
			}
		});

		final TableRowSorter<TableModel> sorter = new TableRowSorter<>(table.getModel());
		sorter.setComparator(model.getColumnIndex(EditionsTableModel.TOTAL), new Comparator<Integer>() {
			@Override
			public int compare(final Integer x, final Integer y) {
				return x.compareTo(y);
			}
		});

		table.setRowSorter(sorter);
		table.getRowSorter().toggleSortOrder(model.getColumnIndex(EditionsTableModel.EDITION_NAME));

		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(final MouseEvent evt) {
				final int row = table.rowAtPoint(evt.getPoint());
				final int col = table.columnAtPoint(evt.getPoint());
				if (row >= 0 && col >= 0) {
					new MtgEditionWindow(
							(String) table.getValueAt(row, model.getColumnIndex(EditionsTableModel.EDITION_NAME)));
				}
			}
		});

		table.getColumnModel().getColumn(model.getColumnIndex(EditionsTableModel.EDITION_NAME))
				.setPreferredWidth(500);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		final JScrollPane tableScrollPane = new JScrollPane(table);

		add(field, BorderLayout.PAGE_START);
		add(tableScrollPane);
		setVisible(true);
	}

}
