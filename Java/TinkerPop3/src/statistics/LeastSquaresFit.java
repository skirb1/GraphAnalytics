package statistics;

import java.util.Arrays;
import java.util.Map;

import org.apache.commons.math3.analysis.interpolation.LinearInterpolator;
import org.apache.commons.math3.analysis.interpolation.SplineInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;
import org.apache.commons.math3.stat.regression.SimpleRegression;

/**
 * @author C. Savkli, Jan 20, 2015
 * @version 1.0
 */

public class LeastSquaresFit 
{
  public LeastSquaresFit() 
  {
    // Linear
    double[][] dataSetLinear = { {0, 1}, {1, 2}, {2, 3}, {3, 4}};
    double[] fLin = linearFit(dataSetLinear);
    PolynomialFunction fL = new PolynomialFunction(fLin);
    System.out.println("Linear : " + fL);

    // Spline
    double[] dataSetX = {0, 1, 2, 3, 4};
    double[] dataSetY = {5, 3, 3, 5, 9};
    SplineInterpolator interpolator = new SplineInterpolator();
    PolynomialSplineFunction spline = interpolator.interpolate(dataSetX, dataSetY);
    System.out.println("spline at 2.5 : " + spline.value(2.5));

    // Exponential
    double[][] dataSetExponential = { {0, 1}, {1, 0.5}, {2, 0.25}, {3, 0.125}};
    double[] fExp = exponentialFit(dataSetExponential);
    System.out.println("Exponential : " + Arrays.toString(fExp));
  }


  //
  // y = a * e^( b*x ) , log(y) = log(a) + b*x, returns {a,b}
  //
  public static double[] exponentialFit(double[][] data) 
  {
    double[][] eData = data.clone();

    for (int i = 0; i < eData.length; i++) {
      double[] d = eData[i];
      d[1] = Math.log(d[1]);
    }

    double[] fit = linearFit(eData);
    fit[0] = Math.exp(fit[0]);

    return fit;
  }

  public static double[] linearFit(double[][] data) 
  {
    SimpleRegression regression = new SimpleRegression();
    regression.addData(data);
    System.out.println(regression.getSlopeStdErr() + " " + regression.getSignificance());
    double[] fit = {regression.getIntercept(), regression.getSlope()};
    return fit;
  }

  public static void main(String[] args) 
  {
    new LeastSquaresFit();
  }

  public static PolynomialSplineFunction getPolynomialSplineFromData(Map<Double, Double> data) {
    SplineInterpolator interpolator = new SplineInterpolator();

    double[] keys = new double[data.size()];
    double[] values = new double[data.size()];
    int i = 0;
    for (double key : data.keySet()) {
      keys[i] = key;
      values[i] = data.get(key);
      i++;
    }

    PolynomialSplineFunction spline = null;

    if (keys.length > 2) {
      spline = interpolator.interpolate(keys, values);
    }

    return spline;
  }

  public static PolynomialSplineFunction getLinearSplineFromData(Map<Double, Double> data) 
  {
    LinearInterpolator interpolator = new LinearInterpolator();

    double[] keys = new double[data.size()];
    double[] values = new double[data.size()];
    int i = 0;
    for (double key : data.keySet()) {
      keys[i] = key;
      values[i] = data.get(key);
      i++;
    }

    PolynomialSplineFunction spline = null;

    if (keys.length > 2) {
      spline = interpolator.interpolate(keys, values);
    }

    return spline;
  }
}
