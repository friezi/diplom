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

	JLabel rssServerLabel = new JLabel("RSSServer:");

	JLabel subscribersLabel = new JLabel("Subscribers:");

	JPanel rssPanel = new JPanel(new GridLayout(6, 1));

	JPanel subscribersPanel = new JPanel(new GridLayout(6, 1));

	JPanel contentPanel = new JPanel(new GridLayout(1, 2));

	protected class ReceivedRSSRequestsObserver implements Observer {

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Observer#update(java.util.Observable,
		 *      java.lang.Object)
		 */
		public void update(Observable observable, Object object) {
			// TODO Auto-generated method stub

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
			// TODO Auto-generated method stub

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
			// TODO Auto-generated method stub

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
			// TODO Auto-generated method stub

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
			// TODO Auto-generated method stub
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
			// TODO Auto-generated method stub
			Integer relSrvBrkRatio = (Integer) arg1;
			relSrvBrkRatioLabel.setText("rel. ratio: " + relSrvBrkRatio + "% feeds from network   ");
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

	protected class CloseWindowAdapter extends WindowAdapter {

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.awt.event.WindowAdapter#windowClosed(java.awt.event.WindowEvent)
		 */
		@Override
		public void windowClosed(WindowEvent arg0) {
			// TODO Auto-generated method stub
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

		// get values manually
		getReceivedRSSRequestsObserver().update(stat.getReceivedRSSFeedRequestObserver(), stat.getReceivedRSSRequests());
		getOmittedRSSRequestsObserver().update(stat.getOmittedRSSFeedRequestObserver(), stat.getOmittedRSSRequests());
		getServerFeedsObserver().update(stat.getServerFeedObserver(), stat.getServerFeeds());
		getBrokerFeedsObserver().update(stat.getBrokerFeedObserver(), stat.getBrokerFeeds());
		getReOmRatioObserver().update(stat.getReOmRatioUpdater(), stat.getReOmRatio());
		getSrvBrkRatioObserver().update(stat.getSrvBrkRatioUpdater(), stat.getSrvBrkRatio());
		getRelReOmRatioObserver().update(stat.getRelReOmRatioUpdater(), stat.getRelReOmRatio());
		getRelSrvBrkRatioObserver().update(stat.getRelSrvBrkRatioUpdater(), stat.getRelSrvBrkRatio());

		this.addWindowListener(new CloseWindowAdapter());

		// this.setPreferredSize(new Dimension(width, height));
		// this.setSize(new Dimension(width, height));

		rssPanel.add(rssServerLabel);
		rssPanel.add(new JSeparator());
		rssPanel.add(receivedLabel);
		rssPanel.add(omittedLabel);
		rssPanel.add(reOmRatioLabel);
		rssPanel.add(relReOmRatioLabel);

		rssPanel.setOpaque(true);

		subscribersPanel.add(subscribersLabel);
		subscribersPanel.add(new JSeparator());
		subscribersPanel.add(serverFeedsLabel);
		subscribersPanel.add(brokerFeedsLabel);
		subscribersPanel.add(srvBrRatioLabel);
		subscribersPanel.add(relSrvBrkRatioLabel);

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

}
