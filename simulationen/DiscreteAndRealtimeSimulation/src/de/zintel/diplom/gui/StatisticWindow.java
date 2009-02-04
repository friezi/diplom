package de.zintel.diplom.gui;
/**
 * 
 */

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashSet;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

import de.zintel.diplom.simulation.Engine;
import de.zintel.diplom.statistics.RPSStatistics;

/**
 * A window for statistic-output
 * 
 * @author Friedemann Zintel
 * 
 */
@SuppressWarnings("serial")
public class StatisticWindow extends JFrame implements ActionListener {

	protected class GraphViewIcon implements Icon {

		boolean display;

		protected int width = 7;

		protected int height = 7;

		int xshift = 2;

		int yshift = 2;

		GraphViewIcon(boolean display) {
			this.display = display;
		}

		public void paintIcon(Component component, Graphics g, int x, int y) {

			if ( display == true ) {

				g.setColor(Color.black);

				// y-arrow
				g.drawLine(x + (getIconWidth() / 2) - xshift, y, x + (getIconWidth() / 2) - xshift, y + getIconHeight() - 1);
				g.drawLine(x + (getIconWidth() / 2) - xshift, y, x + (getIconWidth() / 2) - xshift - 1, y + 1);
				g.drawLine(x + (getIconWidth() / 2) - xshift, y, x + (getIconWidth() / 2) - xshift + 1, y + 1);

				// x-arrow
				g.drawLine(x, y + (getIconHeight() / 2) + yshift, x + getIconWidth() - 1, y + (getIconHeight() / 2) + yshift);
				g.drawLine(x + getIconWidth() - 1, y + (getIconHeight() / 2) + yshift, x + getIconWidth() - 2, y + (getIconHeight() / 2) + yshift - 1);
				g.drawLine(x + getIconWidth() - 1, y + (getIconHeight() / 2) + yshift, x + getIconWidth() - 2, y + (getIconHeight() / 2) + yshift + 1);

				g.setColor(Color.red);
				g.drawLine(x + (getIconWidth() / 2) - 2, y + (getIconHeight() / 2) + 2, x + (getIconWidth() / 2) + 2, y + (getIconHeight() / 2) - 2);

			}

		}

		public int getIconWidth() {
			return width;
		}

		public int getIconHeight() {
			return height;
		}

	}

	protected class GraphButton extends JButton {

		boolean active;

		GraphButton(boolean active) {

			super(new GraphViewIcon(active));
			do_settings(active);

		}

		GraphButton(boolean active, String actionCommand) {

			super(new GraphViewIcon(active));
			setActionCommand(actionCommand);
			do_settings(active);

		}

		void do_settings(boolean active) {

			this.active = active;

			if ( active == false ) {

				setEnabled(active);
				setBorder(new EmptyBorder(0, 6, 0, 0));

			} else {

				addActionListener(statisticWindow);

			}

			setMargin(new Insets(3, 3, 3, 3));

		}

	}

	protected class ValuePanel extends JPanel {

		ValuePanel(GraphButton button, JComponent component) {

			super();

			setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

			add(button);
			add(component);

			component.setBorder(new EmptyBorder(0, 2, 0, 2));

			setBorder(new EmptyBorder(0, 2, 0, 1));
		}

	}

	int width = 300;

	int height = 100;

	int xpos = 300;

	int ypos = 450;

	private JLabel receivedLabel = new JLabel();

	private JLabel omittedLabel = new JLabel();

	private JLabel reOmRatioLabel = new JLabel();

	private JLabel serverFeedsLabel = new JLabel();

	private JLabel brokerFeedsLabel = new JLabel();

	private JLabel srvBrRatioLabel = new JLabel();

	private JLabel relReOmRatioLabel = new JLabel();

	public static final String relReOmRatioActionCmd = "relReOmRatioActionCmd";

	private JLabel relSrvBrkRatioLabel = new JLabel();

	private JLabel averageUptodateRatioLabel = new JLabel();

	public static final String averageUptodateRatioActionCmd = "averageUpdateRatioActionCmd";

	private JLabel delayedMessagesRatioLabel = new JLabel();

	private JLabel averageMessageDelayRatioLabel = new JLabel();

	public static final String averageMessageDelayRatioActionCmd = "averageMessageDelayRatioActionCmd";

	private JLabel requestsInQueueLabel = new JLabel();

	private JLabel unrepliedRequestsLabel = new JLabel();

	private JLabel viewTotalTemporaryRequestsLabel = new JLabel("view total temporary requests");

	public static final String totalTemporaryRequestsActionCmd = "totalTemporaryRequestsActionCmd";

	private JLabel meanValueCPPLabel = new JLabel();

	public static final String meanValueCPPActionCmd = "meanValueCPPLabel";

	private JLabel meanDvtCPPLabel = new JLabel();

	public static final String meanDvtCPPActionCmd = "meanDvtCPPActionCmd";

	private JLabel meanDvtCoeffCPPLabel = new JLabel();

	public static final String meanDvtCoeffCPPActionCmd = "meanDvtCoeffCPPLabel";

	private JLabel rssServerLabel = new JLabel("RSSServer:");

	private JLabel subscribersLabel = new JLabel("Subscribers:");

	private JLabel timeLabel = new JLabel();

	private JPanel rssPanel = new JPanel(new GridLayout(12, 1));

	private JPanel subscribersPanel = new JPanel(new GridLayout(12, 1));

	private JPanel nodeInfoPanel = new JPanel(new GridLayout(1, 2));

	private JPanel timePanel = new JPanel(new GridLayout(1, 1));

	private JPanel contentPane = new JPanel();

	protected HashSet<String> graphButtonActions = new HashSet<String>();

	protected StatisticWindow statisticWindow = null;

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
			averageUptodateRatioLabel.setText("avg. uptodate-ratio: " + averageUptodateratio + "%");
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
			averageMessageDelayRatioLabel.setText("avg. message-delay-ratio: " + averageMessageDelayRatio + "%");
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

	protected class MeanValueCPPObserver implements Observer {

		public void update(Observable arg0, Object arg1) {
			Long meanValueCPP = (Long) arg1;
			meanValueCPPLabel.setText("meanValue Curr.Poll.Pd: " + meanValueCPP);

		}

	}

	protected class StandardDeviationCPPObserver implements Observer {

		public void update(Observable arg0, Object arg1) {
			Long meanDeviationCurrPollPeriods = (Long) arg1;
			meanDvtCPPLabel.setText("std.dev. Curr.Poll.Pd.: " + meanDeviationCurrPollPeriods);

		}

	}

	protected class CoefficientOfVarianceCPPObserver implements Observer {

		public void update(Observable arg0, Object arg1) {
			Float meanDeviationCoefficientCPP = (Float) arg1;
			meanDvtCoeffCPPLabel.setText("coeff.var. CPP: " + String.format("%1.3f", meanDeviationCoefficientCPP));

		}

	}

	protected class TimeObserver implements Observer {

		public void update(Observable arg0, Object value) {
			Long time = (Long) value;
			timeLabel.setText("Time/ticks: " + time / 1000);
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

	private MeanValueCPPObserver meanValueCPPObserver = new MeanValueCPPObserver();

	private StandardDeviationCPPObserver standardDeviationCPPObserver = new StandardDeviationCPPObserver();

	private CoefficientOfVarianceCPPObserver coefficientOfVarianceCPPObserver = new CoefficientOfVarianceCPPObserver();

	private TimeObserver timeObserver = new TimeObserver();

	protected class CloseWindowAdapter extends WindowAdapter {

		StatisticWindow window;

		public CloseWindowAdapter(StatisticWindow window) {
			this.window = window;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.awt.event.WindowAdapter#windowClosed(java.awt.event.WindowEvent)
		 */
		@Override
		public void windowClosed(WindowEvent arg0) {
			// remove observers
			RPSStatistics stat = Engine.getSingleton().getRpsStatistics();
			RPSStatistics.deleteNotifierObserver(stat.getReceivedRSSFeedRequestObserver(), getOmittedRSSRequestsObserver());
			RPSStatistics.deleteNotifierObserver(stat.getOmittedRSSFeedRequestObserver(), getReceivedRSSRequestsObserver());
			RPSStatistics.deleteNotifierObserver(stat.getServerFeedObserver(), getServerFeedsObserver());
			RPSStatistics.deleteNotifierObserver(stat.getBrokerFeedObserver(), getBrokerFeedsObserver());
			RPSStatistics.deleteNotifierObserver(stat.getReOmRatioUpdater(), getReOmRatioObserver());
			RPSStatistics.deleteNotifierObserver(stat.getSrvBrkRatioUpdater(), getSrvBrkRatioObserver());
			stat.deleteRelReOmRatioObserver(getRelReOmRatioObserver());
			RPSStatistics.deleteNotifierObserver(stat.getRelSrvBrkRatioUpdater(), getRelSrvBrkRatioObserver());
			stat.deleteAverageUptodateRatioObserver(getAverageUptodateRatioObserver());
			RPSStatistics.deleteNotifierObserver(stat.getDelayedMessagesRatioNotifier(), getDelayedMessagesRatioObserver());
			RPSStatistics.deleteNotifierObserver(stat.getAverageMessageDelayRatioUpdater(), getAverageMessageDelayRatioObserver());
			RPSStatistics.deleteNotifierObserver(stat.getRequestsInQueueObserver(), getRequestsInQueueObserver());
			RPSStatistics.deleteNotifierObserver(stat.getUnrepliedRequestsObserver(), getUnrepliedRequestsObserver());
			stat.deleteMeanValueCPPObserver(getMeanValueCPPObserver());
			stat.deleteStdDevCPPObserver(getStandardDeviationCPPObserver());
			stat.deleteCoeffOfVarCPPObserver(getCoefficientOfVarianceCPPObserver());
			Engine.getSingleton().deleteTimeObserver(timeObserver);

			Engine.getSingleton().removeWindow(window);

			super.windowClosed(arg0);
		}

	}

	public StatisticWindow(String title) {

		super(title);

		statisticWindow = this;

		graphButtonActions.add(totalTemporaryRequestsActionCmd);
		graphButtonActions.add(averageUptodateRatioActionCmd);
		graphButtonActions.add(averageMessageDelayRatioActionCmd);
		graphButtonActions.add(relReOmRatioActionCmd);
		graphButtonActions.add(meanDvtCoeffCPPActionCmd);
		graphButtonActions.add(meanDvtCPPActionCmd);
		graphButtonActions.add(meanValueCPPActionCmd);

		RPSStatistics stat = Engine.getSingleton().getRpsStatistics();

		Engine.getSingleton().addWindow(this);

		// get values manually
		getReceivedRSSRequestsObserver().update(stat.getReceivedRSSFeedRequestObserver(), stat.getReceivedRSSRequests());
		getOmittedRSSRequestsObserver().update(stat.getOmittedRSSFeedRequestObserver(), stat.getOmittedRSSRequests());
		getServerFeedsObserver().update(stat.getServerFeedObserver(), stat.getServerFeeds());
		getBrokerFeedsObserver().update(stat.getBrokerFeedObserver(), stat.getBrokerFeeds());
		getReOmRatioObserver().update(stat.getReOmRatioUpdater(), stat.getReOmRatio());
		getSrvBrkRatioObserver().update(stat.getSrvBrkRatioUpdater(), stat.getSrvBrkRatio());
		getRelReOmRatioObserver().update(stat.getRelReOmRatioUpdater(), stat.getRelReOmRatio());
		getRelSrvBrkRatioObserver().update(stat.getRelSrvBrkRatioUpdater(), stat.getRelSrvBrkRatio());
		getAverageUptodateRatioObserver().update(stat.getAverageUptodateRatioUpdater(), stat.getAverageUptodateRatio());
		getDelayedMessagesRatioObserver().update(stat.getDelayedMessagesRatioNotifier(), stat.getDelayedMessagesRatio());
		getAverageMessageDelayRatioObserver().update(stat.getAverageMessageDelayRatioUpdater(), stat.getAverageMessageDelayRatio());
		getRequestsInQueueObserver().update(stat.getRequestsInQueueObserver(), stat.getRequestsInQueue());
		getUnrepliedRequestsObserver().update(stat.getUnrepliedRequestsObserver(), stat.getUnrepliedRequests());
		getMeanValueCPPObserver().update(stat.getMeanValueCPPUpdater(), stat.getMeanValueCurrPollPeriods());
		getStandardDeviationCPPObserver().update(stat.getStdDevCPPUpdater(), stat.getStdDevCurrPollPeriods());
		getCoefficientOfVarianceCPPObserver().update(stat.getCoeffOfVarCPPUpdater(), stat.getCoeffVarCurrPollPeriods());
		getTimeObserver().update(Engine.getSingleton().getTimeNotifier(), Engine.getSingleton().getTime());

		// add observers
		RPSStatistics.addNotifierObserver(stat.getReceivedRSSFeedRequestObserver(), getReceivedRSSRequestsObserver());
		RPSStatistics.addNotifierObserver(stat.getOmittedRSSFeedRequestObserver(), getOmittedRSSRequestsObserver());
		RPSStatistics.addNotifierObserver(stat.getServerFeedObserver(), getServerFeedsObserver());
		RPSStatistics.addNotifierObserver(stat.getBrokerFeedObserver(), getBrokerFeedsObserver());
		RPSStatistics.addNotifierObserver(stat.getReOmRatioUpdater(), getReOmRatioObserver());
		RPSStatistics.addNotifierObserver(stat.getSrvBrkRatioUpdater(), getSrvBrkRatioObserver());
		stat.addRelReOmRatioObserver(getRelReOmRatioObserver());
		RPSStatistics.addNotifierObserver(stat.getRelSrvBrkRatioUpdater(), getRelSrvBrkRatioObserver());
		stat.addAverageUptodateRatioObserver(getAverageUptodateRatioObserver());
		RPSStatistics.addNotifierObserver(stat.getDelayedMessagesRatioNotifier(), getDelayedMessagesRatioObserver());
		RPSStatistics.addNotifierObserver(stat.getAverageMessageDelayRatioUpdater(), getAverageMessageDelayRatioObserver());
		RPSStatistics.addNotifierObserver(stat.getRequestsInQueueObserver(), getRequestsInQueueObserver());
		RPSStatistics.addNotifierObserver(stat.getUnrepliedRequestsObserver(), getUnrepliedRequestsObserver());
		stat.addMeanValueCPPObserver(getMeanValueCPPObserver());
		stat.addStdDevCPPObserver(getStandardDeviationCPPObserver());
		stat.addCoeffOfVarCPPObserver(getCoefficientOfVarianceCPPObserver());
		Engine.getSingleton().addTimeObserver(getTimeObserver());

		this.addWindowListener(new CloseWindowAdapter(this));

		// this.setPreferredSize(new Dimension(width, height));
		// this.setSize(new Dimension(width, height));

		rssPanel.add(rssServerLabel);
		rssPanel.add(new JSeparator());
		rssPanel.add(new ValuePanel(new GraphButton(false), receivedLabel));

		rssPanel.add(new ValuePanel(new GraphButton(false), omittedLabel));

		rssPanel.add(new ValuePanel(new GraphButton(false), reOmRatioLabel));
		rssPanel.add(new ValuePanel(new GraphButton(true, relReOmRatioActionCmd), relReOmRatioLabel));

		rssPanel.add(new ValuePanel(new GraphButton(false), requestsInQueueLabel));
		rssPanel.add(new ValuePanel(new GraphButton(false), unrepliedRequestsLabel));

		rssPanel.add(new ValuePanel(new GraphButton(true, totalTemporaryRequestsActionCmd), viewTotalTemporaryRequestsLabel));

		rssPanel.setOpaque(true);

		subscribersPanel.add(subscribersLabel);
		subscribersPanel.add(new JSeparator());
		subscribersPanel.add(new ValuePanel(new GraphButton(false), serverFeedsLabel));
		subscribersPanel.add(new ValuePanel(new GraphButton(false), brokerFeedsLabel));
		subscribersPanel.add(new ValuePanel(new GraphButton(false), srvBrRatioLabel));
		subscribersPanel.add(new ValuePanel(new GraphButton(false), relSrvBrkRatioLabel));
		subscribersPanel.add(new ValuePanel(new GraphButton(true, averageUptodateRatioActionCmd), averageUptodateRatioLabel));
		subscribersPanel.add(new ValuePanel(new GraphButton(false), delayedMessagesRatioLabel));
		subscribersPanel.add(new ValuePanel(new GraphButton(true, averageMessageDelayRatioActionCmd), averageMessageDelayRatioLabel));
		subscribersPanel.add(new ValuePanel(new GraphButton(true, meanValueCPPActionCmd), meanValueCPPLabel));
		subscribersPanel.add(new ValuePanel(new GraphButton(true, meanDvtCPPActionCmd), meanDvtCPPLabel));
		subscribersPanel.add(new ValuePanel(new GraphButton(true, meanDvtCoeffCPPActionCmd), meanDvtCoeffCPPLabel));

		subscribersPanel.setOpaque(true);

		nodeInfoPanel.add(rssPanel);
		nodeInfoPanel.add(subscribersPanel);

		nodeInfoPanel.setOpaque(true);

		// for future use
		timePanel.add(timeLabel);

		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
		contentPane.add(timePanel);
		contentPane.add(nodeInfoPanel);
		contentPane.setOpaque(true);

		this.setContentPane(contentPane);

		this.setResizable(false);

		this.setLocation(xpos, ypos);

		this.setAlwaysOnTop(true);

		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		this.pack();

		this.setVisible(true);
	}

	public void actionPerformed(ActionEvent e) {

		if ( isGraphButton(e.getActionCommand()) ) {

			String title;
			RPSStatistics stats = Engine.getSingleton().getRpsStatistics();
			int timeLimitKTicks = (int) Engine.getSingleton().getTimeLimit() / 1000;

			try {

				if ( e.getActionCommand().equals(StatisticWindow.totalTemporaryRequestsActionCmd) ) {

					title = "total temporary requests";
					new TotTempReqGraphViewWindow(new RPSStatistics.GnuplotProviderNotifier(stats.getTotalTemporaryRequestsObserver(), stats
							.getGnuplotproviderTotTempReq()), title, timeLimitKTicks);

				} else if ( e.getActionCommand().equals(StatisticWindow.averageMessageDelayRatioActionCmd) ) {

					title = "average message-delay ratio";
					new GraphViewWindow(new RPSStatistics.GnuplotProviderNotifier(stats.getAverageMessageDelayRatioUpdater(), stats
							.getGnuplotproviderAverageMessageDelayRatio()), title, timeLimitKTicks, 100, false);

				} else if ( e.getActionCommand().equals(StatisticWindow.averageUptodateRatioActionCmd) ) {

					title = "average uptodate-rate";
					new GraphViewWindow(new RPSStatistics.GnuplotProviderNotifier(stats.getAverageUptodateRatioUpdater(), stats
							.getGnuplotproviderAverageUptodateRatio()), title, timeLimitKTicks, 100, true);

				} else if ( e.getActionCommand().equals(StatisticWindow.relReOmRatioActionCmd) ) {

					title = "saved requests";
					new GraphViewWindow(new RPSStatistics.GnuplotProviderNotifier(stats.getRelReOmRatioUpdater(), stats.getGnuplotproviderRelReOmRatio()),
							title, timeLimitKTicks, 100, true);

				} else if ( e.getActionCommand().equals(StatisticWindow.meanDvtCoeffCPPActionCmd) ) {

					title = "mean coefficient-of-variance of cpp";
					new GraphViewWindow(new RPSStatistics.GnuplotProviderNotifier(stats.getCoeffOfVarCPPUpdater(), stats
							.getGnuplotproviderMeanDvtCoeffCPP()), title, timeLimitKTicks, 2, true);

				} else if ( e.getActionCommand().equals(StatisticWindow.meanDvtCPPActionCmd) ) {

					title = "mean standard-deviation of cpp";
					new GraphViewWindow(new RPSStatistics.GnuplotProviderNotifier(stats.getStdDevCPPUpdater(), stats.getGnuplotproviderMeanDvtCPP()),
							title, timeLimitKTicks, 100, false);

				} else if ( e.getActionCommand().equals(StatisticWindow.meanValueCPPActionCmd) ) {

					title = "mean-values of cpp";
					new GraphViewWindow(new RPSStatistics.GnuplotProviderNotifier(stats.getMeanValueCPPUpdater(), stats.getGnuplotproviderMeanValueCPP()),
							title, timeLimitKTicks, 100, false);

				}

			} catch ( Exception excpt ) {

				System.err.println("WARNING: Exception in StatisticWindow.actionPerformed():" + excpt);
				excpt.printStackTrace();

			}

		}

	}

	/**
	 * Checks for graph-button
	 * 
	 * @param name
	 * @return true, if graph-button
	 */
	protected boolean isGraphButton(String name) {

		for ( String actionCmd : graphButtonActions )
			if ( actionCmd.equals(name) )
				return true;

		return false;

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

	/**
	 * @return Returns the timeObserver.
	 */
	public synchronized TimeObserver getTimeObserver() {
		return timeObserver;
	}

	/**
	 * @return Returns the standardDeviationCPPObserver.
	 */
	public StandardDeviationCPPObserver getStandardDeviationCPPObserver() {
		return standardDeviationCPPObserver;
	}

	/**
	 * @return Returns the coefficientOfVarianceCPPObserver.
	 */
	public CoefficientOfVarianceCPPObserver getCoefficientOfVarianceCPPObserver() {
		return coefficientOfVarianceCPPObserver;
	}

	/**
	 * @return Returns the meanValueCPPObserver.
	 */
	public synchronized MeanValueCPPObserver getMeanValueCPPObserver() {
		return meanValueCPPObserver;
	}

}
