package de.zintel.diplom.simulation;
import java.io.*;
import java.util.*;

import de.zintel.diplom.feed.ColorEventFeedFactory;
import de.zintel.diplom.feed.ColorFeed;
import de.zintel.diplom.feed.ColorFeedFactory;
import de.zintel.diplom.feed.ColorFeedRepresentation;
import de.zintel.diplom.feed.RSSFeed;
import de.zintel.diplom.feed.RSSFeedFactory;
import de.zintel.diplom.feed.RSSFeedRepresentation;
import de.zintel.diplom.feed.RSSFeedRepresentationFactory;
import de.zintel.diplom.graphics.DisplayableObject;
import de.zintel.diplom.rps.RPSFactory;
import de.zintel.diplom.rps.broker.AdjustingBroker;
import de.zintel.diplom.rps.broker.AdjustingEventBroker;
import de.zintel.diplom.rps.broker.Broker;
import de.zintel.diplom.rps.broker.BrokerNode;
import de.zintel.diplom.rps.pubsub.CongestionControlEventPubSub;
import de.zintel.diplom.rps.pubsub.DfltRefreshRateEventPubSub;
import de.zintel.diplom.rps.pubsub.EventPubSub;
import de.zintel.diplom.rps.pubsub.ExponentialArttCongestionControlEventPubSub;
import de.zintel.diplom.rps.pubsub.LinearArttCongestionControlEventPubSub;
import de.zintel.diplom.rps.pubsub.LogarithmicArttCongestionControlEventPubSub;
import de.zintel.diplom.rps.pubsub.NoBalancingCongestionControlEventPubSub;
import de.zintel.diplom.rps.pubsub.NoChurnCompensationCongestionControlEventPubSub;
import de.zintel.diplom.rps.pubsub.NoSeqCongestionControlEventPubSub;
import de.zintel.diplom.rps.pubsub.NoSeqPolynomialCongestionControlEventPubSub;
import de.zintel.diplom.rps.pubsub.PolynomialCongestionControlEventPubSub;
import de.zintel.diplom.rps.pubsub.PubSub;
import de.zintel.diplom.rps.pubsub.PubSubNode;
import de.zintel.diplom.rps.pubsub.QuadraticArttCongestionControlEventPubSub;
import de.zintel.diplom.rps.pubsub.SinglePublisherEventPubSub;
import de.zintel.diplom.rps.pubsub.SoftAdaptingCongestionControlEventPubSub;
import de.zintel.diplom.rps.server.QueueingRSSServer;
import de.zintel.diplom.rps.server.RSSServer;
import de.zintel.diplom.rps.server.RSSServerNode;
import de.zintel.diplom.topology.BRITETopology;
import de.zintel.diplom.topology.Topology;
import de.zintel.diplom.topology.TopologyAllForOne;
import de.zintel.diplom.topology.TopologyOneSurrounded;

/*
 Copyright (C) 1999-2006 Friedemann Zintel
 
 This program is free software; you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation; either version 2 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

 For any questions, contact me at
 friezi@cs.tu-berlin.de
*/

/**
 * This is a general Simulation. On running main you have to provide it with to
 * filenames as arguments. The first file defines all the Topology-, Broker-,
 * etc-Classes as a key/value-pair. The values are the classnames of the
 * appropriate class. The second file defines all the parameters.
 *
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * @author Friedemann Zintel
 * 
 */
public class Simulation {

	protected static final String TOPOLOGY_CLASSKEY = "TopologyClass";

	protected static final String TOPOLOGYALLFORONE = "TopologyAllForOne";

	protected static final String TOPOLOGYONESURROUNDED = "TopologyOneSurrounded";

	protected static final String BRITETOPOLOGY = "BRITETopology";

	protected static final String BRITESUBLAYERFILE = "BRITESublayerFile";

	protected static final String BRITEBROKERNETFILE = "BRITEBrokernetFile";

	protected static final String BROKER_CLASSKEY = "BrokerClass";

	protected static final String BROKER = "Broker";

	protected static final String ADJUSTINGBROKER = "AdjustingBroker";

	protected static final String ADJUSTINGEVENTBROKER = "AdjustingEventBroker";

	protected static final String RSSSERVER_CLASSKEY = "RSSServerClass";

	protected static final String RSSSERVER = "RSSServer";

	protected static final String QUEUEINGRSSSERVER = "QueueingRSSServer";

	protected static final String PUBSUB_CLASSKEY = "PubSubClass";

	protected static final String PUBSUB = "PubSub";

	protected static final String EVENTPUBSUB = "EventPubSub";

	protected static final String DFLTREFRESHRATEEVENTPUBSUB = "DfltRefreshRateEventPubSub";

	protected static final String CONGESTIONCONTROLEVENTPUBSUB = "CongestionControlEventPubSub";

	protected static final String NOBALANCINGCONGESTIONCONTROLEVENTPUBSUB = "NoBalancingCongestionControlEventPubSub";

	protected static final String SOFTADAPTINGCONGESTIONCONTROLEVENTPUBSUB = "SoftAdaptingCongestionControlEventPubSub";

	protected static final String POLYNOMIALCONGESTIONCONTROLEVENTPUBSUB = "PolynomialCongestionControlEventPubSub";

	protected static final String NOCHURNCOMPENSATIONCONGESTIONCONTROLEVENTPUBSUB = "NoChurnCompensationCongestionControlEventPubSub";

	protected static final String LINEARARTTCONGESTIONCONTROLEVENTPUBSUB = "LinearArttCongestionControlEventPubSub";

	protected static final String QUADRATICARTTCONGESTIONCONTROLEVENTPUBSUB = "QuadraticArttCongestionControlEventPubSub";

	protected static final String EXPONENTIALARTTCONGESTIONCONTROLEVENTPUBSUB = "ExponentialArttCongestionControlEventPubSub";

	protected static final String LOGARITHMICARTTCONGESTIONCONTROLEVENTPUBSUB = "LogarithmicArttCongestionControlEventPubSub";

	protected static final String NOSEQCONGESTIONCONTROLEVENTPUBSUB = "NoSeqCongestionControlEventPubSub";

	protected static final String NOSEQPOLYNOMIALCONGESTIONCONTROLEVENTPUBSUB = "NoSeqPolynomialCongestionControlEventPubSub";

	protected static final String SINGLEPUBLISHEREVENTPUBSUB = "SinglePublisherEventPubSub";

	protected static final String RSSFEED_CLASSKEY = "RSSFeedClass";

	protected static final String COLORFEED = "ColorFeed";

	protected static final String COLOREVENTFEED = "ColorEventFeed";

	protected String topologyclass;

	protected String britesublayerfile;

	protected String britebrokernetfile;

	protected String brokerclass;

	protected String rssserverclass;

	protected String rssfeedclass;

	protected String pubsubclass;

	protected SimParameters params;

	public Simulation(SimParameters params) {
		this.params = params;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {

		String parameterfile = "";

		try {

			// check commandline
			if ( args.length > 1 ) {

				System.out.println("Invalid calling syntax!");
				System.out.println("usage: java Simulation [<parameterfile>]");
				System.exit(1);

			} else if ( args.length == 1 ) {
				parameterfile = args[0];
			}

			new Simulation(new SimParameters(parameterfile)).start();

		} catch ( SimParameters.ValueOutOfRangeException e ) {

			System.err.println(e);
			System.exit(1);

		} catch ( Throwable e ) {
			System.err.println(e);

			for ( StackTraceElement element : e.getStackTrace() )
				System.err.println(element);

			System.exit(1);
		}

	}
	
	public void start() throws Exception{

		// if a seed is defined use it
		if ( params.getSeedValue().equals("none") == false )
			Engine.getSingleton().getRandom().setSeed(Long.valueOf(params.getSeedValue()));

		// set Topology-configuration
		setScenarioConfiguration(params.getScenarioFile());

		Engine.getSingleton().setTimerPeriod(params.getEngineTimerPeriod());

		Engine.getSingleton().init(params);

		Topology topology = newTopology();

		if ( Engine.getSingleton().getGui() != null )
			Engine.getSingleton().repaintGUI();

		Engine.getSingleton().startInteractiveMode();
		
	}

	protected Topology newTopology() throws Exception {

		String methodname = "Simulation.Topology()";

		if ( topologyclass.equals(TOPOLOGYALLFORONE) ) {

			return new TopologyAllForOne(newRPSFactory(), newRSSFeedFactory(), newRSSFeedRepresentationFactory(), params);

		} else if ( topologyclass.equals(TOPOLOGYONESURROUNDED) ) {

			return new TopologyOneSurrounded(newRPSFactory(), newRSSFeedFactory(), newRSSFeedRepresentationFactory(), params);

		} else if ( topologyclass.equals(BRITETOPOLOGY) ) {

			return new BRITETopology(britesublayerfile, britebrokernetfile, newRPSFactory(), newRSSFeedFactory(), newRSSFeedRepresentationFactory(),
					params);

		}

		System.err.println(methodname + ": Warning: " + TOPOLOGY_CLASSKEY + " " + topologyclass + " not supported");
		System.err.println(methodname + ": Warning: using default " + TOPOLOGY_CLASSKEY + " " + TOPOLOGYALLFORONE);
		return new TopologyAllForOne(newRPSFactory(), newRSSFeedFactory(), newRSSFeedRepresentationFactory(), params);

	}

	protected RPSFactory newRPSFactory() {

		return new RPSFactory() {
			public BrokerNode newBrokerNode(int xp, int yp, SimParameters params) throws Exception {
				return Simulation.this.newBrokerNode(xp, yp, params);
			}

			public PubSubNode newPubSubNode(int xp, int yp, SimParameters params) throws Exception {
				return Simulation.this.newPubSubNode(xp, yp, params);
			}

			public RSSServerNode newRSSServerNode(int xp, int yp, SimParameters params) throws Exception {
				return Simulation.this.newRSSServerNode(xp, yp, params);
			}

		};

	}

	protected BrokerNode newBrokerNode(int xp, int yp, SimParameters params) throws Exception {

		String methodname = "Simulation.newBrokerNode()";

		if ( brokerclass.equals(BROKER) ) {

			return new Broker(xp, yp, params);

		} else if ( brokerclass.equals(ADJUSTINGBROKER) ) {

			return new AdjustingBroker(xp, yp, params);

		} else if ( brokerclass.equals(ADJUSTINGEVENTBROKER) ) {

			return new AdjustingEventBroker(xp, yp, new ColorEventFeedFactory(params), params);

		}

		System.err.println(methodname + ": Warning: " + BROKER_CLASSKEY + " " + brokerclass + " not supported");
		System.err.println(methodname + ": Warning: using default " + BROKER_CLASSKEY + " " + BROKER);
		return new Broker(xp, yp, params);

	}

	protected PubSubNode newPubSubNode(int xp, int yp, SimParameters params) throws Exception {

		String methodname = "Simulation.newPubSubNode()";

		if ( pubsubclass.equals(PUBSUB) ) {

			return new PubSub(xp, yp, params);

		} else if ( pubsubclass.equals(EVENTPUBSUB) ) {

			return new EventPubSub(xp, yp, new ColorEventFeedFactory(params), params);

		} else if ( pubsubclass.equals(DFLTREFRESHRATEEVENTPUBSUB) ) {

			return new DfltRefreshRateEventPubSub(xp, yp, new ColorEventFeedFactory(params), params);

		} else if ( pubsubclass.equals(CONGESTIONCONTROLEVENTPUBSUB) ) {

			return new CongestionControlEventPubSub(xp, yp, new ColorEventFeedFactory(params), params);

		} else if ( pubsubclass.equals(NOBALANCINGCONGESTIONCONTROLEVENTPUBSUB) ) {

			return new NoBalancingCongestionControlEventPubSub(xp, yp, new ColorEventFeedFactory(params), params);

		} else if ( pubsubclass.equals(SOFTADAPTINGCONGESTIONCONTROLEVENTPUBSUB) ) {

			return new SoftAdaptingCongestionControlEventPubSub(xp, yp, new ColorEventFeedFactory(params), params);

		} else if ( pubsubclass.equals(POLYNOMIALCONGESTIONCONTROLEVENTPUBSUB) ) {

			return new PolynomialCongestionControlEventPubSub(xp, yp, new ColorEventFeedFactory(params), params);

		} else if ( pubsubclass.equals(NOCHURNCOMPENSATIONCONGESTIONCONTROLEVENTPUBSUB) ) {

			return new NoChurnCompensationCongestionControlEventPubSub(xp, yp, new ColorEventFeedFactory(params), params);

		} else if ( pubsubclass.equals(LINEARARTTCONGESTIONCONTROLEVENTPUBSUB) ) {

			return new LinearArttCongestionControlEventPubSub(xp, yp, new ColorEventFeedFactory(params), params);

		} else if ( pubsubclass.equals(QUADRATICARTTCONGESTIONCONTROLEVENTPUBSUB) ) {

			return new QuadraticArttCongestionControlEventPubSub(xp, yp, new ColorEventFeedFactory(params), params);

		} else if ( pubsubclass.equals(EXPONENTIALARTTCONGESTIONCONTROLEVENTPUBSUB) ) {

			return new ExponentialArttCongestionControlEventPubSub(xp, yp, new ColorEventFeedFactory(params), params);

		} else if ( pubsubclass.equals(LOGARITHMICARTTCONGESTIONCONTROLEVENTPUBSUB) ) {

			return new LogarithmicArttCongestionControlEventPubSub(xp, yp, new ColorEventFeedFactory(params), params);

		} else if ( pubsubclass.equals(NOSEQCONGESTIONCONTROLEVENTPUBSUB) ) {

			return new NoSeqCongestionControlEventPubSub(xp, yp, new ColorEventFeedFactory(params), params);

		} else if ( pubsubclass.equals(NOSEQPOLYNOMIALCONGESTIONCONTROLEVENTPUBSUB) ) {

			return new NoSeqPolynomialCongestionControlEventPubSub(xp, yp, new ColorEventFeedFactory(params), params);

		} else if ( pubsubclass.equals(SINGLEPUBLISHEREVENTPUBSUB) ) {

			return new SinglePublisherEventPubSub(xp, yp, new ColorEventFeedFactory(params), params);

		}

		System.err.println(methodname + ": Warning: " + PUBSUB_CLASSKEY + " " + pubsubclass + " not supported");
		System.err.println(methodname + ": Warning: using default " + PUBSUB_CLASSKEY + " " + PUBSUB);
		return new PubSub(xp, yp, params);

	}

	protected RSSServerNode newRSSServerNode(int xp, int yp, SimParameters params) throws Exception {

		String methodname = "Simulation.newRSSServerNode()";

		if ( rssserverclass.equals(RSSSERVER) ) {

			return new RSSServer(xp, yp, params);

		} else if ( rssserverclass.equals(QUEUEINGRSSSERVER) ) {

			return new QueueingRSSServer(xp, yp, params);

		}

		System.err.println(methodname + ": Warning: " + RSSSERVER_CLASSKEY + " " + rssserverclass + " not supported");
		System.err.println(methodname + ": Warning: using default " + RSSSERVER_CLASSKEY + " " + RSSSERVER);
		return new RSSServer(xp, yp, params);

	}

	protected RSSFeedFactory newRSSFeedFactory() {

		String methodname = "Simulation.newRSSFeedFactoryNode()";

		if ( rssfeedclass.equals(COLORFEED) ) {

			return new ColorFeedFactory();

		} else if ( rssfeedclass.equals(COLOREVENTFEED) ) {

			return new ColorEventFeedFactory(params);
		}

		System.err.println(methodname + ": Warning: " + RSSFEED_CLASSKEY + " " + rssfeedclass + " not supported");
		System.err.println(methodname + ": Warning: using default " + RSSFEED_CLASSKEY + " " + COLORFEED);
		return new ColorFeedFactory();
	}

	protected RSSFeedRepresentationFactory newRSSFeedRepresentationFactory() {
		return new RSSFeedRepresentationFactory() {
			public RSSFeedRepresentation newRSSFeedRepresentation(DisplayableObject dObj, RSSFeed feed) {
				return new ColorFeedRepresentation(dObj, (ColorFeed) feed);
			}

			public RSSFeedRepresentation newRSSFeedRepresentation(RSSFeed feed) {
				return newRSSFeedRepresentation(null, (ColorFeed) feed);
			}
		};
	}

	protected void setScenarioConfiguration(String filename) throws Exception {

		boolean error = false;

		Properties scenarioConfiguration = new Properties();

		if ( filename.equals(SimParameters.NONE) == true ) {

			System.out.println("Warning: No scenarioFile defined in parameter-file, using default scenario!");
			setDefaultScenario(scenarioConfiguration);

		} else {

			scenarioConfiguration.load(new FileInputStream(filename));

		}

		if ( (topologyclass = scenarioConfiguration.getProperty(TOPOLOGY_CLASSKEY)) == null ) {

			System.err.println("Please define the key/value pair for key " + TOPOLOGY_CLASSKEY + " in file " + filename);
			error = true;

		} else if ( topologyclass.equals(BRITETOPOLOGY) ) {

			if ( (britesublayerfile = scenarioConfiguration.getProperty(BRITESUBLAYERFILE)) == null ) {
				System.out.println("Please define the key/value pair for key " + BRITESUBLAYERFILE + " in file " + filename);
				error = true;
			}

			if ( (britebrokernetfile = scenarioConfiguration.getProperty(BRITEBROKERNETFILE)) == null ) {
				System.out.println("Please define the key/value pair for key " + BRITEBROKERNETFILE + " in file " + filename);
				error = true;
			}

		}

		if ( (brokerclass = scenarioConfiguration.getProperty(BROKER_CLASSKEY)) == null ) {
			System.err.println("Please define the key/value pair for key " + BROKER_CLASSKEY + " in file " + filename);
			error = true;
		}

		if ( (pubsubclass = scenarioConfiguration.getProperty(PUBSUB_CLASSKEY)) == null ) {
			System.err.println("Please define the key/value pair for key " + PUBSUB_CLASSKEY + " in file " + filename);
			error = true;
		}

		if ( (rssserverclass = scenarioConfiguration.getProperty(RSSSERVER_CLASSKEY)) == null ) {
			System.err.println("Please define the key/value pair for key " + RSSSERVER_CLASSKEY + " in file " + filename);
			error = true;
		}

		if ( (rssfeedclass = scenarioConfiguration.getProperty(RSSFEED_CLASSKEY)) == null ) {
			System.err.println("Please define the key/value pair for key " + RSSFEED_CLASSKEY + " in file " + filename);
			error = true;
		}

		if ( error == true )
			System.exit(1);

	}

	protected void setDefaultScenario(Properties scenarioConfiguration) {

		scenarioConfiguration.setProperty(TOPOLOGY_CLASSKEY, TOPOLOGYONESURROUNDED);
		scenarioConfiguration.setProperty(BROKER_CLASSKEY, ADJUSTINGEVENTBROKER);
		scenarioConfiguration.setProperty(PUBSUB_CLASSKEY, CONGESTIONCONTROLEVENTPUBSUB);
		scenarioConfiguration.setProperty(RSSSERVER_CLASSKEY, QUEUEINGRSSSERVER);
		scenarioConfiguration.setProperty(RSSFEED_CLASSKEY, COLOREVENTFEED);

	}

}
