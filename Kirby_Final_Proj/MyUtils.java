package FinalProject;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

public class MyUtils {

    public static Double sumVector(Vector<Double> vec){
        Double result = 0.0;
        for(Double num : vec){
            result += num;
        }

        return result;
    }

    public static double getMin(double[] arr){
        double result = Double.MAX_VALUE;
        for(int i = 0;i < arr.length;i++){
            if(arr[i]< result){
                result = arr[i];
            }
        }
        return result;
    }

    public static double getMax(double[] arr){
        double result = Double.MIN_VALUE;
        for(int i = 0;i < arr.length;i++){
            if(arr[i] > result){
                result = arr[i];
            }
        }
        return result;
    }

    public static int getMaxIndex(double[] arr){
        int index = -1;
        double result = Double.MIN_VALUE;
        for(int i = 0;i < arr.length;i++){
            if(arr[i] > result){
                result = arr[i];
                index = i;
            }
        }
        return index;
    }

    public static int countIterator(Iterator it){
        int count = 0;
        while(it.hasNext())
        {
            it.next();
            count++;
        }
        return count;
    }

    public static double getAverageFromInt(List<Integer> list){
        int sum = 0;
        for(Integer i : list){
            sum += i;
        }
        return (double)sum / list.size();
    }

    public static double getAverage(List<Double> list){
        double sum = 0;
        for(Double i : list){
            sum += i;
        }
        return sum / list.size();
    }
}
