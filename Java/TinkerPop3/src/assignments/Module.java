package assignments;

import graph.GraphUtils;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;

public class Module {

    private String GRAPH_FILE = "GraphDatabases\\karate.graphml";

    public Module(){

        TinkerGraph graph = GraphUtils.readGraphML(GRAPH_FILE);

    }

    public static void main(String[] args){
        new Module();
    }
}
