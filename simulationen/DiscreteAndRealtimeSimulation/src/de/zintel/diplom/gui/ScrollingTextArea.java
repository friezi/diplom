package de.zintel.diplom.gui;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * 
 */

@SuppressWarnings("serial")
class ScrollingTextArea extends JTextArea {

	private JScrollBar scrollbar;

	private JScrollPane scrollpane;

	public ScrollingTextArea() {

		super();
		scrollpane = new JScrollPane(this);
		scrollpane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollpane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		this.setLineWrap(true);
		scrollbar = scrollpane.getVerticalScrollBar();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.JTextArea#append(java.lang.String)
	 */
	@Override
	public void append(String arg0) {

		super.append(arg0);
		scrollbar.setValue(scrollbar.getMaximum());

	}

	/**
	 * @return Returns the scrollpane.
	 */
	public JScrollPane getScrollpane() {
		return scrollpane;
	}

}