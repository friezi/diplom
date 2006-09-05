/**
 * 
 */

import java.io.*;
import java.util.*;
import java.util.regex.*;

import org.apache.commons.math.MathException;
import org.apache.commons.math.distribution.DistributionFactory;
import org.apache.commons.math.distribution.TDistribution;
import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;

/**
 * @author friezi
 * 
 */
public class ConfidenceIntervalCalculator {

	public static class Item {

		public long time;

		public DescriptiveStatistics stat;

		public Item(long time, DescriptiveStatistics stat) {

			this.time = time;
			this.stat = stat;

		}

	}

	public static class Filter implements FilenameFilter {

		private Pattern pattern;

		public Filter(Pattern pattern) {
			this.pattern = pattern;
		}

		public boolean accept(File dir, String name) {

			Matcher matcher = pattern.matcher(name);

			if ( matcher.matches() == true )
				return true;

			return false;
		}

	}

	protected static final String suffix = ".gnuplotdata";

	protected static final String suffixpattern = "\\" + suffix;

	protected static final String ci_infix = "_ConfidenceIntervals";

	protected static final String mv_infix = "_MeanValues";

	protected static final String mvr_infix = "_MeanValueRanges";

	protected static Pattern gnuplotPattern = Pattern.compile("(.*)" + suffixpattern);

	protected static double alpha = 0.05;

	/**
	 * 
	 */
	public ConfidenceIntervalCalculator() {
		super();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {

		String testscenariosfilename = "testscenarios";

		if ( args.length != 1 ) {

			System.out.println("usage: <appname> <directory>");
			System.exit(1);

		}

		System.out.print("Calculating confidence-intervals ... ");

		String dirname = args[0];

		String testscenariosfile = dirname + "/" + testscenariosfilename;

		LineNumberReader scanner = new LineNumberReader(new FileReader(testscenariosfile));

		String line = null;
		while ( (line = scanner.readLine()) != null ) {

			String scenario = dirname + "/" + line;

			Properties properties = new Properties();

			properties.load(new FileInputStream(scenario));

			collectAndCalculate(dirname, properties.getProperty("gnuplotFileTotalTemporaryRequests"));
			collectAndCalculate(dirname, properties.getProperty("gnuplotFileStdDevCPP"));
			collectAndCalculate(dirname, properties.getProperty("gnuplotFileMeanValueCPP"));
			collectAndCalculate(dirname, properties.getProperty("gnuplotFileCoeffVarCPP"));
			collectAndCalculate(dirname, properties.getProperty("gnuplotFileAvgMsgDelayRatio"));

		}

		System.out.println("done");

	}

	protected static void collectAndCalculate(String dirname, String name) throws IOException, MathException {

		Matcher matcher = gnuplotPattern.matcher(name);
		matcher.matches();

		String totalpatternstring = matcher.group(1) + "\\.pass" + ".*" + suffixpattern;

		File currentdir = new File(dirname);
		String[] entries = currentdir.list(new Filter(Pattern.compile(totalpatternstring)));

		LinkedList<Item> statlist = new LinkedList<Item>();
		long time;
		double value;
		Item item = null;
		DescriptiveStatistics stat = null;
		int counter = 0;

		for ( String entry : entries ) {

			Iterator<Item> iterator = statlist.iterator();
			StreamTokenizer scanner = new StreamTokenizer(new FileReader(dirname + "/" + entry));
			scanner.parseNumbers();

			int line = 0;

			scanner.nextToken();
			while ( scanner.ttype != StreamTokenizer.TT_EOF ) {

				line++;

				time = (long) scanner.nval;
				// System.out.print(time + " ");

				scanner.nextToken();
				if ( scanner.ttype == StreamTokenizer.TT_EOF )
					throw new IOException(entry + " corrupt in line " + scanner.lineno() + "!");

				value = scanner.nval;
				// System.out.println(value);

				// liste aufbauen
				if ( counter == 0 ) {

					// if ( line > 1 ) {

					stat = DescriptiveStatistics.newInstance();
					stat.addValue(value);
					statlist.add(new Item(time, stat));

					// }

				} else {

					// if ( line > 1 ) {

					if ( iterator.hasNext() == false )
						throw new IOException(entry + ": too many entries!");

					item = iterator.next();

					if ( item.time != time )
						throw new IOException(entry + ": timevalues don't match!");

					item.stat.addValue(value);

					// }

				}

				scanner.nextToken();

			}

			counter++;

		}

		// System.out.println(entry);

		FileWriter ci_output = new FileWriter(dirname + "/" + matcher.group(1) + ci_infix + suffix);
		FileWriter mv_output = new FileWriter(dirname + "/" + matcher.group(1) + mv_infix + suffix);
		FileWriter mvr_output = new FileWriter(dirname + "/" + matcher.group(1) + mvr_infix + suffix);

		// write the datas
		for ( Item it : statlist ) {

			TDistribution tDistribution = DistributionFactory.newInstance().createTDistribution(it.stat.getN() - 1);
			double confidenceInterval = tDistribution.inverseCumulativeProbability(1d - alpha / 2d) * it.stat.getStandardDeviation()
					/ Math.sqrt(it.stat.getN());
			
			ci_output.write(String.valueOf(it.time) + " " + String.format(Locale.US, "% 9.9f", confidenceInterval) + "\n");
			mv_output.write(String.valueOf(it.time) + " " + String.format(Locale.US, "% 9.9f", it.stat.getMean()) + "\n");
			mvr_output.write(String.valueOf(it.time) + " " + String.format(Locale.US, "% 9.9f", it.stat.getMean() - confidenceInterval) + "\n");
			mvr_output.write(String.valueOf(it.time) + " " + String.format(Locale.US, "% 9.9f", it.stat.getMean() + confidenceInterval) + "\n");

		}

		ci_output.close();
		mv_output.close();
		mvr_output.close();

	}

}
