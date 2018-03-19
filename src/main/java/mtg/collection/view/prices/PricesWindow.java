package mtg.collection.view.prices;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;
import mtg.collection.editions.Editions;
import mtg.collection.scg.SCGReader;
import mtg.collection.view.addcards.AddCardsTableModel;

public class PricesWindow extends JFrame {

	private static final long serialVersionUID = 1L;

	private JTable leftTable;

	private JButton removeAllButton;

	private RefreshTableModel rightModel;
	private JTable rightTable;

	private JTextArea logPanel;
	private JButton refreshButton;
	private SCGReader scgReader;
	private Timer timer;

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

		leftTable = new JTable();
		configureLeftTable();

		panel.add(new JScrollPane(leftTable), BorderLayout.CENTER);

		final JLabel label = new JLabel("Edicoes disponiveis:", SwingConstants.CENTER);
		panel.add(label, BorderLayout.NORTH);

		final JButton addAllButton = new JButton("Adicionar Todas");
		addAllButton.addActionListener(new AddAllButtonListener());
		panel.add(addAllButton, BorderLayout.SOUTH);

		return panel;
	}

	private void configureLeftTable() {
		leftTable.setModel(new PricesTableModel());

		final PricesTableModel model = (PricesTableModel) leftTable.getModel();
		final TableRowSorter<TableModel> sorter = new TableRowSorter<>(model);
		sorter.setComparator(model.getColumnIndex(PricesTableModel.EN_NAME), new Comparator<String>() {
			@Override
			public int compare(final String x, final String y) {
				return x.compareTo(y);
			}
		});

		leftTable.setRowSorter(sorter);
		leftTable.getRowSorter().toggleSortOrder(model.getColumnIndex(PricesTableModel.EN_NAME));
		leftTable.getColumnModel().getColumn(model.getColumnIndex(AddCardsTableModel.EN_NAME)).setPreferredWidth(200);
		leftTable.addMouseListener(new LeftTableMouseListener());
		leftTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		final DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
		renderer.setHorizontalAlignment(JLabel.CENTER);
		leftTable.getColumnModel().getColumn(model.getColumnIndex(PricesTableModel.LAST_UPDATE))
				.setCellRenderer(renderer);
	}

	private JPanel getCenterPanel() {
		final JPanel panel = new JPanel(new BorderLayout());

		rightModel = new RefreshTableModel();
		rightTable = new JTable(rightModel);
		rightTable.addMouseListener(new RightTableMouseListener());
		rightTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		rightTable.getColumnModel().getColumn(rightModel.getColumnIndex(RefreshTableModel.EN_NAME))
				.setPreferredWidth(200);

		final DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
		renderer.setHorizontalAlignment(JLabel.CENTER);
		rightTable.getColumnModel().getColumn(rightModel.getColumnIndex(RefreshTableModel.LAST_UPDATE))
				.setCellRenderer(renderer);

		final JScrollPane tableScrollPane = new JScrollPane(rightTable);
		panel.add(tableScrollPane, BorderLayout.CENTER);

		final JLabel label = new JLabel("Edicoes para atualizar:", SwingConstants.CENTER);
		panel.add(label, BorderLayout.NORTH);

		removeAllButton = new JButton("Remover Todas");
		removeAllButton.addActionListener(new RemoveAllButtonListener());
		panel.add(removeAllButton, BorderLayout.SOUTH);

		return panel;
	}

	private Component getEastPanel() {
		final JPanel panel = new JPanel(new BorderLayout());
		panel.setPreferredSize(new Dimension(300, 1000));

		logPanel = new JTextArea();
		logPanel.setEditable(false);
		logPanel.setLineWrap(true);

		final JScrollPane scrollPane = new JScrollPane(logPanel);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		panel.add(scrollPane, BorderLayout.CENTER);

		final JLabel label = new JLabel("Progresso:", SwingConstants.CENTER);
		panel.add(label, BorderLayout.NORTH);

		refreshButton = new JButton("Atualizar");
		refreshButton.addActionListener(new RefreshButtonListener());

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

					final PricesTableModel leftModel = (PricesTableModel) leftTable.getModel();
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

					final PricesTableModel leftModel = (PricesTableModel) leftTable.getModel();
					leftModel.addRow(Arrays
							.asList(rightModel.getValueAt(convertedIndex, 0), rightModel.getValueAt(convertedIndex, 1))
							.toArray());
					rightModel.removeRow(convertedIndex);
				}
			}
		}
	}

	final class AddAllButtonListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			while (leftTable.getRowCount() > 0) {
				int convertedIndex = leftTable.convertRowIndexToModel(0);

				final PricesTableModel leftModel = (PricesTableModel) leftTable.getModel();
				rightModel.addRow(
						Arrays.asList(leftModel.getValueAt(convertedIndex, 0), leftModel.getValueAt(convertedIndex, 1))
								.toArray());
				leftModel.removeRow(convertedIndex);
			}
		}
	}

	final class RemoveAllButtonListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			while (rightTable.getRowCount() > 0) {
				int convertedIndex = rightTable.convertRowIndexToModel(0);

				final PricesTableModel leftModel = (PricesTableModel) leftTable.getModel();
				leftModel.addRow(Arrays
						.asList(rightModel.getValueAt(convertedIndex, 0), rightModel.getValueAt(convertedIndex, 1))
						.toArray());
				rightModel.removeRow(convertedIndex);
			}
		}
	}

	final class RefreshButtonListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			refreshButton.setText("Atualizando...");
			refreshButton.setEnabled(false);

			logPanel.setText("");

			timer = new Timer(3000, new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					if (scgReader.isDone()) {
						timer.stop();

						removeAllButton.doClick();

						configureLeftTable();
						leftTable.repaint();

						refreshButton.setText("Atualizar");
						refreshButton.setEnabled(true);
						refreshButton.repaint();
						
						try {
							final Player player = new Player(new FileInputStream(new File("FF7.mp3")));
							player.play();
						} catch (final JavaLayerException | FileNotFoundException e) {
							e.printStackTrace();
						}
					} else {
						scgReader.getEditionsThreads().forEach(thread -> {
							if (thread.isrRunning() && !thread.isFinished()) {
								String tmp = thread.getMessage();
								if (!logPanel.getText().contains(tmp)) {
									logPanel.append(tmp);
									logPanel.append("\n");
									logPanel.repaint();
								}
							}
						});
					}
				}
			});
			timer.setInitialDelay(3000);
			timer.setCoalesce(true);
			timer.start();

			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
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
				}
			});
		}
	}

}
