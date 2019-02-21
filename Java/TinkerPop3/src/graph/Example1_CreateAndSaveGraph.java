package graph;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;

/**
 * @author C. Savkli, Dec 20, 2017
 * @version 1.0
 */

public class Example1_CreateAndSaveGraph
{
	private TinkerGraph graph;
	
	private String GRAPH_FILE = "GraphDatabases\\simple.graphml";
	
	public Example1_CreateAndSaveGraph()
	{
		// Create an empty graph
		System.out.println("Create an empty graph");
		graph = TinkerGraph.open();

		// Populate it
		System.out.println("Add vertices and edges ");
		populateGraph();	
		
		// Save graph to file
		System.out.println("Save graph to " + GRAPH_FILE);		
		GraphUtils.saveGraphML(graph,GRAPH_FILE);
		
		// Read graph from file
		System.out.println("Read graph from " + GRAPH_FILE);
		graph = GraphUtils.readGraphML(GRAPH_FILE);
		
		// Iterate over elements
		
		iterateVertices();
		iterateEdges();
	}
	
	public void populateGraph()
	{		
        Vertex p1 = graph.addVertex("name", "p1", "age", 20);
        Vertex p2 = graph.addVertex("name", "p2", "age", 22);
        Vertex p3 = graph.addVertex("name", "p3", "age", 21);
        Vertex p4 = graph.addVertex("name", "p4", "age", 21);
        Vertex p5 = graph.addVertex("name", "p5", "age", 24);
        Vertex p6 = graph.addVertex("name", "p6", "age", 23);
        
        p1.addEdge("knows", p2, "weight", 0.5f);
        p1.addEdge("knows", p3, "weight", 1.0f);
        p1.addEdge("knows", p5, "weight", 0.4f);
        p1.addEdge("knows", p6, "weight", 1.0f);
        p2.addEdge("knows", p4, "weight", 0.4f);
        p3.addEdge("knows", p4, "weight", 0.2f);
        p2.addEdge("knows", p3, "weight", 0.7f);
	}	
	
	public void iterateVertices()
	{
		GraphTraversalSource g = graph.traversal();    
		
	    GraphTraversal<Vertex, Vertex> t = g.V().unfold();
		
        while(t.hasNext())
        {
        	Vertex v = t.next();
        	
        	System.out.println("Vertex: name: "+v.value("name") +":\t age = " + v.value("age"));
        }
	}
		
	public void iterateEdges()
	{
		GraphTraversalSource g = graph.traversal();    
		
	    GraphTraversal<Edge, Edge> t = g.E().unfold();
		
        while(t.hasNext())
        {
        	Edge e = t.next();
        	
        	System.out.println("Edge: \t"+e.outVertex().value("name") +" -> "+ e.inVertex().value("name") +"\t w: " + e.value("weight"));
        }
	}

	public static void main(String[] args)
	{
		new Example1_CreateAndSaveGraph();
	}
}
