package mtg.collection.view.prices;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;

public class PricesButton extends JButton {

	private static final long serialVersionUID = 1L;

	public PricesButton() {
		super("Atualizacao de Precos");
		addMouseListener(new MouseListener() {
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
				new PricesWindow();
			}
		});
	}

}
