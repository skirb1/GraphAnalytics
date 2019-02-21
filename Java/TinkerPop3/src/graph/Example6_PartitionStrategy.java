package graph;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.strategy.decoration.PartitionStrategy;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;

/**
 * @author C. Savkli, Dec 20, 2017
 * @version 1.0
 * 
 * This class demostrates usage of PartitionStrategy to localize vertices in graph partitions for potentially improving performance. 
 * Partition labels provide information to storage back ends to store vertices that belong to a given partition close to each other 
 * for minimizing data movement
 * 
 */

public class Example6_PartitionStrategy
{
	private TinkerGraph graph;
		
	public Example6_PartitionStrategy()
	{
		// Create a graph
		
		System.out.println("Create graph using a partitioning strategy ");
		
		graph = createGraphWithPartitions();	
						
		// Iterate over vertices
		
		System.out.println("Iteratate over all vertices ");
		
		iterateVertices();
		
		// Iterate over vertices on partition A	
		
		System.out.println("Iteratate only over vertices in a given partition ");
		
		iterateVerticesOnPartitionA();
	}
	
	public TinkerGraph createGraphWithPartitions()
	{		
		TinkerGraph graph = TinkerGraph.open();
		
		PartitionStrategy partitionStrategyA = PartitionStrategy.build().partitionKey("_partition").writePartition("A").readPartitions("A").create();
		PartitionStrategy partitionStrategyB = PartitionStrategy.build().partitionKey("_partition").writePartition("B").readPartitions("B").create();
		
		GraphTraversalSource g = graph.traversal();    
        Vertex p1 = g.withStrategies(partitionStrategyA).addV().property("name", "p1").next();
        Vertex p2 = g.withStrategies(partitionStrategyA).addV().property("name", "p2").next();
        Vertex p3 = g.withStrategies(partitionStrategyB).addV().property("name", "p3").next();
        Vertex p4 = g.withStrategies(partitionStrategyB).addV().property("name", "p4").next();
		
        p1.addEdge("knows", p2);
        p1.addEdge("knows", p3);
        p2.addEdge("knows", p4);
        p3.addEdge("knows", p4);
        
        return graph;
	}	
	
	public void iterateVertices()
	{
		GraphTraversalSource g = graph.traversal();    
		
	    GraphTraversal<Vertex, Vertex> t = g.V().unfold();
		
        while(t.hasNext())
        {
        	Vertex v = t.next();
        	
        	System.out.println("Vertex: name: "+v.value("name") +":\t partition = " + v.value("_partition"));
        }
	}
		
	public void iterateVerticesOnPartitionA()
	{
		GraphTraversalSource g = graph.traversal(); 
		
		PartitionStrategy partitionStrategyA = PartitionStrategy.build().partitionKey("_partition").writePartition("A").readPartitions("A").create();

		GraphTraversalSource gA = g.withStrategies(partitionStrategyA);
		
	    GraphTraversal<Vertex, Vertex> t = gA.V().unfold();
		
        while(t.hasNext())
        {
        	Vertex v = t.next();
        	
        	System.out.println("Vertex: name: "+v.value("name") +":\t partition = " + v.value("_partition"));
        }
	}

	public static void main(String[] args)
	{
		new Example6_PartitionStrategy();
	}
}
