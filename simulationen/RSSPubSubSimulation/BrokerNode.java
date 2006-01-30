import rsspubsubframework.*;

public class BrokerNode extends Node {

	protected SimParameters params;

	/**
	 * @param xp
	 *            x-position
	 * @param yp
	 *            y-position
	 * @param params
	 *            relevat parameters
	 */
	public BrokerNode(int xp, int yp, SimParameters params) {
		super(xp, yp);
		this.params = params;
		setColor(new java.awt.Color((float) 0.5, (float) 0.5, 0));
		// TODO Auto-generated constructor stub
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void receiveMessage(Message m) {
		// TODO Auto-generated method stub

	}

	protected int size() {
		return 30;
	}

	protected void draw(java.awt.Graphics g, int x, int y) {

		g.setColor(color());
		int s = size();
		g.fillRect(x - s / 2, y - s / 2, s, s);
		g.setColor(java.awt.Color.black);
		g.drawRect(x - s / 2, y - s / 2, s, s);
		String t = text();
		java.awt.FontMetrics fm = g.getFontMetrics();
		g.setColor(textColor());
		g.drawString(t, x - fm.stringWidth(t) / 2, y + fm.getHeight() / 2);

	}

}
