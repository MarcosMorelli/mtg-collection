package mtg.collection.view;

import javax.swing.JTextPane;

public class JTextFieldLabel extends JTextPane {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public JTextFieldLabel(final String label) {
		super();
		setContentType("text/html");
		setText(label);
		setEditable(false);
		setBackground(null); 
		setBorder(null);
	}

}
