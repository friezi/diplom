package de.zintel.diplom.simulation;
import java.awt.Color;


/**
 * 
 */

/**
 * This Node is only for transfering/routing messages to final destinations.
 * 
 * @author Friedemann Zintel
 * 
 */
public class TransferNode extends Node {

	static Color BLOCKEDCOLOR = new Color((float) 0.5, 0, 0);

	SimParameters params;

	public TransferNode(int xp, int yp, SimParameters params) {
		super(xp, yp);
		this.params = params;
		setColor(Color.darkGray);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Node#init()
	 */
	@Override
	public void start() {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Node#receiveMessage(Message)
	 */

	@Override
	public void receiveMessage(Message m) {

		if ( m instanceof InternalMessage )
			handleInternalMessage((InternalMessage) m);
		else if ( m instanceof TransferMessage )
			handleTransferMessage((TransferMessage) m);
		else
			System.err.println("got message from unsupported type!");

	}

	protected void handleInternalMessage(InternalMessage m) {
		handleStandardMessage(m);
	}

	protected void handleTransferMessage(TransferMessage m) {

		if ( isBlocked() == true )
			return;

		if ( m.getDestination() == this ) {

			handleStandardMessage(m);

		} else {

			// forward the message to next node on the way
			m.forward(this, m.getDestination());

		}

	}

	protected void handleStandardMessage(Message m) {
	}

	public int size() {
		return 7;
	}

	protected void draw(java.awt.Graphics g, int x, int y) {

		g.setColor(color());
		int s = size();
		int x1 = x - (s) / 2;
		int width = s;
		int y1 = y - s / 2;
		int height = s;
		g.fillRect(x1, y1, width, height);
		g.setColor(java.awt.Color.black);
		g.drawRect(x1, y1, width, height);
		String t = text();
		java.awt.FontMetrics fm = g.getFontMetrics();
		g.setColor(textColor());
		g.drawString(t, x - fm.stringWidth(t) / 2, y + fm.getHeight() / 2);

		if ( isBlocked() == true ) {
			g.setColor(BLOCKEDCOLOR);
			crossit(g, x1, y1, width, height);
		}

	}

}
