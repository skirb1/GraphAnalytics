package graph;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

import org.apache.tinkerpop.gremlin.process.computer.ComputerResult;
import org.apache.tinkerpop.gremlin.process.computer.Memory;
import org.apache.tinkerpop.gremlin.process.computer.VertexProgram;
import org.apache.tinkerpop.gremlin.process.traversal.Order;
import org.apache.tinkerpop.gremlin.process.traversal.P;
import org.apache.tinkerpop.gremlin.process.traversal.Path;
import org.apache.tinkerpop.gremlin.process.traversal.Scope;
import org.apache.tinkerpop.gremlin.process.traversal.Traversal;
import org.apache.tinkerpop.gremlin.process.traversal.Traverser;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import static org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__.* ;

import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;

/**
 * @author C. Savkli, Jan 15, 2018
 * @version 1.0
 */

public class Example3_BasicGraphOperations
{
	private TinkerGraph graph;
	private String GRAPH_FILE = "GraphDatabases\\students.graphml";
	
	public Example3_BasicGraphOperations()
	{
		graph = GraphUtils.readGraphML(GRAPH_FILE);

//		  iterateVertices();
		
//		  iterateEdges();
		
//	      iterateVerticesWithQuery();
	    
//	      iterateVerticesWithRangeQuery();
		
//	      iterateVerticesAndNeighborsWithQuery();
	      
//	      iterateVerticesAndNeighborsWithQuery2();
	      
//        getAttributeDistributionWithTraversal();
		
//        setDegreesWithTraversal();
		
//	      depthFirstSearch();
		
//        useUserDefinedFunctionsWithTraversal();
        
//        getAttributeDistributionWithMapReduce();
		
//		  setDegreesWithVertexProgram();
		  		  
	}
	
	public void iterateVertices()
	{
		GraphTraversalSource g = graph.traversal();    
		
	    GraphTraversal<Vertex, Vertex> t = g.V().unfold();
		
        while(t.hasNext())
        {
        	Vertex v = t.next();
        	
        	System.out.println(v.value("personid") +":\t grade = " + v.value("grade") + "\t school = " + v.value("school"));
        }
	}
		
	public void iterateEdges()
	{
		GraphTraversalSource g = graph.traversal();    
		
	    GraphTraversal<Edge, Edge> t = g.E().unfold();
		
        while(t.hasNext())
        {
        	Edge e = t.next();
        	
        	System.out.println("Edge :\t"  + 
        	e.outVertex().value("personid") +" -> "+ e.inVertex().value("personid") + "\t w = "+ e.value("w"));
        }
	}
	
	public void iterateVerticesWithQuery()
	{
        ArrayList<Vertex> list = new ArrayList<Vertex>();
        GraphTraversalSource g = graph.traversal();       
        
        g.V().has("sex","1").has("school","0").fill(list);
                
        for(Vertex v:list)
        {
        	System.out.println(v.value("personid") + ":\t  grade = "+v.value("grade") + "\t  sex = " + v.value("sex")+ "\t  school = " + v.value("school"));
        }
	}
	
	//
	// Interval queries are exclusive of boundaries
	//
	public void iterateVerticesWithRangeQuery()
	{
		GraphTraversalSource g = graph.traversal();        
        
	    GraphTraversal<Vertex, Vertex> t = g.V().has("sex", "1").has("grade", P.between(6, 11)).order().by("grade", Order.decr).unfold();
	
        while(t.hasNext())
        {
        	Vertex v = t.next();
        	
        	System.out.println(v.value("personid") +":\t grade = " + v.value("grade") + "\t  sex = " + v.value("sex")+ "\t school = " + v.value("school"));
        }
	}
	
	// Query Vertex->Edge->Vertex triplets subject to an edge constraint
	
	public void iterateVerticesAndNeighborsWithQuery()
	{
		ArrayList<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		GraphTraversalSource g = graph.traversal();       
		
		g.V().as("a").outE().has("w",P.between("3", "5")).as("e").inV().as("b").select("a","e","b").fill(list);
		
		for(Map<String,Object> v:list)
		{
			Vertex v1 = (Vertex) v.get("a");
			Edge e = (Edge) v.get("e");
			Vertex v2 = (Vertex) v.get("b");
			
			System.out.println("id1 = " + v1.value("personid") +"\t w = " +e.value("w")+"\t id2 = " + v2.value("personid"));
		}
	}
	
	// Query Vertex->Edge->Vertex triplets subject to constraints
	
	public void iterateVerticesAndNeighborsWithQuery2()
	{
		ArrayList<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		
		GraphTraversalSource g = graph.traversal();       
		
		//Students who are at different schools and have different genders but are friends:
		
		Traversal<?,?>[] constraints = {where("a",P.neq("b")).by("sex"),  where("a",P.neq("b")).by("school") };
		
		g.V().as("a").out().as("b").and(constraints).select("a","b").fill(list);
				
		for(Map<String,Object> v:list)
		{
			Vertex v1 = (Vertex) v.get("a");
			Vertex v2 = (Vertex) v.get("b");
			
			System.out.println("id1 = " + v1.value("personid") +"\t school = " +v1.value("school") +"\t sex = " +v1.value("sex")+ " -- id2 = "+v2.value("personid")+"\t school = " + v2.value("school")+"\t sex = " +v2.value("sex"));
		}
	}
	
	// Example traversal for gathering statistics
	
	public void getAttributeDistributionWithTraversal() 
	{
        GraphTraversalSource g = graph.traversal();  
                
		@SuppressWarnings("unchecked")
		Map<String, Map<Object,Long>> stats = (Map<String, Map<Object, Long>>) g.V().groupCount("a").by("school").groupCount("b").by("grade").cap("a","b").next();
		
		Map<Object,Long> schools= stats.get("a");
		System.out.println("schools: " + schools);
		
		Map<Object,Long> grades= stats.get("b");
		System.out.println("grade: " + grades);	
	}
		
	// Example traversal for setting degrees
	
	public void setDegreesWithTraversal()
	{
        GraphTraversalSource g = graph.traversal();        
        
        g.V().property("degree_o", outE().count()).iterate();
        g.V().property("degree_i", inE().count()).iterate();
        g.V().property("degree", bothE().count()).iterate();   
        
        List<String> attributes = new ArrayList<String>();
        attributes.add("personid");
        attributes.add("degree_o");
        attributes.add("degree_i");
        attributes.add("degree");
        
        GraphUtils.printAttributes(graph, attributes);
	}
	
	// Find 10 paths between 2 vertices using Traversals:
	
	public void depthFirstSearch()
	{
		GraphTraversalSource g = graph.traversal();

		Vertex fromNode = g.V().has("personid",  "10").next();
		Vertex toNode   = g.V().has("personid",  "20").next();

		int paths = 10;
		
		ArrayList<Path> list = new ArrayList<Path>();
		
		g.V(fromNode).repeat(out().simplePath()).until(is(toNode)).limit(paths).path().fill(list);

		int i=0;
		for(Path p: list)
		{
			System.out.print("Path " +i++ +" : ");
			Iterator<Object> it = p.iterator();
			while(it.hasNext())
			{
				Vertex v = (Vertex) it.next();
				System.out.print("\t" + v.value("personid").toString());
			}
			System.out.println();
		} 
	}
	
	// Using user defined functions during traversal. This example sets degrees and normalizes them by max degree.
	
	public void useUserDefinedFunctionsWithTraversal()
	{
		GraphTraversalSource g = graph.traversal();
		
        g.V().property("degree", bothE().count()).iterate();   
        
		Number max = (Number) g.V().values("degree").max(Scope.global).next();

		Function<Traverser<Vertex>,Vertex> normalizeFunction = (Traverser<Vertex> l) -> 
		{ 
			Vertex v = l.get(); 
			double normDegree = (Double.parseDouble(v.property("degree").value().toString()))/max.doubleValue();
			v.property("normalized_degree", normDegree); 
			return l.get();
		};
		
		Function<Traverser<Vertex>,Vertex> printFunction = (Traverser<Vertex> l) -> 
		{ 
			Vertex v = l.get(); 
			System.out.println("id = "+v.value("personid") + "  normalized degree = " + v.property("normalized_degree").value()); 
			return l.get();
		};

		g.V().map(normalizeFunction).map(printFunction).iterate();
	}
	
	// Map-Reduce processing is particularly suitable for doing various aggregations in a parallelized way. 
	// This simple example demonstrates use of Map-Reduce for calculating degree distribution in a graph. 
	
	public void getAttributeDistributionWithMapReduce()
	{
		// First create a degree attribute
        GraphTraversalSource g = graph.traversal();        
        g.V().property("degree", bothE().count()).iterate();
        
        // Use a map-reduce to calculate distribution of degrees
        Example4_MapReduce_AttributeDistribution mr = new Example4_MapReduce_AttributeDistribution(graph, "degree");
        Map<String, Double> map = mr.execute();
        
        // Print out the result
        for(String degree: map.keySet())
        {
        	System.out.println("Degree: \t"+degree +"\t frequency:\t "+map.get(degree));
        }
	}
		
	// Vertex Programs provide an interface for implementing algorithms using Bulk-Synchronous-Parallel (BSP) processing
	// This is a simple example where in and out degrees of vertices are calculated using a vertex program.
	// Besides vertex properties program also calculates average degree in the graph to demonstrate how you can maintain global data
	// within the scope of a vertex program. A vertex program is really not needed to determine vertex degrees, however this program
	// serves as a simple example showing how to construct and use a vertex program and it has all the structure you may need in 
	// implementing more complex algorithms. 
	
	public void setDegreesWithVertexProgram()
	{
		TinkerGraph graph = TinkerGraph.open();
        Vertex p1 = graph.addVertex("name", "p1");
        Vertex p2 = graph.addVertex("name", "p2");
        Vertex p3 = graph.addVertex("name", "p3");
        Vertex p4 = graph.addVertex("name", "p4");
        
        p1.addEdge("knows", p2);
        p1.addEdge("knows", p3);
        p2.addEdge("knows", p4);
        p3.addEdge("knows", p4);
        p2.addEdge("knows", p3);
        		
		try 
		{
			// Initialize vertex program:

			Example5_VertexProgram_Degree P = new Example5_VertexProgram_Degree();
			VertexProgram<String> program = P.build().iterations(1).create(graph);

			// Execute vertex program:

			ComputerResult res = graph.compute().program(program).submit().get();
			graph = (TinkerGraph) res.graph();

			// Print calculated vertex properties:

	        List<String> attributes = new ArrayList<String>();
	        attributes.add("name");
	        attributes.add("degree_o");
	        attributes.add("degree_i");
	        
	        GraphUtils.printAttributes(graph, attributes);
	        
	        // Print calculated global graph properties:
	        
	        Memory memory = res.memory();
	        System.out.println("Average degree = " + memory.get("average_degree"));
		} 
		catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
	}
			
	public static void main(String[] args)
	{
		new Example3_BasicGraphOperations();
	}
}
