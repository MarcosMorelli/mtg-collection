package mtg.collection.view.prices;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import mtg.collection.editions.Editions;
import mtg.collection.scg.SCGReader;
import mtg.collection.view.addcards.AddCardsTableModel;

public class PricesWindow extends JFrame {

	private static final long serialVersionUID = 1L;

	private PricesTableModel leftModel;
	private JTable leftTable;

	private RefreshTableModel rightModel;
	private JTable rightTable;

	private JTextArea logPanel;
	private JButton refreshButton;
	private SCGReader scgReader;

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
		leftTable.getColumnModel().getColumn(leftModel.getColumnIndex(AddCardsTableModel.EN_NAME))
				.setPreferredWidth(200);
		leftTable.addMouseListener(new LeftTableMouseListener());

		panel.add(new JScrollPane(leftTable), BorderLayout.CENTER);

		final JLabel label = new JLabel("Edicoes disponiveis:", SwingConstants.CENTER);
		panel.add(label, BorderLayout.NORTH);

		final JButton addAllButton = new JButton("Adicionar Todas");
		addAllButton.addMouseListener(new AddAllButtonMouseListener());
		panel.add(addAllButton, BorderLayout.SOUTH);

		return panel;
	}

	private JPanel getCenterPanel() {
		final JPanel panel = new JPanel(new BorderLayout());

		rightModel = new RefreshTableModel();
		rightTable = new JTable(rightModel);
		rightTable.addMouseListener(new RightTableMouseListener());

		final TableRowSorter<TableModel> sorter = new TableRowSorter<>(rightTable.getModel());
		sorter.setComparator(rightModel.getColumnIndex(RefreshTableModel.EN_NAME), new Comparator<String>() {
			@Override
			public int compare(final String x, final String y) {
				return x.compareTo(y);
			}
		});

		rightTable.setRowSorter(sorter);
		rightTable.getRowSorter().toggleSortOrder(rightModel.getColumnIndex(PricesTableModel.EN_NAME));
		rightTable.getColumnModel().getColumn(rightModel.getColumnIndex(PricesTableModel.EN_NAME))
				.setPreferredWidth(200);

		final JScrollPane tableScrollPane = new JScrollPane(rightTable);
		panel.add(tableScrollPane, BorderLayout.CENTER);

		final JLabel label = new JLabel("Edicoes para atualizar:", SwingConstants.CENTER);
		panel.add(label, BorderLayout.NORTH);

		final JButton removeAllButton = new JButton("Remover Todas");
		removeAllButton.addMouseListener(new RemoveAllButtonMouseListener());
		panel.add(removeAllButton, BorderLayout.SOUTH);

		return panel;
	}

	private Component getEastPanel() {
		final JPanel panel = new JPanel(new BorderLayout());
		panel.setPreferredSize(new Dimension(300, 1000));

		logPanel = new JTextArea();
		logPanel.setEditable(false);

		final JScrollPane scrollPane = new JScrollPane(logPanel);
		panel.add(scrollPane, BorderLayout.CENTER);

		refreshButton = new JButton("Atualizar");
		refreshButton.addMouseListener(new RefreshButtonMouseListener());

		panel.add(refreshButton, BorderLayout.SOUTH);
		return panel;
	}

	final class LeftTableMouseListener implements MouseListener {
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
			if (arg0.getClickCount() == 2) {
				int[] selectedRows = leftTable.getSelectedRows();
				for (int i = 0; i < selectedRows.length; i++) {
					int index = selectedRows[i];
					int convertedIndex = leftTable.convertRowIndexToModel(index);

					rightModel.addRow(Arrays
							.asList(leftModel.getValueAt(convertedIndex, 0), leftModel.getValueAt(convertedIndex, 1))
							.toArray());
					leftModel.removeRow(convertedIndex);
				}
			}
		}
	}

	final class RightTableMouseListener implements MouseListener {
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
			if (arg0.getClickCount() == 2) {
				int[] selectedRows = rightTable.getSelectedRows();
				for (int i = 0; i < selectedRows.length; i++) {
					int index = selectedRows[i];
					int convertedIndex = rightTable.convertRowIndexToModel(index);

					leftModel.addRow(Arrays
							.asList(rightModel.getValueAt(convertedIndex, 0), rightModel.getValueAt(convertedIndex, 1))
							.toArray());
					rightModel.removeRow(convertedIndex);
				}
			}
		}
	}

	final class AddAllButtonMouseListener implements MouseListener {
		@Override
		public void mouseClicked(MouseEvent arg0) {
			while (leftTable.getRowCount() > 0) {
				int convertedIndex = leftTable.convertRowIndexToModel(0);

				rightModel.addRow(
						Arrays.asList(leftModel.getValueAt(convertedIndex, 0), leftModel.getValueAt(convertedIndex, 1))
								.toArray());
				leftModel.removeRow(convertedIndex);
			}
		}

		@Override
		public void mouseEntered(MouseEvent arg0) {
		}

		@Override
		public void mouseExited(MouseEvent arg0) {
		}

		@Override
		public void mousePressed(MouseEvent arg0) {
		}

		@Override
		public void mouseReleased(MouseEvent arg0) {
			
		}
	}

	final class RemoveAllButtonMouseListener implements MouseListener {
		@Override
		public void mouseClicked(MouseEvent arg0) {
			while (rightTable.getRowCount() > 0) {
				int convertedIndex = rightTable.convertRowIndexToModel(0);

				leftModel.addRow(Arrays
						.asList(rightModel.getValueAt(convertedIndex, 0), rightModel.getValueAt(convertedIndex, 1))
						.toArray());
				rightModel.removeRow(convertedIndex);
			}
		}

		@Override
		public void mouseEntered(MouseEvent arg0) {
		}

		@Override
		public void mouseExited(MouseEvent arg0) {
		}

		@Override
		public void mousePressed(MouseEvent arg0) {
		}

		@Override
		public void mouseReleased(MouseEvent arg0) {
		}
	}

	final class RefreshButtonMouseListener implements MouseListener {
		@Override
		public void mouseClicked(MouseEvent arg0) {
			refreshButton.setText("Atualizando...");
			refreshButton.setEnabled(false);
			refreshButton.repaint();
			
			final ConcurrentLinkedQueue<Editions> editionsToRefresh = new ConcurrentLinkedQueue<Editions>();
			for (int i = 0; i < rightModel.getRowCount(); i++) {
				final String editionName = rightModel.getValueAt(i, 0).toString();
				Arrays.asList(Editions.values()).forEach(edition -> {
					if (edition.getName().equals(editionName)) {
						editionsToRefresh.add(edition);
					}
				});
			}

			scgReader = new SCGReader(1, editionsToRefresh);
			scgReader.start();
			
			while (!scgReader.isDone()) {
				logPanel.append("Working...\n");
				try {
					Thread.sleep(2 * 1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			refreshButton.setText("Atualizar");
			refreshButton.setEnabled(true);
			refreshButton.repaint();
		}

		@Override
		public void mouseEntered(MouseEvent arg0) {
		}

		@Override
		public void mouseExited(MouseEvent arg0) {
		}

		@Override
		public void mousePressed(MouseEvent arg0) {
		}

		@Override
		public void mouseReleased(MouseEvent arg0) {
		}
	}

}
