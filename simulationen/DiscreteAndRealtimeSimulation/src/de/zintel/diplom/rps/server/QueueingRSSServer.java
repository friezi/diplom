package de.zintel.diplom.rps.server;
/**
 * 
 */

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.zintel.diplom.messages.RSSFeedMessage;
import de.zintel.diplom.messages.RSSFeedRequestMessage;
import de.zintel.diplom.messages.SeqAckRSSFeedMessage;
import de.zintel.diplom.messages.SequencedRSSFeedRequestMessage;
import de.zintel.diplom.simulation.InternalMessage;
import de.zintel.diplom.simulation.Node;
import de.zintel.diplom.simulation.SimParameters;
import de.zintel.diplom.util.ObserverJComponent;

/**
 * @author Friedemann Zintel
 * 
 */

public class QueueingRSSServer extends RSSServer {

	/**
	 * Only in case of discrete event-machine: indicates that the queue must be
	 * dispatched and that a RSSFeedMessage must be sent to subscriber
	 * 
	 * @author Friedemann Zintel
	 * 
	 */
	public static class DispatchQueueMessage extends InternalMessage {

		private RSSFeedMessage message;

		public DispatchQueueMessage(Node src, Node dst, RSSFeedMessage m, long arrivalTime) {
			super(src, dst, arrivalTime);
			message = m;
		}

		/**
		 * @return Returns the message.
		 */
		public RSSFeedMessage getMessage() {
			return message;
		}
	}

	// protected static class

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
					System.err.println("HandleRequestTask: run(): caught Exception: " + e);
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

	protected LinkedList<RSSFeedRequestMessage> requestqueue = new LinkedList<RSSFeedRequestMessage>();

	protected long numberOfQueueMembers = 0;

	protected LinkedList<RSSFeedRequestMessage> unrepliedRequests = new LinkedList<RSSFeedRequestMessage>();

	protected AbstractUploaderFactory uploaderfactory;

	protected AbstractUploader uploader;

	static long messagecounter = 0;

	protected long numberUnrepliedRequests = 0;

	/**
	 * @param xp
	 * @param yp
	 * @param params
	 */
	public QueueingRSSServer(int xp, int yp, SimParameters params) throws Exception {
		super(xp, yp, params);
		uploaderfactory = new AbstractUploaderFactory(params);
		uploader = uploaderfactory.newUploader(this, AbstractUploaderFactory.SERVER_U);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see RSSServer#handleRSSFeedRequestMessage(RSSFeedRequestMessage)
	 */
	@Override
	protected void handleRSSFeedRequestMessage(RSSFeedRequestMessage rfrm) {

		if ( params.isDiscreteSimulation() == false )
			threadedHandlingOfRSSFeedRequestMessage(rfrm);
		else
			discreteHandlingOfRSSFeedRequestMessage(rfrm);

	}

	protected void threadedHandlingOfRSSFeedRequestMessage(RSSFeedRequestMessage rfrm) {

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

			if ( rsize < params.getServerQueueSize() ) {

				requestqueue.addLast(rfrm);
				if ( rsize < params.getServerQueueSize() - 1 ) {
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

	protected void discreteHandlingOfRSSFeedRequestMessage(RSSFeedRequestMessage rfrm) {
		//
		// messagecounter++;
		//
		// System.out.println("Server: got " + messagecounter + ". message.");

		if ( numberOfQueueMembers < params.getServerQueueSize() ) {

			numberOfQueueMembers++;

			getStatistics().setRequestsInQueue(numberOfQueueMembers);

			processRSSFeedRequestMessage(rfrm);
			numberUnrepliedRequests = 0;
			getStatistics().setUnrepliedRequests(numberUnrepliedRequests);

		} else {

			((VirtualUploader) uploader).incNextIdleTime(getProcessingTimeUnrepliedRequests());
			numberUnrepliedRequests++;
			getStatistics().setUnrepliedRequests(numberUnrepliedRequests);

		}

		getStatistics().setTotalTemporaryRequests(numberOfQueueMembers + numberUnrepliedRequests);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see RSSServer#handleControlMessage(ControlMessage)
	 */
	@Override
	protected void handleInternalMessage(InternalMessage m) {

		if ( m instanceof DispatchQueueMessage ) {

			numberOfQueueMembers--;

			RSSFeedMessage rfm = ((DispatchQueueMessage) m).getMessage();
			rfm.setFeed(getFeed());
			rfm.resetArrivalTime();
			rfm.send();

			getStatistics().setRequestsInQueue(numberOfQueueMembers);

			getStatistics().setTotalTemporaryRequests(numberOfQueueMembers + numberUnrepliedRequests);

		} else {

			super.handleInternalMessage(m);

		}

	}

	protected void processRSSFeedRequestMessage(RSSFeedRequestMessage rfrm) {

		// observers (most probably the Engine) have to be informed about the
		// request
		this.getStatistics().addReceivedRSSFeedRequest(this);

		RSSFeedMessage rssfeed;

		if ( rfrm instanceof SequencedRSSFeedRequestMessage ) {

			SequencedRSSFeedRequestMessage srfrm = (SequencedRSSFeedRequestMessage) rfrm;

			(rssfeed = new SeqAckRSSFeedMessage(this, rfrm.getOrigin(), srfrm.getSequenceNumber(), srfrm.getRetransmissionSequenceNumber(), getFeed(),
					getRssFeedRepresentationFactory().newRSSFeedRepresentation(getFeed()), params)).send();
		} else
			(rssfeed = new RSSFeedMessage(this, rfrm.getOrigin(), getFeed(), getRssFeedRepresentationFactory().newRSSFeedRepresentation(getFeed()), params))
					.send();

		uploader.upload(rssfeed);

	}

	protected void processUnrepliedRequests(int number) {

		try {
			Thread.sleep(getProcessingTimeUnrepliedRequests() * number);
		} catch ( Exception e ) {
			System.out.println("Exception: " + e);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see RSSServerNode#setServiceTimeFactor(float)
	 */
	@Override
	public synchronized void setServiceTimeFactor(float serviceTimeFactor) {

		// all the arrivaltimes of the DispatchQueueMessage have to be
		// rescheduled
		if ( params.isDiscreteSimulation() == true )
			if ( serviceTimeFactor != getServiceTimeFactor() )
				((VirtualServerUploader) uploader).rescheduleCurrentUploads(serviceTimeFactor, getProcessingTimeUnrepliedRequests());

		super.setServiceTimeFactor(serviceTimeFactor);
	}

	protected long getProcessingTimeUnrepliedRequests() {
		return params.getProcessingTimeUnrepliedRequest();
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

				deleteServiceTimeFactorObserver(serviceTimeFactorObserver);
				feedDisplay.stopObserve();
				super.windowClosed(arg0);
			}

		}

		protected class ServiceTimeFactorObserver implements Observer {

			public void update(Observable arg0, Object arg1) {

				Float serviceTimeFactor = (Float) arg1;

				sliderlabel.setText("reply-delay scaling-factor: " + serviceTimeFactor);

				int value = (int) (serviceTimeFactor.floatValue() * 10);

				// this check is important in case of observers in a circle
				if ( value != slider.getValue() ) {

					if ( slider.getMaximum() < value )
						slider.setMaximum(value);
					slider.setValue(value);
				}

			}

		}

		RSSServerNode rssserver;

		int maxValue = 100;

		JLabel sliderlabel;

		JSlider slider;

		ObserverJComponent feedDisplay;

		ServiceTimeFactorObserver serviceTimeFactorObserver = new ServiceTimeFactorObserver();

		public InfoWindowExtension(JFrame baseWindow, RSSServerNode rssserver) {

			JPanel sliderpanel = new JPanel();

			sliderpanel.setLayout(new BoxLayout(sliderpanel, BoxLayout.Y_AXIS));

			JPanel eventspanel = new JPanel();

			eventspanel.setLayout(new BoxLayout(eventspanel, BoxLayout.X_AXIS));

			eventspanel.add(new JLabel("Events in feed: "));

			this.rssserver = rssserver;

			feedDisplay = rssserver.getRssFeedFactory().createAndStartNewFeedDisplay();

			eventspanel.add(feedDisplay);

			sliderpanel.add(eventspanel);

			Hashtable<Integer, JLabel> labeltable = new Hashtable<Integer, JLabel>();

			labeltable.put(0, new JLabel("0"));
			labeltable.put(10, new JLabel("1.0"));
			labeltable.put(20, new JLabel("2.0"));
			labeltable.put(30, new JLabel("3.0"));
			labeltable.put(40, new JLabel("4.0"));
			labeltable.put(50, new JLabel("5.0"));
			labeltable.put(60, new JLabel("6.0"));
			labeltable.put(70, new JLabel("7.0"));
			labeltable.put(80, new JLabel("8.0"));
			labeltable.put(90, new JLabel("9.0"));
			labeltable.put(100, new JLabel("10.0"));

			slider = new JSlider(0, Math.max(maxValue, (int) (rssserver.getServiceTimeFactor() * 10)), (int) (rssserver.getServiceTimeFactor() * 10));
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

			addServiceTimeFactorObserver(serviceTimeFactorObserver);
			serviceTimeFactorObserver.update(getServiceTimeFactorNotifier(), getServiceTimeFactor());

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
				rssserver.setServiceTimeFactor(((float) slider.getValue()) / 10);
			}

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
