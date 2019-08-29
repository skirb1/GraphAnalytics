package FinalProject;

import graph.GraphUtils;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;

import java.io.File;
import java.io.FilenameFilter;
import java.util.*;

public class MyGraphUtils extends GraphUtils {

    public static List<String> getNetstatFiles(String directory){
        File f = new File(directory);

        FilenameFilter graphmlFilter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".nattributes");
            }
        };

        File[] fileList = f.listFiles(graphmlFilter);
        List<String> filePaths = new ArrayList<>();

        for (File file : fileList) {
            if (!file.isDirectory()) {
                filePaths.add(file.getAbsolutePath());
            }
        }

        System.out.println("File count: " + filePaths.size());
        return filePaths;
    }

    public static List<String> getGraphFiles(String directory){
        File f = new File(directory);

        FilenameFilter graphmlFilter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".graphml");
            }
        };

        File[] fileList = f.listFiles(graphmlFilter);
        List<String> filePaths = new ArrayList<>();

        for (File file : fileList) {
            if (!file.isDirectory()) {
                filePaths.add(file.getAbsolutePath());
            }
        }

        System.out.println("File count: " + filePaths.size());
        return filePaths;
    }

    public static Map<String, Object> getPropertyHash(TinkerGraph graph, String property){
        GraphTraversalSource g = graph.traversal();
        return g.V().valueMap(property).order().next();
    }

    /**
     * Checks if one vertex is linked to another
     * @param from Vertex linked from
     * @param to Vertex linked to
     * @return True if the vertices are linked
     */
    public static boolean hasLink(Vertex from, Vertex to){
        boolean result = false;

        Iterator it = from.vertices(Direction.BOTH);
        while(it.hasNext()){
            if(it.next().equals(to)){
                result = true;
                break;
            }
        }

        return result;
    }

    public static int countVertices(GraphTraversalSource g){
        return g.V().count().next().intValue();
    }

    public static int countVertices(TinkerGraph graph)
    {
        GraphTraversalSource g = graph.traversal();
        return countVertices(g);
    }

    public static double getAverageDegree(GraphTraversalSource g){
        return g.V().bothE().count().next().doubleValue() / g.V().count().next().doubleValue();
    }

    public static double getAverageDegree(TinkerGraph graph){
        GraphTraversalSource g = graph.traversal();
        return getAverageDegree(g);
    }

    /**
     * Calculates the percentage of a vertex's neighbors that are linked
     * out of all possible links between neighbors
     * @param v Vertex to calculate on
     * @return local clustering coefficient for Vertex v
     */
    public static double clusterCoefficientLocal(Vertex v){
        double result = 0;
        int triangles = 0;
        int degree = 0;
        Map<Integer, Vertex> neighborMap = new HashMap<>();

        //add neighbors to map
        Iterator<Vertex> neighbors = v.vertices(Direction.BOTH);
        while(neighbors.hasNext()){
            Vertex neighbor = neighbors.next();
            neighborMap.put(neighbor.hashCode(), neighbor);
            degree++;
        }

        //see how many neighbors are linked
        neighbors = v.vertices(Direction.BOTH);
        while(neighbors.hasNext()){
            Vertex neighbor = neighbors.next();
            Iterator otherNeighbors = neighbor.vertices(Direction.BOTH);
            while(otherNeighbors.hasNext()){
                if(neighborMap.containsKey(otherNeighbors.next().hashCode())){
                    triangles++;
                }
            }
        }

        double denominator = (degree * (degree - 1));
        if(denominator > 0) {
            result = triangles / denominator;
        }
        return result;
    }


}
