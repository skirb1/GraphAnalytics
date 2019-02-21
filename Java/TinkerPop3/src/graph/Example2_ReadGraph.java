package graph;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;

/**
 * @author C. Savkli, Dec 17, 2017
 * @version 1.0
 */

public class Example2_ReadGraph
{
	private TinkerGraph graph;
	private String GRAPH_FILE = "GraphDatabases\\karate.graphml";
	
	public Example2_ReadGraph()
	{
		graph = GraphUtils.readGraphML(GRAPH_FILE);

		iterateVertices();		
		iterateEdges();		
	}
	
	public void iterateVertices()
	{
		GraphTraversalSource g = graph.traversal();    
		
	    GraphTraversal<Vertex, Vertex> t = g.V().unfold();
		
        while(t.hasNext())
        {
        	Vertex v = t.next();
        	
        	System.out.println("Person: "+v.value("personid") +":\t partition = " + v.value("prtn"));
        }
	}
		
	public void iterateEdges()
	{
		GraphTraversalSource g = graph.traversal();    
		
	    GraphTraversal<Edge, Edge> t = g.E().unfold();
		
        while(t.hasNext())
        {
        	Edge e = t.next();
        	
        	System.out.println("Edge: \t"+e.outVertex().value("personid") +"\t -> \t"+ e.inVertex().value("personid"));
        }
	}
	
	public static void main(String[] args)
	{
		new Example2_ReadGraph();
	}
}
