package mtg.collection.view.collection;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

public class CollectionWindow extends JFrame {

	private static final long serialVersionUID = 1L;

	private JTextField field;
	private CollectionTableModel model;
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
				TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(model);
				sorter.setRowFilter(RowFilter.regexFilter("(?i)" + field.getText()));
				table.setRowSorter(sorter);
				table.getRowSorter().toggleSortOrder(0);
			}

			@Override
			public void keyPressed(KeyEvent e) {
			}
		});

		model = new CollectionTableModel();
		table = new JTable(model);

		table.setRowSorter(new TableRowSorter<TableModel>(model));
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

		DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
		leftRenderer.setHorizontalAlignment(JLabel.LEFT);
		table.getColumnModel().getColumn(1).setCellRenderer(leftRenderer);
		table.getColumnModel().getColumn(2).setCellRenderer(leftRenderer);

		TableColumn eventColumn = table.getColumnModel().getColumn(0);
		eventColumn.setPreferredWidth(600);

		JScrollPane tableScrollPane = new JScrollPane(table);

		add(field, BorderLayout.PAGE_START);
		add(tableScrollPane);
		setVisible(true);
	}

}
