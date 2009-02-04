package de.zintel.diplom.topology;
/**
 /**
 * 
 */

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import de.zintel.diplom.feed.RSSFeedFactory;
import de.zintel.diplom.feed.RSSFeedRepresentationFactory;
import de.zintel.diplom.graph.Graph;
import de.zintel.diplom.gui.InfoOutput;
import de.zintel.diplom.rps.RPSFactory;
import de.zintel.diplom.rps.broker.BrokerNode;
import de.zintel.diplom.rps.pubsub.PubSubNode;
import de.zintel.diplom.rps.server.RSSServerNode;
import de.zintel.diplom.simulation.EdgeFactory;
import de.zintel.diplom.simulation.Engine;
import de.zintel.diplom.simulation.Node;
import de.zintel.diplom.simulation.SimParameters;
import de.zintel.diplom.simulation.TransferNode;
import de.zintel.diplom.tools.IntegrityFileInputStream;
import de.zintel.diplom.tools.IntegrityFileOutputStream;
import de.zintel.diplom.util.ProgressBarAccessor;

/**
 * @author Friedemann Zintel
 * 
 */
public class BRITETopology extends Topology {

	protected class FileReaderBuffer {

		public String filename;

		private int position = 0;

		private char buffer[];

		public FileReaderBuffer(String filename) throws IOException {

			this.filename = filename;

			FileInputStream fileinfo = new FileInputStream(filename);

			buffer = new char[(int) fileinfo.getChannel().size()];

			fileinfo.close();

			FileReader file = new FileReader(filename);

			file.read(buffer);

			file.close();

		}

		public int read() {

			if ( position < buffer.length )
				return buffer[position++];
			else
				return -1;

		}

		/**
		 * @return Returns the position.
		 */
		public int getPosition() {
			return position;
		}

		/**
		 * @param position
		 *            The position to set.
		 */
		public void setPosition(int position) {
			this.position = position;
		}

	}

	protected class NodeProperties {
		public int id;

		public int xpos;

		public int ypos;

		public NodeProperties(int id, int xpos, int ypos) {
			this.id = id;
			this.xpos = xpos;
			this.ypos = ypos;
		}

	}

	private static final char delimitors[] = { '(', ')', '{', '}', '<', '>', '-', '+', '*', ':', ',', ' ', '\t', '\r', '\n', '\0' };

	private static final char notokens[] = { ' ', '\t', '\n', '\r', '\0' };

	private static final char blanks[] = { ' ', '\t' };

	private static final char lineends[] = { '\r', '\n' };

	private static final byte version[] = { 0, 0, 0, 1 };

	private static int SHA1_BYTES = 20;

	private final int RSS_XPOS = 400;

	private final int RSS_YPOS = 400;

	private RPSFactory rpsFactory;

	private RSSFeedRepresentationFactory rssFeedRepresentationFactory;

	private RSSFeedFactory rssFeedFactory;

	private SimParameters params;

	private RSSServerNode rssServer;

	private double maxBandwith;

	private String model = "";

	private int brokernodes;

	private String sublayerfilename;

	private String brokernetfilename;

	// Suffix for routind-data file
	private String rd_suffix = ".rd";

	// pattern for brite-files
	private Pattern brite_pattern = Pattern.compile("(.*).brite");

	private byte[][] toplayerBrokerAdjacencytable = null;

	private byte[][] toplayerTreeAdjacencyTable = null;

	InfoOutput infoOutput = Engine.getSingleton().getInfoOutput();

	private LinkedList<String> supportedModels = new LinkedList<String>();

	private LinkedList<PubSubNode> pubsublist = new LinkedList<PubSubNode>();

	private LinkedList<BrokerNode> brokerlist = new LinkedList<BrokerNode>();

	private LinkedList<NodeProperties> nodeproperties = new LinkedList<NodeProperties>();

	public BRITETopology(String sublayerfilename, String brokernetfilename, RPSFactory rpsFactory, RSSFeedFactory rssFeedFactory,
			RSSFeedRepresentationFactory rssFeedRepresentationFactory, SimParameters params) throws Exception {

		super(rpsFactory, rssFeedFactory, rssFeedRepresentationFactory, params);

		supportedModels.add("ASWaxman");
		supportedModels.add("RTWaxman");

		this.sublayerfilename = sublayerfilename;
		this.brokernetfilename = brokernetfilename;
		this.rpsFactory = rpsFactory;
		this.rssFeedFactory = rssFeedFactory;
		this.rssFeedRepresentationFactory = rssFeedRepresentationFactory;
		this.params = params;

		int[][] adjacencytable = null;
		int[][] nextnodetable = null;

		try {

			FileReaderBuffer sublayerfile = new FileReaderBuffer(sublayerfilename);
			FileReaderBuffer toplayerfile = new FileReaderBuffer(brokernetfilename);

			infoOutput.write("Parsing sublayer-file " + sublayerfilename + " ... ");

			if ( (adjacencytable = parseBRITESublayerFile(sublayerfile)) == null ) {
				System.out.println("Error in file " + sublayerfilename);
				System.exit(1);
			}

			infoOutput.write("done\n");
			infoOutput.write("Parsing brokernet-file " + brokernetfilename + " ... ");

			if ( parseBRITEBrokernetFile(toplayerfile) == false ) {
				System.out.println("Error in file " + brokernetfilename);
				System.exit(1);
			}

			infoOutput.write("done\n");

			if ( (nextnodetable = readRoutingDataFromFile()) == null ) {

				infoOutput.write("Calculating shortest paths ... ");

				nextnodetable = new int[Engine.getNumberOfNodes()][Engine.getNumberOfNodes()];

				// calculate transitive closure
				Graph.shortestPaths(Engine.getNumberOfNodes(), adjacencytable, Engine.getCosttable(), nextnodetable);

				infoOutput.write("done\n");

				writeRoutingDataToFile(nextnodetable);

			}

			infoOutput.write("Building sublayer:\n");

			buildSublayer(adjacencytable);

			// save memory
			adjacencytable = null;

			infoOutput.write("Generating routing-table ... ");

			// must be generated after building the sublayer, otherwise no nodes
			// exist for the routing-table
			Engine.getSingleton().generateRoutingTable(nextnodetable);

			infoOutput.write("done\n");

			if ( params.isGui() == true ) {

				infoOutput.write("Generating hop-table ... ");

				Engine.getSingleton().generateHopTable(nextnodetable);

				infoOutput.write("done\n");

			}

			// save memory
			nextnodetable = null;

			infoOutput.write("Building virtual toplayer:\n");

			buildToplayer();

			setStatisticObservers(rssServer, pubsublist);

		} catch (FileNotFoundException e) {

			System.out.println(e);
			System.exit(1);

		}

	}

	/**
	 * Writes the routingdatas from a next nodetable to a file
	 * 
	 * @param nextnodetable
	 *            the filled nextnodetable
	 * @throws Exception
	 */
	protected void writeRoutingDataToFile(int[][] nextnodetable) throws Exception {

		ProgressBarAccessor progressbar = new ProgressBarAccessor();

		progressbar.setMaximum(Engine.getNumberOfNodes());
		progressbar.setMinimum(0);
		progressbar.setValue(0);
		progressbar.setStringPainted(true);

		String routingdatafilename = getRoutingDataFileName(sublayerfilename);

		DataOutputStream routingdatafile = new DataOutputStream(new GZIPOutputStream(new IntegrityFileOutputStream(routingdatafilename)));

		FileInputStream topologyfile = new FileInputStream(sublayerfilename);

		byte[] t_filecontent = new byte[(int) topologyfile.getChannel().size()];
		topologyfile.read(t_filecontent);

		// calculate the hashvalue of the sublayerfile to be able to ensure that
		// routingdatafile
		// contains the correct datas
		MessageDigest sha = MessageDigest.getInstance("SHA-1");

		sha.update(t_filecontent);
		byte[] signature = sha.digest();

		topologyfile.close();

		infoOutput.write("Writing routing-data to file " + routingdatafilename + " ... ");

		// write version
		routingdatafile.write(version);

		// write signature of topologyfile
		routingdatafile.write(signature);

		for (int i = 0; i < Engine.getNumberOfNodes(); i++) {

			progressbar.setValue(i);

			for (int j = 0; j < Engine.getNumberOfNodes(); j++)
				routingdatafile.writeInt(nextnodetable[i][j]);

		}

		progressbar.setValue(0);
		progressbar.setStringPainted(false);

		routingdatafile.close();

		infoOutput.write("done\n");

	}

	/**
	 * @return a filled nextnodetable or null on error
	 * @throws Exception
	 */
	protected int[][] readRoutingDataFromFile() throws Exception {

		ProgressBarAccessor progressbar = new ProgressBarAccessor();

		progressbar.setMaximum(Engine.getNumberOfNodes());
		progressbar.setMinimum(0);
		progressbar.setValue(0);
		progressbar.setStringPainted(true);

		String routingdatafilename = getRoutingDataFileName(sublayerfilename);

		// try to find a data-file
		try {

			DataInputStream routingdatafile = new DataInputStream(new GZIPInputStream(new IntegrityFileInputStream(routingdatafilename)));

			infoOutput.write("Integrity-check for file " + routingdatafilename + " successfull\n");

			infoOutput.write("Reading routing-data from file: " + routingdatafilename + "\n");

			FileInputStream topologyfile;
			try {
				topologyfile = new FileInputStream(sublayerfilename);
			} catch (Exception e) {
				infoOutput.write("IOError: file " + sublayerfilename);
				routingdatafile.close();
				return null;
			}

			byte[] t_filecontent = new byte[(int) topologyfile.getChannel().size()];
			topologyfile.read(t_filecontent);

			MessageDigest sha = MessageDigest.getInstance("SHA-1");

			sha.update(t_filecontent);
			byte[] signature = sha.digest();

			topologyfile.close();

			byte[] file_version = new byte[4];

			// read version and compare
			if ( routingdatafile.read(file_version) < 4 ) {
				infoOutput.write("File " + routingdatafilename + " corrupt!\n");
				routingdatafile.close();
				return null;
			}

			if ( new String(file_version).equals(new String(version)) == false ) {
				infoOutput.write("Wrong version of routing-data file!\n");
				routingdatafile.close();
				return null;
			}

			byte[] saved_signature = new byte[SHA1_BYTES];

			if ( routingdatafile.read(saved_signature) < SHA1_BYTES ) {
				infoOutput.write("File " + routingdatafilename + " corrupt!\n");
				routingdatafile.close();
				return null;
			}

			// compare the signatures
			if ( new String(saved_signature).equals(new String(signature)) == false ) {
				infoOutput.write("Routingdata-file doesn't match with file " + sublayerfilename + "!\n");
				routingdatafile.close();
				return null;
			}

			int[][] nextnodetable = new int[Engine.getNumberOfNodes()][Engine.getNumberOfNodes()];

			for (int i = 0; i < Engine.getNumberOfNodes(); i++) {

				progressbar.setValue(i);

				for (int j = 0; j < Engine.getNumberOfNodes(); j++) {
					try {
						nextnodetable[i][j] = routingdatafile.readInt();
					} catch (EOFException e) {
						infoOutput.write("File " + routingdatafilename + " corrupt!\n");
						routingdatafile.close();
						return null;
					}
				}
			}

			progressbar.setValue(0);
			progressbar.setStringPainted(false);

			routingdatafile.close();

			return nextnodetable;

		} catch (FileNotFoundException e) {
			infoOutput.write("No routingdata-file found\n");
		} catch (IOException e) {
			infoOutput.write("IOError in file\n");

			infoOutput.write(e.getMessage() + "\n");

			for (StackTraceElement element : e.getStackTrace())
				System.err.println(element);

		} catch (IntegrityFileInputStream.IntegrityException e) {
			infoOutput.write("integrity-check failed for routingdata-file " + getRoutingDataFileName(sublayerfilename) + "!\n");
		}

		return null;

	}

	protected String getRoutingDataFileName(String filename) {

		Matcher matcher = brite_pattern.matcher(filename);
		if ( matcher.matches() == false )
			return filename + rd_suffix;
		else
			return matcher.group(1) + rd_suffix;

	}

	protected void buildSublayer(int[][] adjacencytable) throws Exception {

		infoOutput.write("-> Making nodes ... ");

		makeNodes();

		// hook off from edge-notification
		for (BrokerNode broker : brokerlist) {
			broker.deleteGuiObservers();

		}

		for (PubSubNode pubsub : pubsublist) {
			pubsub.deleteGuiObservers();

		}

		infoOutput.write("done\n");

		infoOutput.write("-> Making edges ... ");

		makeEdges(adjacencytable);

		infoOutput.write("done\n");

	}

	protected void buildToplayer() {

		infoOutput.write("-> Registering subscribers at rss-server ... ");

		registerAtRSSServer();

		infoOutput.write("done\n");

		toplayerTreeAdjacencyTable = new byte[brokernodes][brokernodes];

		infoOutput.write("-> Building spanning tree for brokers ... ");

		Graph.spanningTree(brokernodes, toplayerBrokerAdjacencytable, Engine.getCosttable(), toplayerTreeAdjacencyTable);

		infoOutput.write("done\n");

		infoOutput.write("-> Registering brokers ... ");

		registerBrokers();

		infoOutput.write("done\n");

		infoOutput.write("-> Registering subscribers at brokers ... ");

		registerSubscribersAtBrokers();

		infoOutput.write("done\n");

	}

	protected void registerAtRSSServer() {

		for (PubSubNode pubsub : pubsublist)
			pubsub.setRSSServer(rssServer);

	}

	protected void registerBrokers() {

		for (BrokerNode broker1 : brokerlist)
			for (BrokerNode broker2 : brokerlist)
				if ( toplayerTreeAdjacencyTable[broker1.getId()][broker2.getId()] == 1 )
					broker1.callbackRegisterAtBroker(broker2);

	}

	protected void registerSubscribersAtBrokers() {

		Iterator<BrokerNode> brokerit = brokerlist.iterator();
		BrokerNode broker = null;

		if ( brokerit.hasNext() == false )
			return;

		for (PubSubNode pubsub : pubsublist) {

			if ( brokerit.hasNext() )
				broker = brokerit.next();
			else {
				brokerit = brokerlist.iterator();
				broker = brokerit.next();
			}

			pubsub.callbackRegisterAtBroker(broker);

		}

	}

	/**
	 * Checks if a BRITE-model is suppported
	 * 
	 * @param name
	 * @return true, if model is supported, false otherwise
	 */
	protected boolean isSupportedModel(String name) {

		for (String model : supportedModels)
			if ( model.equals(name) )
				return true;

		return false;

	}

	protected boolean isDelimitor(char character) {

		for (int i = 0; i < delimitors.length; i++)
			if ( delimitors[i] == character )
				return true;

		return false;
	}

	protected boolean isNoToken(char character) {

		for (int i = 0; i < notokens.length; i++)
			if ( notokens[i] == character )
				return true;

		return false;
	}

	protected boolean isBlank(char character) {

		for (int i = 0; i < blanks.length; i++)
			if ( blanks[i] == character )
				return true;

		return false;
	}

	protected boolean isLineEnd(char character) {

		for (int i = 0; i < lineends.length; i++)
			if ( lineends[i] == character )
				return true;

		return false;
	}

	/**
	 * Skips all characters upto next line
	 * 
	 * @param file
	 *            then open file
	 * @return false, if EOF, else true
	 * @throws IOException
	 */
	protected boolean skipRestOfLine(FileReaderBuffer file) throws IOException {

		int character = file.read();

		while (character != -1) {
			if ( (char) character == '\n' )
				return true;
			character = file.read();
		}

		return false;

	}

	protected boolean skipEmptyLines(FileReaderBuffer file) throws IOException {

		int position;

		if ( skipBlanks(file) == false )
			return false;

		position = file.getPosition();

		int character = file.read();

		while (isNoToken((char) character) == true) {

			file.setPosition(position);

			if ( skipRestOfLine(file) == false )
				return false;

			if ( skipBlanks(file) == false )
				return false;

			position = file.getPosition();

			character = file.read();

		}

		file.setPosition(position);

		return true;

	}

	protected boolean skipBlanks(FileReaderBuffer file) throws IOException {

		int position;

		position = file.getPosition();

		int character = file.read();

		while (character != -1) {
			if ( isBlank((char) character) == false ) {
				file.setPosition(position);
				return true;
			}

			position = file.getPosition();
			character = file.read();
		}

		return false;

	}

	/**
	 * Skips all lines upto line behind "\<Token\>: ..."
	 * 
	 * @param file
	 *            the open file
	 * @return false, if EOF, else true
	 * @throws IOException
	 */
	protected boolean skipToLineWithToken(FileReaderBuffer file, String token) throws IOException {

		if ( skipEmptyLines(file) == false )
			return false;

		String currenttoken = getTokenAhead(file);

		while (currenttoken.equals(token) == false) {

			if ( skipRestOfLine(file) == false )
				return false;

			if ( skipEmptyLines(file) == false )
				return false;

			currenttoken = getTokenAhead(file);

		}

		return true;

	}

	protected String getTokenAhead(FileReaderBuffer file) throws IOException {

		int position;

		String token;

		if ( skipBlanks(file) == false )
			return "";

		position = file.getPosition();

		token = nextLineToken(file);

		file.setPosition(position);

		return token;

	}

	protected void skipNextToken(FileReaderBuffer file) throws IOException {
		nextLineToken(file);
	}

	protected String nextLineToken(FileReaderBuffer file) throws IOException {

		int position;

		String token = new String("");

		if ( skipBlanks(file) == false )
			return token;

		position = file.getPosition();

		int character = file.read();

		if ( character == -1 )
			return token;

		if ( isNoToken((char) character) == false )
			token = token.concat(String.valueOf((char) character));
		else {

			file.setPosition(position);
			return token;

		}

		if ( isDelimitor((char) character) )
			return token;

		position = file.getPosition();
		character = file.read();

		while (character != -1) {

			if ( isDelimitor((char) character) ) {

				file.setPosition(position);
				break;

			}

			token = token.concat(String.valueOf((char) character));

			position = file.getPosition();
			character = file.read();

		}

		return token;

	}

	/**
	 * @param file
	 * @return a filled adjacencytable or null on error
	 * @throws Exception
	 */
	protected int[][] parseBRITESublayerFile(FileReaderBuffer file) throws Exception {
		// parse nodes

		int nodeid, xpos, ypos, firstnodeid = -1;

		Node node;
		int nodes, nodecount = 0;
		String token = "";
		int[][] adjacencytable = null;

		if ( skipToLineWithToken(file, "Model") == false )
			return null;

		nextLineToken(file);
		nextLineToken(file);
		nextLineToken(file);
		nextLineToken(file);

		model = nextLineToken(file);

		if ( isSupportedModel(model) == false ) {

			String text = new String("In file " + file.filename + ": model " + model + " not yet supported!");
			System.out.println(text);
			infoOutput.write(text + "\n");
			System.exit(1);

		}

		while ((token = nextLineToken(file)).equals(":") == false)
			;

		// the 11th token after ":" is the bandwith
		for (int i = 0; i < 11; i++)
			token = nextLineToken(file);

		// due to a possible bug in BRITE, this doesn't need to be the real max
		// value; we have
		// to check that later
		maxBandwith = Double.parseDouble(token);

		if ( skipToLineWithToken(file, "Nodes") == false )
			return null;

		if ( skipEmptyLines(file) == false )
			return null;

		// skip: "Nodes" ":" "("
		nextLineToken(file);
		nextLineToken(file);
		nextLineToken(file);

		token = nextLineToken(file);

		nodes = Integer.parseInt(token);

		Engine.setNumberOfNodes(nodes);

		// skip rest
		if ( skipRestOfLine(file) == false )
			return null;

		token = getTokenAhead(file);

		while (true) {

			try {
				// force break if first token is not a number
				Integer.parseInt(token);
			} catch (NumberFormatException e) {

				if ( nodecount < nodes ) {

					String text = new String("Error: less nodes than expected!");
					infoOutput.write(text + "\n");
					System.err.println(text);
					return null;

				} else
					break;
			}

			while (token.equals("") == false) {

				token = nextLineToken(file);

				nodeid = Integer.parseInt(token);

				nodecount++;

				// in BRITE-files, the first node doesn't need to start with
				// id==0
				if ( firstnodeid == -1 )
					firstnodeid = nodeid;

				token = nextLineToken(file);

				xpos = Integer.parseInt(token);

				token = nextLineToken(file);

				ypos = Integer.parseInt(token);

				nodeproperties.add(new NodeProperties(nodeid - firstnodeid, xpos, ypos));

				skipRestOfLine(file);
				break;
			}

			if ( skipEmptyLines(file) == false )
				return null;

			token = getTokenAhead(file);

		}

		// parse edges

		int edgeid, nodeid1, nodeid2;
		double length, delay, bandwidth;

		if ( skipToLineWithToken(file, "Edges") == false )
			return null;

		if ( skipEmptyLines(file) == false )
			return null;

		// skip line "Edges : ..."
		if ( skipRestOfLine(file) == false )
			return null;

		token = getTokenAhead(file);

		adjacencytable = new int[Engine.getNumberOfNodes()][Engine.getNumberOfNodes()];
		Engine.setCosttable(new double[Engine.getNumberOfNodes()][Engine.getNumberOfNodes()]);

		// initialize costtable
		for (int x = 0; x < Engine.getNumberOfNodes(); x++)
			for (int y = 0; y < Engine.getNumberOfNodes(); y++)
				// Engine.getCosttable()[x][y] = maxBandwith;
				Engine.getCosttable()[x][y] = Double.MAX_VALUE / 2;

		while (true) {

			try {
				// force break if first token is not a number
				Integer.parseInt(token);

			} catch (NumberFormatException e) {
				break;
			}

			while (token.equals("") == false) {

				token = nextLineToken(file);

				edgeid = Integer.parseInt(token);

				token = nextLineToken(file);

				nodeid1 = Integer.parseInt(token);

				token = nextLineToken(file);

				nodeid2 = Integer.parseInt(token);

				token = nextLineToken(file);

				length = Double.parseDouble(token);

				token = nextLineToken(file);

				delay = Double.parseDouble(token);

				token = nextLineToken(file);

				bandwidth = Double.parseDouble(token);

				// due to a possible bug in BRITE, s.a.
				if ( bandwidth > maxBandwith )
					maxBandwith = bandwidth;

				if ( nodeid1 - firstnodeid >= Engine.getNumberOfNodes() || nodeid2 - firstnodeid >= Engine.getNumberOfNodes() ) {

					String text = "Error: edge defined for non-existing node!";
					infoOutput.write(text + "\n");
					System.err.println(text);

				}

				adjacencytable[nodeid1 - firstnodeid][nodeid2 - firstnodeid] = 1;
				adjacencytable[nodeid2 - firstnodeid][nodeid1 - firstnodeid] = 1;

				// the higher the bandwith the better the connection; but for
				// floyd_warshall we need
				// to reduce the costs to get the best connection; so we set the
				// cost to maxBandwith-bandwith
				Engine.getCosttable()[nodeid1 - firstnodeid][nodeid2 - firstnodeid] = (maxBandwith + 1) - bandwidth;
				Engine.getCosttable()[nodeid2 - firstnodeid][nodeid1 - firstnodeid] = (maxBandwith + 1) - bandwidth;
				if ( (maxBandwith + 1) - bandwidth < 0 ) {

					String text = new String("Negative presettet cost: maxBandwidth=" + maxBandwith + " bandwidth=" + bandwidth);
					System.out.println(text);
					infoOutput.write(text + "\n");

				}

				skipRestOfLine(file);
				break;

			}

			if ( skipEmptyLines(file) == false )
				return adjacencytable;

			token = getTokenAhead(file);

		}

		return adjacencytable;

	}

	protected boolean parseBRITEBrokernetFile(FileReaderBuffer file) throws Exception {

		// parse first node

		int firstnodeid = -1;
		String token = "";

		if ( skipToLineWithToken(file, "Model") == false )
			return false;

		nextLineToken(file);
		nextLineToken(file);
		nextLineToken(file);
		nextLineToken(file);

		model = nextLineToken(file);

		if ( isSupportedModel(model) == false ) {

			String text = new String("In file " + file.filename + ": model " + model + " not yet supported!");
			System.out.println(text);
			infoOutput.write(text + "\n");
			System.exit(1);

		}

		if ( skipToLineWithToken(file, "Nodes") == false )
			return false;

		if ( skipEmptyLines(file) == false )
			return false;

		// skip: "Nodes" ":" "("
		nextLineToken(file);
		nextLineToken(file);
		nextLineToken(file);

		token = nextLineToken(file);

		brokernodes = Integer.parseInt(token);

		if ( brokernodes > Engine.getNumberOfNodes() ) {

			String text = new String("Error: number of brokers exceeds number of total nodes!");
			infoOutput.write(text + "\n");
			System.err.println(text);
			return false;

		}

		if ( params.getSubscribers() > (Engine.getNumberOfNodes() - brokernodes) - 1 ) {

			String text = new String("Error: number of subscribers exceeds number of permissible nodes!");
			infoOutput.write(text + "\n");
			System.err.println(text);
			return false;

		}
		// skip rest
		if ( skipRestOfLine(file) == false )
			return false;

		token = getTokenAhead(file);

		try {
			// force break if first token is not a number
			Integer.parseInt(token);
		} catch (NumberFormatException e) {

			String text = "Error: need at least one node in brokernet-Britefile!";
			infoOutput.write(text + "\n");
			System.err.println(text);
			return false;
		}

		token = nextLineToken(file);

		firstnodeid = Integer.parseInt(token);

		skipRestOfLine(file);

		toplayerBrokerAdjacencytable = new byte[brokernodes][brokernodes];

		// parse edges

		@SuppressWarnings("unused")
		int edgeid, nodeid1, nodeid2;
		double length, delay, bandwidth;

		if ( skipToLineWithToken(file, "Edges") == false )
			return false;

		if ( skipEmptyLines(file) == false )
			return false;

		// skip line "Edges : ..."
		if ( skipRestOfLine(file) == false )
			return false;

		token = getTokenAhead(file);

		while (true) {

			try {
				// force break if first token is not a number
				Integer.parseInt(token);

			} catch (NumberFormatException e) {
				break;
			}

			while (token.equals("") == false) {

				token = nextLineToken(file);

				edgeid = Integer.parseInt(token);

				token = nextLineToken(file);

				nodeid1 = Integer.parseInt(token);

				token = nextLineToken(file);

				nodeid2 = Integer.parseInt(token);

				if ( nodeid1 - firstnodeid >= Engine.getNumberOfNodes() || nodeid2 - firstnodeid >= Engine.getNumberOfNodes() ) {

					String text = "Error: edge defined for non-existing node!";
					infoOutput.write(text + "\n");
					System.err.println(text);

				}

				toplayerBrokerAdjacencytable[nodeid1 - firstnodeid][nodeid2 - firstnodeid] = 1;
				toplayerBrokerAdjacencytable[nodeid2 - firstnodeid][nodeid1 - firstnodeid] = 1;

				skipRestOfLine(file);
				break;

			}

			if ( skipEmptyLines(file) == false )
				return true;

			token = getTokenAhead(file);

		}

		return true;

	}

	protected void makeNodes() throws Exception {

		int nodeid, xpos, ypos, subscriberscount = 0, nodecount = 0;
		NodeProperties nodeproperty;
		Node node;

		Iterator<NodeProperties> it = nodeproperties.iterator();

		while (it.hasNext()) {

			nodeproperty = it.next();

			nodeid = nodeproperty.id;
			xpos = nodeproperty.xpos;
			ypos = nodeproperty.ypos;

			if ( nodeid == Engine.getNumberOfNodes() - 1 ) {

				rssServer = rpsFactory.newRSSServerNode(xpos, ypos, params);
				// set the factories for creating and displaying the
				// feeds
				rssServer.setRssFeedFactory(rssFeedFactory);
				rssServer.setRssFeedRepresentationFactory(rssFeedRepresentationFactory);
				node = rssServer;
				rssServer.addToInitList();

			} else {

				if ( nodecount < brokernodes ) {

					node = rpsFactory.newBrokerNode(xpos, ypos, params);
					node.addToInitList();
					brokerlist.add((BrokerNode) node);

				} else {

					if ( params.getSubscribers() == 0 ) {

						if ( Engine.getSingleton().getRandom().nextFloat() < 0.5 )
							node = new TransferNode(xpos, ypos, params);
						else {

							node = rpsFactory.newPubSubNode(xpos, ypos, params);
							((PubSubNode) node).setRssFeedRepresentationFactory(rssFeedRepresentationFactory);
							((PubSubNode) node).addToInitList();
							pubsublist.add((PubSubNode) node);

						}
					} else {

						if ( subscriberscount < params.getSubscribers() ) {

							node = rpsFactory.newPubSubNode(xpos, ypos, params);
							((PubSubNode) node).setRssFeedRepresentationFactory(rssFeedRepresentationFactory);
							((PubSubNode) node).addToInitList();
							pubsublist.add((PubSubNode) node);
							subscriberscount++;

						} else {
							node = new TransferNode(xpos, ypos, params);
						}

					}
				}
			}

			node.setId(nodeid);
			node.setShowId(false);
			node.register();

			nodecount++;

		}

	}

	protected void makeEdges(int[][] adjacencytable) {

		EdgeFactory edgefactory = new EdgeFactory();

		// one edge per connection is enough, so we scan only a triagle of the
		// matrix
		for (int x = 0; x < Engine.getNumberOfNodes(); x++)
			for (int y = x; y < Engine.getNumberOfNodes(); y++)
				if ( adjacencytable[x][y] == 1 )
					edgefactory.newEdge(x, y, Engine.getCosttable()[x][y]).register();

	}

}
