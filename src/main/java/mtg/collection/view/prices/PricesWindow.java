package mtg.collection.view.prices;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Arrays;
import java.util.Comparator;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import mtg.collection.view.addcards.AddCardsTableModel;

public class PricesWindow extends JFrame {

	private static final long serialVersionUID = 1L;

	private PricesTableModel leftModel;
	private JTable leftTable;

	private DefaultTableModel rightModel;
	private JTable rightTable;

	public PricesWindow() {
		setTitle("Atualizacao de Precos - Mtg Collection - by Morelli");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setSize(1024, 768);
		setLocationRelativeTo(null);
		setLayout(new BorderLayout());

		add(getLeftPanel(), BorderLayout.WEST);
		add(getCenterPanel(), BorderLayout.CENTER);
		add(getEastPanel(), BorderLayout.EAST);

		setVisible(true);
	}

	private JPanel getLeftPanel() {
		final JPanel panel = new JPanel(new BorderLayout());
		panel.setPreferredSize(new Dimension(350, 1000));

		leftModel = new PricesTableModel();
		leftTable = new JTable(leftModel);
		
		final TableRowSorter<TableModel> sorter = new TableRowSorter<>(leftTable.getModel());
		sorter.setComparator(leftModel.getColumnIndex(PricesTableModel.EN_NAME), new Comparator<String>() {
			@Override
			public int compare(final String x, final String y) {
				return x.compareTo(y);
			}
		});

		leftTable.setRowSorter(sorter);
		leftTable.getRowSorter().toggleSortOrder(leftModel.getColumnIndex(PricesTableModel.EN_NAME));
		leftTable.getColumnModel().getColumn(leftModel.getColumnIndex(AddCardsTableModel.EN_NAME)).setPreferredWidth(200);

		panel.add(new JScrollPane(leftTable), BorderLayout.CENTER);

		final JLabel label = new JLabel("Edicoes disponiveis:", SwingConstants.CENTER);
		panel.add(label, BorderLayout.NORTH);
		return panel;
	}

	private JPanel getCenterPanel() {
		final JPanel panel = new JPanel(new BorderLayout());

		rightModel = new DefaultTableModel();
		rightModel.addColumn(PricesTableModel.EN_NAME);
		rightTable = new JTable(rightModel);
		final JScrollPane tableScrollPane = new JScrollPane(rightTable);
		panel.add(tableScrollPane, BorderLayout.CENTER);

		final JLabel label = new JLabel("Edicoes para atualizar:", SwingConstants.CENTER);
		panel.add(label, BorderLayout.NORTH);
		return panel;
	}
	
	private Component getEastPanel() {
		final JPanel panel = new JPanel(new BorderLayout());
		panel.setPreferredSize(new Dimension(350, 1000));

		final JButton addAllButton = new JButton("Adicionar Todas");
		addAllButton.addMouseListener(new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent arg0) {
			}

			@Override
			public void mousePressed(MouseEvent arg0) {
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
			}

			@Override
			public void mouseClicked(MouseEvent arg0) {
				final int count = leftModel.getRowCount();
				for (int i = 0; i < count; i++) {
					rightModel.addRow(Arrays.asList(leftModel.getValueAt(i, 0)).toArray());
					leftModel.removeRow(i);
					leftModel.fireTableRowsDeleted(0, 0);
				}
				
			}
		});

		panel.add(addAllButton, BorderLayout.CENTER);
		return panel;
	}
	
}
