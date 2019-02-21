package assignments;

import graph.GraphUtils;
import graph.MGraph;
import org.apache.commons.math3.linear.EigenDecomposition;
import org.apache.commons.math3.linear.SparseRealMatrix;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;

public class Module3 {

    private final String GRAPH_FILE = "GraphDatabases\\students.graphml";

    private final double zero = 0.000001;

    private TinkerGraph graph;

    public Module3()
    {
        graph = createGraph();
        getGraphMatrices(graph);
    }

    public TinkerGraph createGraph()
    {
        TinkerGraph graph = GraphUtils.readGraphML(GRAPH_FILE);
        return graph;
    }

    // This example demonstrates how to obtain 2 of the most commonly used matrices in graph analytics

    public void getGraphMatrices(TinkerGraph g)
    {
        boolean directed = false;

        MGraph graph = new MGraph(g, directed);

        //Adjacency
        SparseRealMatrix adjacency = graph.getAdjacency();
        EigenDecomposition eigenDecompAdj = new EigenDecomposition(adjacency);
        double[] eigenAdj = eigenDecompAdj.getRealEigenvalues();

        System.out.println("Adjacency Matrix");
        //printArray(eigenAdj);
        System.out.println("Eigen Min: " + getMin(eigenAdj) + "  Max: " + getMax(eigenAdj));
        System.out.println("Zero count: " + countZeros(eigenAdj));

        //Laplacian
        System.out.println("\nLaplacian Matrix");
        SparseRealMatrix laplacian = graph.getLaplacian();
        EigenDecomposition eigenDecompLapl = new EigenDecomposition(laplacian);
        double[] eigenLapl = eigenDecompLapl.getRealEigenvalues();

        //printArray(eigenLapl);
        System.out.println("Eigen Min: " + getMin(eigenLapl) + "  Max: " + getMax(eigenLapl));
        System.out.println("Zero count: " + countZeros(eigenLapl));
    }

    public void printArray(double[] arr){
        System.out.print("Eigen values:");
        for(int i = 0;i < arr.length;i++){
            System.out.print(" " + arr[i]);
        }
        System.out.println("");
    }

    public double getMin(double[] arr){
        double result = Double.MAX_VALUE;
        for(int i = 0;i < arr.length;i++){
            if(arr[i]< result){
                result = arr[i];
            }
        }
        return result;
    }

    public double getMax(double[] arr){
        double result = Double.MIN_VALUE;
        for(int i = 0;i < arr.length;i++){
            if(arr[i] > result){
                result = arr[i];
            }
        }
        return result;
    }

    public int countZeros(double[] arr){
        int result = 0;
        for(int i = 0;i < arr.length;i++){
            if(arr[i] > -1*zero && arr[i] < zero){
                //System.out.println("zero: " + arr[i]);
                result++;
            }
        }
        return result;
    }

    public static void main(String[] args)
    {
        new Module3();
    }
}

