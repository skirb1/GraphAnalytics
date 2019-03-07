package assignments;

import graph.GraphUtils;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;

public class ModuleBase {

    protected String GRAPH_FILE;

    protected TinkerGraph graph;

    public ModuleBase(){ }

    public TinkerGraph createGraph()
    {
        TinkerGraph graph = GraphUtils.readGraphML(GRAPH_FILE);
        return graph;
    }

}
