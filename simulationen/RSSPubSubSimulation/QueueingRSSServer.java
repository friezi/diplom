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
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * @author Friedemann Zintel
 * 
 */

public class QueueingRSSServer extends RSSServer {

	protected class HandleRequestsTask implements Runnable {

		public void run() {

			boolean isEmpty = false;

			RSSFeedRequestMessage request;

			while ( isEmpty == false ) {

				synchronized ( requestqueue ) {

					getStatistics().setRequestsInQueue(requestqueue.size());

					request = requestqueue.getFirst();

				}

				try {

					processRSSFeedRequestMessage(request);

					int number;

					// also unreplied requests consume processing time
					synchronized ( unrepliedRequests ) {

						number = unrepliedRequests.size();
						unrepliedRequests.clear();

					}

					getStatistics().setUnrepliedRequests(number);
					processUnrepliedRequests(number);

				} catch ( Exception e ) {
					System.err.println("HandleRequessTask: run(): caught Exception: " + e);
					System.exit(1);
				}

				synchronized ( requestqueue ) {

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
		synchronized ( requestqueue ) {

			if ( requestqueue.isEmpty() == true )
				isEmpty = true;
			else
				isEmpty = false;

			rsize = requestqueue.size();

			if ( rsize < params.serverQueueSize ) {

				requestqueue.addLast(rfrm);
				if ( rsize < params.serverQueueSize - 1 ) {
					// numberUnrepliedRequests = 0;
					// getStatistics().setUnrepliedRequests(numberUnrepliedRequests);
				}

			} else {
				synchronized ( unrepliedRequests ) {
					unrepliedRequests.addFirst(rfrm);
					// numberUnrepliedRequests++;
					// getStatistics().setUnrepliedRequests(numberUnrepliedRequests);
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

		} catch ( Exception e ) {
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
		} catch ( Exception e ) {
			System.out.println("Exception: " + e);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see RSSServerNode#showInfo()
	 */
	@Override
	public void showInfo() {
		super.showInfo();
		new InfoWindowExtension(infoWindow, this);
	}

	protected class InfoWindowExtension extends WindowAdapter implements ChangeListener {

		private class CloseWindowAdapter extends WindowAdapter {

			/*
			 * (non-Javadoc)
			 * 
			 * @see java.awt.event.WindowAdapter#windowClosed(java.awt.event.WindowEvent)
			 */
			@Override
			public void windowClosed(WindowEvent arg0) {
				deleteUploadScalingFactorObserver(uploadScalingFactorObserver);
				super.windowClosed(arg0);
			}

		}

		protected class UploadScalingFactorObserver implements Observer {

			public void update(Observable arg0, Object arg1) {

				Float value = (Float) arg1;
				sliderlabel.setText("reply-delay scaling-factor: " + value);

			}

		}

		RSSServerNode rssserver;

		int maxValue = 50;

		JLabel sliderlabel;

		UploadScalingFactorObserver uploadScalingFactorObserver = new UploadScalingFactorObserver();

		public InfoWindowExtension(JFrame baseWindow, RSSServerNode rssserver) {

			JPanel sliderpanel = new JPanel();

			sliderpanel.setLayout(new BoxLayout(sliderpanel, BoxLayout.Y_AXIS));

			JSlider slider;

			this.rssserver = rssserver;

			Hashtable<Integer, JLabel> labeltable = new Hashtable<Integer, JLabel>();

			labeltable.put(0, new JLabel("0"));
			labeltable.put(10, new JLabel("1.0"));
			labeltable.put(20, new JLabel("2.0"));
			labeltable.put(30, new JLabel("3.0"));
			labeltable.put(40, new JLabel("4.0"));
			labeltable.put(50, new JLabel("5.0"));

			slider = new JSlider(0, maxValue, (int) (rssserver.getUploadScalingFactor() * 10));
			slider.setLabelTable(labeltable);
			slider.addChangeListener(this);
			slider.setMajorTickSpacing(10);
			slider.setMinorTickSpacing(1);
			slider.setPaintTicks(true);
			slider.setPaintLabels(true);

			sliderlabel = new JLabel();

			sliderpanel.add(sliderlabel);
			sliderpanel.add(slider);
			sliderpanel.setBorder(BorderFactory.createEtchedBorder());

			baseWindow.getContentPane().add(sliderpanel);

			baseWindow.addWindowListener(new CloseWindowAdapter());

			addUploadScalingfactorObserver(uploadScalingFactorObserver);
			uploadScalingFactorObserver.update(getUploadScalingFactorNotifier(), getUploadScalingFactor());

			baseWindow.pack();

		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
		 */
		public void stateChanged(ChangeEvent e) {

			JSlider slider = (JSlider) e.getSource();

			if ( slider.getValueIsAdjusting() == false ) {
				rssserver.setUploadScalingFactor(((float) slider.getValue()) / 10);
			}

		}

		public void update(Observable arg0, Object arg1) {

		}

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
