package assignments;

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

}
