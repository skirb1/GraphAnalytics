package graph;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.OpenMapRealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;

/**
 * @author C. Savkli, Dec 16, 2017
 * @version 1.0
 * 
 * A class that facilitates working with graph matrices
 * 
 */

public class MGraph
{
	private OpenMapRealMatrix adjacency = null;
	private OpenMapRealMatrix laplacian = null;
	private RealVector degrees = null;
	
	private Map<String,Integer> vertexIndices = null;
	private Map<Integer,String> vertexIDs = null;
	private int vertexCount = 0;
	
	public TinkerGraph graph;
	private boolean directed = false;
	
	public MGraph(TinkerGraph g)
	{
		this.graph = g;
		initVertexIndices();		
	}
	
	public MGraph(TinkerGraph g, boolean directed)
	{
		this.graph = g;
		this.directed  = directed;
		initVertexIndices();		
	}
	
	public OpenMapRealMatrix getAdjacency()
	{
		if(adjacency == null) initAdjacency();
		return adjacency;
	}

	public OpenMapRealMatrix getLaplacian()
	{
		if(adjacency == null) initAdjacency();
		initLaplacian();
		return laplacian;
	}
	
	private void initAdjacency()
	{
		adjacency = new OpenMapRealMatrix(this.vertexCount, this.vertexCount);
		
		GraphTraversalSource g = graph.traversal();    
	    GraphTraversal<Edge, Edge> t = g.E().unfold();
		
        while(t.hasNext())
        {
        	Edge e = t.next();
			Vertex from = e.outVertex();
			int i = vertexIndices.get(from.id().toString());
			Vertex to = e.inVertex();
			int j = vertexIndices.get(to.id().toString());

			adjacency.setEntry(i, j, 1.0);
			if(!directed) adjacency.setEntry(j, i, 1.0);
        }
        
		RealVector ones = new ArrayRealVector(vertexCount,1.0);
		
		degrees = adjacency.operate(ones);
	}
	
	private void initLaplacian()
	{
		initVertexIndices();
		
		laplacian = (OpenMapRealMatrix) adjacency.copy().scalarMultiply(-1);
		
		for(int i=0; i< vertexCount; i++)
		{
			laplacian.setEntry(i, i, degrees.getEntry(i) + laplacian.getEntry(i, i));
		}
	}
	
	public String getVertexIDForMatrixElement(int j)
	{
		return vertexIDs.get(j);
	}

	public int getVertexIndexFromID(String id){ return vertexIndices.get(id); }
	
	private void initVertexIndices()
	{
		vertexIndices = new HashMap<>();
		vertexIDs = new HashMap<>();

		int counter = 0;
		GraphTraversal<Vertex, Object> t = graph.traversal().V().unfold();

		while (t.hasNext()) 
		{
			Vertex v = (Vertex) t.next();
			vertexIndices.put(v.id().toString(), counter);
			vertexIDs.put(counter, v.id().toString());
			counter++;
		}
		vertexCount = counter;
	}	
}
