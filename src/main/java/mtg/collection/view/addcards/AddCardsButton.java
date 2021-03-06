package mtg.collection.view.addcards;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;

public class AddCardsButton extends JButton {

	private static final long serialVersionUID = 1L;

	public AddCardsButton() {
		super("Adicionar Cartas a Colecao");
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
				new AddCardsWindow();
			}
		});
	}

}
