package graph;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.tinkerpop.gremlin.process.computer.KeyValue;
import org.apache.tinkerpop.gremlin.process.computer.MapReduce;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import com.google.common.collect.Iterators;

/**
 * @author C. Savkli, Dec 16, 2017
 * @version 1.0
 */

//
// A simple program that demonstrates use of map-reduce in Tinkerpop 3 to calculate a histogram.
// MapReduce programs do not have access to edge properties. Edge based processing can be
// done by vertex programs. 
//

public class Example4_MapReduce_AttributeDistribution implements MapReduce<String, Double, String, Double, Map<String, Double>>
{ 
	private String attrName;
	private int numVertices;
	private Graph g;
	
	public Example4_MapReduce_AttributeDistribution (Graph g, String degreeAttrName)  
	{
		this.attrName = degreeAttrName;
		this.numVertices = Iterators.size(g.vertices());
		this.g= g;
	}
	
	@SuppressWarnings("unchecked")
	@Override 
	public MapReduce<String, Double, String, Double, Map<String, Double>> clone() 
	{
		try 
		{ 
			return (MapReduce<String, Double, String, Double, Map<String, Double>>) super.clone(); 
		}
		catch(Exception e) { throw new IllegalStateException(); }
	}

	@Override
	public boolean doStage(Stage stage) 
	{
		if(Stage.MAP.equals(stage)) return true;          // will perform a map
		else if(Stage.REDUCE.equals(stage)) return true;  // will perform a reduce
		else if(Stage.COMBINE.equals(stage)) return true; // should be able to run on multiple machines
		else throw new IllegalStateException();
	}

	@Override
	public void map(Vertex vertex, MapEmitter<String, Double> emitter) 
	{
		String d = vertex.value(attrName).toString();
		emitter.emit(d, 1.);
	}
	
	@Override public void reduce(String key, Iterator<Double> values, MapReduce.ReduceEmitter<String, Double> emitter) 
	{
		// Number of messages received is equal to number of vertices that emitted a 1 for this key.
		// All we need to calculate to get frequency of such vertices is number of messages divided 
		// by the total number of vertices: 
		
		double keyVertices = (double)Iterators.size(values);
		double keyFrequency = keyVertices / numVertices;
		emitter.emit(key,  keyFrequency);
	}

	@Override
	public Map<String, Double> generateFinalResult(Iterator<KeyValue<String, Double>> keyValues) 
	{
		// Results for each key has already been aggregated at the reduce step and we need no further aggregation.
		// We can return the results in a hashmap. 
		
		Map<String, Double> result = new HashMap<>();
		keyValues.forEachRemaining((KeyValue<String, Double> k) -> { result.put(k.getKey().toString(), k.getValue()); });
		return result;
	}

	@Override
	public String getMemoryKey() 
	{
		return "attributeDistribution";
	}

	@SuppressWarnings("unchecked")
	public Map<String, Double> execute() 
	{
		Map<String, Double> map = null;
		try {
			map = (Map<String, Double>) g.compute().mapReduce(this).submit().get().memory().get(this.getMemoryKey());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}	
}