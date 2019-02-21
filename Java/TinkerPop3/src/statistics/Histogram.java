package statistics;

import java.util.List;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.random.EmpiricalDistribution;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

/**
 * @author C. Savkli, Jan 22, 2015
 * @version 1.0
 */

public class Histogram
{

    public Histogram()
    {
    	// Generate some data from a normal distribution:
    	double mean = 5.0;
    	double std = 1.0;
    	NormalDistribution normal = new  NormalDistribution(mean, std);
    	double[] data = new double[100]; 
    	for(int i=0; i< 100; i++)
    	{
    		data[i] = normal.sample();
    	}
    	
    	// Create a histogram:
    	int binCount = 10;
    	EmpiricalDistribution histogram = new EmpiricalDistribution(binCount);
    	histogram.load(data);
    	
    	//Print general statistics of the histogram:
    	System.out.println("Histogram stats:");
    	System.out.println(histogram.getSampleStats());
    	
    	//Print statistics for each bin:
    	System.out.println("Bin stats:");
    	List<SummaryStatistics> stats = histogram.getBinStats();
    	int bin = 0;
    	for(SummaryStatistics stat : stats)
    	{
    		System.out.println("Bin : " + bin++);
    		System.out.println(stat);
    	}
    	
    	//Check how close cumulative distributions are for empirical data vs the true distribution:
    	System.out.println(histogram.cumulativeProbability(mean) + " " + normal.cumulativeProbability(mean));
	}  
    
	public static void main(String[] args)
	{
		new Histogram();
	}
}