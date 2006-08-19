import org.apache.commons.math.MathException;
import org.apache.commons.math.distribution.DistributionFactory;
import org.apache.commons.math.distribution.TDistribution;
import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;

/**
 * Calculates the mean value and the 95 percent confidence interval of the
 * samples given on the commandline. The first value is ignored.
 * 
 * The output format is: args[0] mean confidence-interval
 * 
 * @author michael.jaeger@acm.org
 */
public class ConfidenceInterval {

	public static void main(String[] args) throws Exception, MathException {
		// Set to 0.05 for 95% confidence interval

		double alpha = 0.05;

		DescriptiveStatistics stats = DescriptiveStatistics.newInstance();

		int i = 0;
		for ( String sample : args ) {

			if ( i != 0 ) {
				stats.addValue(Double.parseDouble(sample));
			}
			
			i++;
		}

		TDistribution tDistribution = DistributionFactory.newInstance().createTDistribution(stats.getN() - 1);

		System.out.println(args[0] + " " + stats.getMean() + " " + tDistribution.inverseCumulativeProbability(1d - alpha / 2d)
				* (stats.getStandardDeviation() / Math.sqrt(stats.getN())));
	}
}
