/**
 * 
 */

import java.util.*;
import java.lang.*;
import java.lang.reflect.*;
import rsspubsubframework.*;
import javax.swing.*;
import javax.swing.event.*;

import java.awt.*;

/**
 * @author Friedemann Zintel
 * 
 */

public class QueueingRSSServer extends RSSServer {

	protected class HandleRequestsTask implements Runnable {

		public void run() {

			boolean isEmpty = false;

			RSSFeedRequestMessage request;

			while (isEmpty == false) {

				synchronized (requestqueue) {

					getStatistics().setRequestsInQueue(requestqueue.size());

					request = requestqueue.getFirst();

				}

				try {

					processRSSFeedRequestMessage(request);

					int number;

					// also unreplied requests consume processing time
					synchronized (unrepliedRequests) {

						number = unrepliedRequests.size();
						unrepliedRequests.clear();

					}

					processUnrepliedRequests(number);

				} catch (Exception e) {
					System.err.println("HandleRequessTask: run(): caught Exception: " + e);
					System.exit(1);
				}

				synchronized (requestqueue) {

					requestqueue.removeFirst();
					isEmpty = requestqueue.isEmpty();

					getStatistics().setRequestsInQueue(requestqueue.size());

				}
			}

		}

	}

	LinkedList<RSSFeedRequestMessage> requestqueue = new LinkedList<RSSFeedRequestMessage>();

	LinkedList<RSSFeedRequestMessage> unrepliedRequests = new LinkedList<RSSFeedRequestMessage>();

	long numberUnrepliedRequests = 0;

	/**
	 * @param xp
	 * @param yp
	 * @param params
	 */
	public QueueingRSSServer(int xp, int yp, SimParameters params) {
		super(xp, yp, params);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see RSSServer#handleRSSFeedRequestMessage(RSSFeedRequestMessage)
	 */
	@Override
	protected void handleRSSFeedRequestMessage(RSSFeedRequestMessage rfrm) {

		boolean isEmpty;
		int rsize;

		// enqueue the new feed-request
		// all odd requests will be put to unrepliedRequests, they will consume
		// some processing-time
		synchronized (requestqueue) {

			if ( requestqueue.isEmpty() == true )
				isEmpty = true;
			else
				isEmpty = false;

			rsize = requestqueue.size();

			if ( rsize < params.serverQueueSize ) {

				requestqueue.addLast(rfrm);
				if ( rsize < params.serverQueueSize - 1 ) {
					numberUnrepliedRequests = 0;
					getStatistics().setUnrepliedRequests(numberUnrepliedRequests);
				}

			} else {
				synchronized (unrepliedRequests) {
					unrepliedRequests.addFirst(rfrm);
					numberUnrepliedRequests++;
					getStatistics().setUnrepliedRequests(numberUnrepliedRequests);
				}
			}
		}

		// submit super.handleRSSFeedRequestMessage()
		/*
		 * try { Class[] types = new Class[1]; types[0] =
		 * RSSFeedRequestMessage.class; if (isEmpty == true){ (new Thread(new
		 * HandleRequestsTask(this,
		 * RSSServer.class.getMethod("handleRSSFeedRequestMessage",
		 * types)))).start(); System.out.println("new Thread"); } } catch
		 * (Exception e) { System.err.println("QueueingRSSServer:
		 * handleRSSFeedRequestMessage(): caught Exception: " + e);
		 * System.exit(1); }
		 */

		// if necessary start a new task
		try {

			if ( isEmpty == true )
				new Thread(new HandleRequestsTask()).start();

		} catch (Exception e) {
			System.err.println("QueueingRSSServer: handleRSSFeedRequestMessage(): caught Exception: " + e);
			System.exit(1);
		}
	}

	protected void processRSSFeedRequestMessage(RSSFeedRequestMessage rfrm) {

		// observers (most probably the Engine) have to be informed about the
		// request
		this.getStatistics().addReceivedRSSFeedRequest(this);

		Message m;
		(m = new RSSFeedMessage(this, rfrm.getSrc(), getFeed(), getRssFeedRepresentationFactory().newRSSFeedRepresentation(null, getFeed()), params))
				.send();

		upload(m);

	}

	protected void processUnrepliedRequests(int number) {

		try {
			Thread.sleep((Engine.getSingleton().getTimerPeriod() * params.rssFeedMsgRT * number) / 8);
		} catch (Exception e) {
			System.out.println("Exception: " + e);
		}

	}

	protected class InfoWindow extends JFrame implements ChangeListener {

		int xpos = 200;

		int ypos = 200;

		int maxValue = 50;

		RSSServerNode rssserver;

		JPanel panel = new JPanel();

		JPanel sliderpanel = new JPanel(new GridLayout(2, 1));

		JSlider slider;

		protected InfoWindow(String titel, RSSServerNode rssserver) {

			super(titel);

			this.rssserver = rssserver;

			slider = new JSlider(0, maxValue, (int)(rssserver.getUploadScalingFactor()*10));
			slider.addChangeListener(this);
			slider.setMajorTickSpacing(10);
			slider.setMinorTickSpacing(1);
			slider.setPaintTicks(true);
			slider.setPaintLabels(true);
			//	        slider.setBorder(
			//	                BorderFactory.createEmptyBorder(0,0,10,0));

			sliderpanel.add(new JLabel("reply-delay scaling-factor * 10", JLabel.CENTER));
			sliderpanel.add(slider);

			panel.add(sliderpanel);

			this.setContentPane(panel);

			this.setResizable(false);

			this.setLocation(xpos, ypos);

			this.setAlwaysOnTop(true);

			this.pack();

		}

		/* (non-Javadoc)
		 * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
		 */
		public void stateChanged(ChangeEvent e) {
			// TODO Auto-generated method stub

			JSlider slider = (JSlider) e.getSource();

			if ( slider.getValueIsAdjusting() == false ) {
				rssserver.setUploadScalingFactor(((float) slider.getValue()) / 10);
				System.out.println("uploadScalingFactor: " + rssserver.getUploadScalingFactor());
			}

		}

	}

	/* (non-Javadoc)
	 * @see RSSServerNode#showInfo()
	 */
	@Override
	public void showInfo() {
		super.showInfo();
		new InfoWindow("RSSServer-Info", this).setVisible(true);
	}

	/*
	 * protected class HandleRequestsTask implements Runnable {
	 * 
	 * RSSServerNode server;
	 * 
	 * Method handleRoutine;
	 * 
	 * public HandleRequestsTask(RSSServerNode server, Method handleRoutine) {
	 * this.server = server; this.handleRoutine = handleRoutine; }
	 * 
	 * public void run() {
	 * 
	 * boolean isEmpty = false;
	 * 
	 * Object[] args = new Object[1];
	 * 
	 * while (isEmpty == false) {
	 * 
	 * RSSFeedRequestMessage request = requests.getFirst();
	 * 
	 * args[0] = request;
	 * 
	 * try { Thread.sleep(1000); handleRoutine.invoke(server, args); } catch
	 * (Exception e) { System.err.println("HandleRequessTask: run(): caught
	 * Exception: " + e); System.exit(1); }
	 * 
	 * synchronized (requests) { requests.removeFirst(); isEmpty =
	 * requests.isEmpty(); } } } }
	 */

}
