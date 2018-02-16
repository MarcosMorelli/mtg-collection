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

public class MtgEditionWindow extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	String edition;
	MtgEditionTableModel model;
	JTable table;

	public MtgEditionWindow(String edition) {
		this.edition = edition;
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
				new EditionsWindow();
			}

			@Override
			public void windowClosed(WindowEvent e) {
			}

			@Override
			public void windowActivated(WindowEvent e) {
			}
		});

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
				table.getRowSorter().toggleSortOrder(1);
			}

			@Override
			public void keyPressed(KeyEvent e) {
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
				String enName = table.getValueAt(table.getSelectedRow(), 1).toString();
				CollectionManager
						.addCard(new NewCollectionEntry(CollectionManager.getQuantity(enName, edition), enName, edition));

				model.updateCell(enName, CollectionManager.getQuantity(enName));
			}
		});

		JPanel panel = new JPanel(new BorderLayout());
		panel.add(field);
		panel.add(button, BorderLayout.LINE_END);

		model = new MtgEditionTableModel();
		table = new JTable(model);

		table.setAutoCreateRowSorter(true);
		table.getRowSorter().toggleSortOrder(1);

		table.getColumnModel().getColumn(1).setPreferredWidth(250);
		table.getColumnModel().getColumn(2).setPreferredWidth(250);
		table.getColumnModel().getColumn(3).setPreferredWidth(150);

		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		JScrollPane tableScrollPane = new JScrollPane(table);

		add(panel, BorderLayout.PAGE_START);
		add(tableScrollPane);
		setVisible(true);
	}

}
