package matrices;

import org.apache.commons.math3.linear.SparseRealMatrix;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;

import graph.MGraph;

/**
 * @author C. Savkli, Dec 20, 2017
 * @version 1.0
 * 
 */

public class Example7_GetGraphMatrices
{
	private TinkerGraph graph;
	
	public Example7_GetGraphMatrices()
	{
		// Create a graph
		
		graph = createGraph();	
		
		// Print out adjacency and laplacian matrices:
		
		getGraphMatrices(graph);
	}
	
	//       1                  |0 1 1 0 1|      | 3 -1 -1  0 -1|
	//     / | \                |1 0 1 1 0|      |-1  3 -1 -1  0|
	//    3  |   0 -- 4     A = |1 1 0 1 0|  L = |-1 -1  3 -1  0|
	//     \ | /                |0 1 1 0 0|      | 0 -1 -1  2  0|
	//       2                  |1 0 0 0 0|      |-1  0  0  0  1|
	
	public TinkerGraph createGraph()
	{		
		TinkerGraph graph = TinkerGraph.open();
		
        Vertex p0 = graph.addVertex("name", "p0");
        Vertex p1 = graph.addVertex("name", "p1");
        Vertex p2 = graph.addVertex("name", "p2");
        Vertex p3 = graph.addVertex("name", "p3");
        Vertex p4 = graph.addVertex("name", "p4");
        
        p0.addEdge("knows", p1);
        p0.addEdge("knows", p2);
        p0.addEdge("knows", p4);
        p1.addEdge("knows", p3);
        p1.addEdge("knows", p2);
        p2.addEdge("knows", p3);
        
        return graph;
	}	
		
	// This example demostrates how to obtain 2 of the most commonly used matrices in graph analytics
	
	public void getGraphMatrices(TinkerGraph g)
	{
		boolean directed = false;
		
		MGraph graph = new MGraph(g, directed);		        
		
        SparseRealMatrix adjacency = graph.getAdjacency();
        
        System.out.println("Adjacency matrix :\n\n" + adjacency + "\n");
                
        SparseRealMatrix laplacian = graph.getLaplacian();
        
        System.out.println("Laplacian matrix :\n\n" + laplacian + "\n");        

        System.out.println("Matrix elements and corresponding vertex ids:\n");
        
        for(int i=0; i<adjacency.getRowDimension(); i++)
        {
        	System.out.println("Matrix element: \t" + i +" -> Vertex id: \t "+ graph.getVertexIDForMatrixElement(i));
        }
	}

	public static void main(String[] args)
	{
		new Example7_GetGraphMatrices();
	}
}
