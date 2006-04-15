/**
 * 
 */
package rsspubsubframework;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;

/**
 * A window for statistic-output
 * 
 * @author friezi
 * 
 */
public class StatisticWindow extends JFrame {

	int width = 300;

	int height = 100;

	int xpos = 300;

	int ypos = 450;

	JLabel receivedLabel = new JLabel();

	JLabel omittedLabel = new JLabel();

	JLabel reOmRatioLabel = new JLabel();

	JLabel serverFeedsLabel = new JLabel();

	JLabel brokerFeedsLabel = new JLabel();

	JLabel srvBrRatioLabel = new JLabel();

	JLabel relReOmRatioLabel = new JLabel();

	JLabel relSrvBrkRatioLabel = new JLabel();

	JLabel averageUpdateRatioLabel = new JLabel();

	JLabel delayedMessagesRatioLabel = new JLabel();

	JLabel averageMessageDelayRatioLabel = new JLabel();

	JLabel requestsInQueueLabel = new JLabel();

	JLabel unrepliedRequestsLabel = new JLabel();

	JLabel rssServerLabel = new JLabel("RSSServer:");

	JLabel subscribersLabel = new JLabel("Subscribers:");

	JPanel rssPanel = new JPanel(new GridLayout(9, 1));

	JPanel subscribersPanel = new JPanel(new GridLayout(9, 1));

	JPanel contentPanel = new JPanel(new GridLayout(1, 2));

	protected class ReceivedRSSRequestsObserver implements Observer {

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Observer#update(java.util.Observable,
		 *      java.lang.Object)
		 */
		public void update(Observable observable, Object object) {

			long receivedRSSRequests = (Long) object;
			receivedLabel.setText("received requests: " + receivedRSSRequests);

		}

	}

	protected class OmittedRSSRequestsObserver implements Observer {

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Observer#update(java.util.Observable,
		 *      java.lang.Object)
		 */
		public void update(Observable observable, Object object) {

			long omittedRSSRequests = (Long) object;
			omittedLabel.setText("omitted requests: " + omittedRSSRequests);

		}

	}

	protected class ServerFeedsObserver implements Observer {

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Observer#update(java.util.Observable,
		 *      java.lang.Object)
		 */
		public void update(Observable observable, Object object) {

			long serverFeeds = (Long) object;
			serverFeedsLabel.setText("feeds from server: " + serverFeeds);

		}

	}

	protected class BrokerFeedsObserver implements Observer {

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Observer#update(java.util.Observable,
		 *      java.lang.Object)
		 */
		public void update(Observable observable, Object object) {

			long brokerFeeds = (Long) object;
			brokerFeedsLabel.setText("feeds from broker: " + brokerFeeds);

		}

	}

	protected class ReOmRatioObserver implements Observer {

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Observer#update(java.util.Observable,
		 *      java.lang.Object)
		 */
		public void update(Observable arg0, Object arg1) {

			Integer reOmRatio = (Integer) arg1;
			reOmRatioLabel.setText("abs. ratio: " + reOmRatio + "% saved requests");

		}

	}

	protected class SrvBrkRatioObserver implements Observer {

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Observer#update(java.util.Observable,
		 *      java.lang.Object)
		 */
		public void update(Observable arg0, Object arg1) {
			Integer srvBrkRatio = (Integer) arg1;
			srvBrRatioLabel.setText("abs. ratio: " + srvBrkRatio + "% feeds from network   ");

		}

	}

	protected class RelReOmRatioObserver implements Observer {

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Observer#update(java.util.Observable,
		 *      java.lang.Object)
		 */
		public void update(Observable arg0, Object arg1) {
			Integer relReOmRatio = (Integer) arg1;
			relReOmRatioLabel.setText("rel. ratio: " + relReOmRatio + "% saved requests");
		}

	}

	protected class RelSrvBrkRatioObserver implements Observer {

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Observer#update(java.util.Observable,
		 *      java.lang.Object)
		 */
		public void update(Observable arg0, Object arg1) {
			Integer relSrvBrkRatio = (Integer) arg1;
			relSrvBrkRatioLabel.setText("rel. ratio: " + relSrvBrkRatio + "% feeds from network   ");
		}

	}

	protected class AverageUptodateRatioObserver implements Observer {

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Observer#update(java.util.Observable,
		 *      java.lang.Object)
		 */
		public void update(Observable arg0, Object arg1) {
			Integer averageUptodateratio = (Integer) arg1;
			averageUpdateRatioLabel.setText("avg. uptodate-ratio: " + averageUptodateratio + "%");
		}

	}

	protected class DelayedMessagesRatioObserver implements Observer {

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Observer#update(java.util.Observable,
		 *      java.lang.Object)
		 */
		public void update(Observable arg0, Object arg1) {
			Integer delayedMessagesRatio = (Integer) arg1;
			delayedMessagesRatioLabel.setText("delayed-messages-ratio: " + delayedMessagesRatio + "%");
		}

	}

	protected class AverageMessageDelayRatioObserver implements Observer {

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Observer#update(java.util.Observable,
		 *      java.lang.Object)
		 */
		public void update(Observable arg0, Object arg1) {
			Integer averageMessageDelayRatio = (Integer) arg1;
			averageMessageDelayRatioLabel.setText("avg. message-delay-ratio: " + averageMessageDelayRatio
					+ "%");
		}

	}

	protected class RequestsInQueueObserver implements Observer {

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Observer#update(java.util.Observable,
		 *      java.lang.Object)
		 */
		public void update(Observable arg0, Object arg1) {
			Long requestsInQueue = (Long) arg1;
			requestsInQueueLabel.setText("requests in Queue: " + requestsInQueue);
		}

	}

	protected class UnrepliedRequestsObserver implements Observer {

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Observer#update(java.util.Observable,
		 *      java.lang.Object)
		 */
		public void update(Observable arg0, Object arg1) {
			Long unrepliedRequests = (Long) arg1;
			unrepliedRequestsLabel.setText("unreplied requests: " + unrepliedRequests);
		}

	}

	private ReceivedRSSRequestsObserver receivedRSSRequestsObserver = new ReceivedRSSRequestsObserver();

	private OmittedRSSRequestsObserver omittedRSSRequestsObserver = new OmittedRSSRequestsObserver();

	private ServerFeedsObserver serverFeedsObserver = new ServerFeedsObserver();

	private BrokerFeedsObserver brokerFeedsObserver = new BrokerFeedsObserver();

	private ReOmRatioObserver reOmRatioObserver = new ReOmRatioObserver();

	private SrvBrkRatioObserver srvBrkRatioObserver = new SrvBrkRatioObserver();

	private RelReOmRatioObserver relReOmRatioObserver = new RelReOmRatioObserver();

	private RelSrvBrkRatioObserver relSrvBrkRatioObserver = new RelSrvBrkRatioObserver();

	private AverageUptodateRatioObserver averageUptodateRatioObserver = new AverageUptodateRatioObserver();

	private DelayedMessagesRatioObserver delayedMessagesRatioObserver = new DelayedMessagesRatioObserver();

	private AverageMessageDelayRatioObserver averageMessageDelayRatioObserver = new AverageMessageDelayRatioObserver();

	private RequestsInQueueObserver requestsInQueueObserver = new RequestsInQueueObserver();

	private UnrepliedRequestsObserver unrepliedRequestsObserver = new UnrepliedRequestsObserver();

	protected class CloseWindowAdapter extends WindowAdapter {

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.awt.event.WindowAdapter#windowClosed(java.awt.event.WindowEvent)
		 */
		@Override
		public void windowClosed(WindowEvent arg0) {
			// remove observers
			RPSStatistics stat = Engine.getSingleton().getRpsStatistics();
			stat.getReceivedRSSFeedRequestObserver().deleteObserver(getOmittedRSSRequestsObserver());
			stat.getOmittedRSSFeedRequestObserver().deleteObserver(getReceivedRSSRequestsObserver());
			stat.getServerFeedObserver().deleteObserver(getServerFeedsObserver());
			stat.getBrokerFeedObserver().deleteObserver(getBrokerFeedsObserver());
			stat.getReOmRatioUpdater().deleteObserver(getReOmRatioObserver());
			stat.getSrvBrkRatioUpdater().deleteObserver(getSrvBrkRatioObserver());
			stat.getRelReOmRatioUpdater().deleteObserver(getRelReOmRatioObserver());
			stat.getRelSrvBrkRatioUpdater().deleteObserver(getRelSrvBrkRatioObserver());
			stat.getAverageUptodateRatioUpdater().deleteObserver(getAverageUptodateRatioObserver());
			stat.getDelayedMessagesRatioNotifier().deleteObserver(getDelayedMessagesRatioObserver());
			stat.getAverageMessageDelayRatioUpdater().deleteObserver(getAverageMessageDelayRatioObserver());
			stat.getRequestsInQueueObserver().deleteObserver(getRequestsInQueueObserver());
			stat.getUnrepliedRequestsObserver().deleteObserver(getUnrepliedRequestsObserver());

			super.windowClosed(arg0);
		}

	}

	public StatisticWindow(String title) {

		super(title);

		RPSStatistics stat = Engine.getSingleton().getRpsStatistics();

		// add observers
		stat.getReceivedRSSFeedRequestObserver().addObserver(getReceivedRSSRequestsObserver());
		stat.getOmittedRSSFeedRequestObserver().addObserver(getOmittedRSSRequestsObserver());
		stat.getServerFeedObserver().addObserver(getServerFeedsObserver());
		stat.getBrokerFeedObserver().addObserver(getBrokerFeedsObserver());
		stat.getReOmRatioUpdater().addObserver(getReOmRatioObserver());
		stat.getSrvBrkRatioUpdater().addObserver(getSrvBrkRatioObserver());
		stat.getRelReOmRatioUpdater().addObserver(getRelReOmRatioObserver());
		stat.getRelSrvBrkRatioUpdater().addObserver(getRelSrvBrkRatioObserver());
		stat.getAverageUptodateRatioUpdater().addObserver(getAverageUptodateRatioObserver());
		stat.getDelayedMessagesRatioNotifier().addObserver(getDelayedMessagesRatioObserver());
		stat.getAverageMessageDelayRatioUpdater().addObserver(getAverageMessageDelayRatioObserver());
		stat.getRequestsInQueueObserver().addObserver(getRequestsInQueueObserver());
		stat.getUnrepliedRequestsObserver().addObserver(getUnrepliedRequestsObserver());

		// get values manually
		getReceivedRSSRequestsObserver().update(stat.getReceivedRSSFeedRequestObserver(),
				stat.getReceivedRSSRequests());
		getOmittedRSSRequestsObserver().update(stat.getOmittedRSSFeedRequestObserver(),
				stat.getOmittedRSSRequests());
		getServerFeedsObserver().update(stat.getServerFeedObserver(), stat.getServerFeeds());
		getBrokerFeedsObserver().update(stat.getBrokerFeedObserver(), stat.getBrokerFeeds());
		getReOmRatioObserver().update(stat.getReOmRatioUpdater(), stat.getReOmRatio());
		getSrvBrkRatioObserver().update(stat.getSrvBrkRatioUpdater(), stat.getSrvBrkRatio());
		getRelReOmRatioObserver().update(stat.getRelReOmRatioUpdater(), stat.getRelReOmRatio());
		getRelSrvBrkRatioObserver().update(stat.getRelSrvBrkRatioUpdater(), stat.getRelSrvBrkRatio());
		getAverageUptodateRatioObserver().update(stat.getAverageUptodateRatioUpdater(),
				stat.getAverageUptodateRatio());
		getDelayedMessagesRatioObserver().update(stat.getDelayedMessagesRatioNotifier(),
				stat.getDelayedMessagesRatio());
		getAverageMessageDelayRatioObserver().update(stat.getAverageMessageDelayRatioUpdater(),
				stat.getAverageMessageDelayRatio());
		getRequestsInQueueObserver().update(stat.getRequestsInQueueObserver(), stat.getRequestsInQueue());
		getUnrepliedRequestsObserver().update(stat.getUnrepliedRequestsObserver(),
				stat.getUnrepliedRequests());

		this.addWindowListener(new CloseWindowAdapter());

		// this.setPreferredSize(new Dimension(width, height));
		// this.setSize(new Dimension(width, height));

		rssPanel.add(rssServerLabel);
		rssPanel.add(new JSeparator());
		rssPanel.add(receivedLabel);
		rssPanel.add(omittedLabel);
		rssPanel.add(reOmRatioLabel);
		rssPanel.add(relReOmRatioLabel);
		rssPanel.add(requestsInQueueLabel);
		rssPanel.add(unrepliedRequestsLabel);

		rssPanel.setOpaque(true);

		subscribersPanel.add(subscribersLabel);
		subscribersPanel.add(new JSeparator());
		subscribersPanel.add(serverFeedsLabel);
		subscribersPanel.add(brokerFeedsLabel);
		subscribersPanel.add(srvBrRatioLabel);
		subscribersPanel.add(relSrvBrkRatioLabel);
		subscribersPanel.add(averageUpdateRatioLabel);
		subscribersPanel.add(delayedMessagesRatioLabel);
		subscribersPanel.add(averageMessageDelayRatioLabel);

		subscribersPanel.setOpaque(true);

		contentPanel.add(rssPanel);
		contentPanel.add(subscribersPanel);

		contentPanel.setOpaque(true);

		this.setContentPane(contentPanel);

		this.setResizable(false);

		this.setLocation(xpos, ypos);

		this.setAlwaysOnTop(true);

		this.pack();
	}

	/**
	 * @return Returns the omittedRSSRequestsObserver.
	 */
	public OmittedRSSRequestsObserver getOmittedRSSRequestsObserver() {
		return omittedRSSRequestsObserver;
	}

	/**
	 * @return Returns the receivedRSSRequestsObserver.
	 */
	public ReceivedRSSRequestsObserver getReceivedRSSRequestsObserver() {
		return receivedRSSRequestsObserver;
	}

	/**
	 * @return Returns the brokerFeedsObserver.
	 */
	public BrokerFeedsObserver getBrokerFeedsObserver() {
		return brokerFeedsObserver;
	}

	/**
	 * @return Returns the serverFeedsObserver.
	 */
	public ServerFeedsObserver getServerFeedsObserver() {
		return serverFeedsObserver;
	}

	/**
	 * @return Returns the reOmRatioObserver.
	 */
	public ReOmRatioObserver getReOmRatioObserver() {
		return reOmRatioObserver;
	}

	/**
	 * @return Returns the srvBrkRatioObserver.
	 */
	public SrvBrkRatioObserver getSrvBrkRatioObserver() {
		return srvBrkRatioObserver;
	}

	/**
	 * @return Returns the relReOmRatioObserver.
	 */
	public RelReOmRatioObserver getRelReOmRatioObserver() {
		return relReOmRatioObserver;
	}

	/**
	 * @return Returns the relSrvBrkRatioObserver.
	 */
	public RelSrvBrkRatioObserver getRelSrvBrkRatioObserver() {
		return relSrvBrkRatioObserver;
	}

	/**
	 * @return Returns the averageUptodateRatioObserver.
	 */
	public AverageUptodateRatioObserver getAverageUptodateRatioObserver() {
		return averageUptodateRatioObserver;
	}

	/**
	 * @return Returns the delayedMessagesRatioObserver.
	 */
	public DelayedMessagesRatioObserver getDelayedMessagesRatioObserver() {
		return delayedMessagesRatioObserver;
	}

	/**
	 * @return Returns the averageMessageDelayRatioObserver.
	 */
	public AverageMessageDelayRatioObserver getAverageMessageDelayRatioObserver() {
		return averageMessageDelayRatioObserver;
	}

	/**
	 * @return Returns the requestsInQueueObserver.
	 */
	public RequestsInQueueObserver getRequestsInQueueObserver() {
		return requestsInQueueObserver;
	}

	/**
	 * @return Returns the unrepliedRequestsObserver.
	 */
	public UnrepliedRequestsObserver getUnrepliedRequestsObserver() {
		return unrepliedRequestsObserver;
	}

}
