package mtg.collection.view.collection;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Comparator;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

public class CollectionWindow extends JFrame {

	private static final long serialVersionUID = 1L;

	private JTable table;

	public CollectionWindow() {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setTitle("Mtg Collection - by Morelli");
		setSize(1024, 768);
		setLocationRelativeTo(null);
		setLayout(new BorderLayout());

		JTextField field = new JTextField(60);
		field.setPreferredSize(new Dimension(100, 25));
		field.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(table.getModel());
				sorter.setRowFilter(RowFilter.regexFilter("(?i)" + field.getText()));
				table.setRowSorter(sorter);
				table.getRowSorter().toggleSortOrder(0);
			}

			@Override
			public void keyPressed(KeyEvent e) {
			}
		});

		table = new JTable(new CollectionTableModel());
		final CollectionTableModel model = (CollectionTableModel) table.getModel();
		final TableRowSorter<TableModel> sorter = new TableRowSorter<>(model);
		sorter.setComparator(model.getColumnIndex(CollectionTableModel.PRICE), new Comparator<Float>() {
			@Override
			public int compare(final Float x, final Float y) {
				return x.compareTo(y);
			}
		});
		table.setRowSorter(sorter);
		table.getRowSorter().toggleSortOrder(0);

		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent evt) {
				int row = table.rowAtPoint(evt.getPoint());
				int col = table.columnAtPoint(evt.getPoint());
				if (row >= 0 && col >= 0) {
					System.err.println(table.getValueAt(row, 0));
				}
			}
		});

		table.getColumnModel().getColumn(model.getColumnIndex(CollectionTableModel.EN_NAME)).setPreferredWidth(230);
		table.getColumnModel().getColumn(model.getColumnIndex(CollectionTableModel.PT_NAME)).setPreferredWidth(230);

		JScrollPane tableScrollPane = new JScrollPane(table);

		add(field, BorderLayout.PAGE_START);
		add(tableScrollPane);
		setVisible(true);
	}

}
