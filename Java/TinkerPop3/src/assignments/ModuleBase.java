package assignments;

import graph.GraphUtils;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;

public class ModuleBase {

    protected TinkerGraph graph;

    public ModuleBase(){ }

    public TinkerGraph createGraph(String graph_file)
    {
        TinkerGraph graph = GraphUtils.readGraphML(graph_file);
        return graph;
    }

}
