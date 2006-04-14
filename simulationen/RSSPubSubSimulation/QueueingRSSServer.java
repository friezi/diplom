/**
 * 
 */

import java.util.*;
import java.lang.*;
import java.lang.reflect.*;
import rsspubsubframework.*;

/**
 * @author Friedemann Zintel
 * 
 */

public class QueueingRSSServer extends RSSServer {

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
	 * public void run() { // TODO Auto-generated method stub
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
	protected class HandleRequestsTask implements Runnable {

		public void run() {
			// TODO Auto-generated method stub

			boolean isEmpty = false;

			RSSFeedRequestMessage request;

			while (isEmpty == false) {

				synchronized (requests) {

					getStatistics().setRequestsInQueue(requests.size());

					request = requests.getFirst();

				}

				try {

					processRSSFeedRequestMessage(request);

				} catch (Exception e) {
					System.err.println("HandleRequessTask: run(): caught Exception: " + e);
					System.exit(1);
				}

				synchronized (requests) {

					requests.removeFirst();
					isEmpty = requests.isEmpty();

					getStatistics().setRequestsInQueue(requests.size());

				}
			}

		}

	}

	LinkedList<RSSFeedRequestMessage> requests = new LinkedList<RSSFeedRequestMessage>();

	/**
	 * @param xp
	 * @param yp
	 * @param params
	 */
	public QueueingRSSServer(int xp, int yp, SimParameters params) {
		super(xp, yp, params);
		// TODO Auto-generated constructor stub
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see RSSServer#handleRSSFeedRequestMessage(RSSFeedRequestMessage)
	 */
	@Override
	protected void handleRSSFeedRequestMessage(RSSFeedRequestMessage rfrm) {

		boolean isEmpty;

		// enqueue the new feed
		synchronized (requests) {

			if ( requests.isEmpty() == true )
				isEmpty = true;
			else
				isEmpty = false;

			requests.addLast(rfrm);
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

		Message m = new RSSFeedMessage(this, rfrm.getSrc(), getFeed(), getRssFeedRepresentationFactory().newRSSFeedRepresentation(null, getFeed()),
				params);

		upload(m);

	}

}
