package assignments;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MyGraphUtils {

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

}
