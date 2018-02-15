package mtg.collection.view;

import java.awt.BorderLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Comparator;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import mtg.collection.Main;

public class EditionsWindow extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	EditionsTableModel model = new EditionsTableModel();
	JTable table = new JTable(model);

	public EditionsWindow() {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setTitle("Edi��es - Mtg Collection - by Morelli");
		setSize(1024, 768);
		setLocationRelativeTo(null);
		setLayout(new BorderLayout());
		
		JTextField field = new JTextField();
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
		
		TableRowSorter<TableModel> sorter = new TableRowSorter<>(table.getModel());
		sorter.setComparator(3, new Comparator<String>() {
		    @Override
		    public int compare(String name1, String name2) {
		    	Integer x = Integer.parseInt(name1.substring(0, name1.indexOf("/") - 1));
		    	Integer y = Integer.parseInt(name2.substring(0, name2.indexOf("/") - 1));
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
					Main.selectedEdition = (String) table.getValueAt(row, 1);
					new MtgEditionWindow((String) table.getValueAt(row, 0));
					dispose();
				}
			}
		});

		DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
		leftRenderer.setHorizontalAlignment(JLabel.LEFT);
		table.getColumnModel().getColumn(1).setCellRenderer(leftRenderer);
		table.getColumnModel().getColumn(2).setCellRenderer(leftRenderer);
		table.getColumnModel().getColumn(3).setCellRenderer(leftRenderer);
		table.getColumnModel().getColumn(0).setPreferredWidth(500);
		
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		JScrollPane tableScrollPane = new JScrollPane(table);

		add(field, BorderLayout.PAGE_START);
		add(tableScrollPane);
		setVisible(true);
	}
	
}
