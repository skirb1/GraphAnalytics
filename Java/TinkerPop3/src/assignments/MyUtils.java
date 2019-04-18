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
}
