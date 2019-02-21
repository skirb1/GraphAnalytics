package graph;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.configuration.Configuration;
import org.apache.tinkerpop.gremlin.process.computer.GraphComputer;
import org.apache.tinkerpop.gremlin.process.computer.Memory;
import org.apache.tinkerpop.gremlin.process.computer.MemoryComputeKey;
import org.apache.tinkerpop.gremlin.process.computer.MessageScope;
import org.apache.tinkerpop.gremlin.process.computer.Messenger;
import org.apache.tinkerpop.gremlin.process.computer.VertexComputeKey;
import org.apache.tinkerpop.gremlin.process.computer.VertexProgram;
import org.apache.tinkerpop.gremlin.process.computer.util.AbstractVertexProgramBuilder;
import org.apache.tinkerpop.gremlin.process.traversal.Operator;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.util.StringFactory;

/**
 * @author C. Savkli, Dec 16, 2017
 * @version 1.0
 */

//
// A simple program that demonstrates use of Bulk-Synchronous-Parallel (BSP) processing to calculate 
// in and out degrees of vertices as well as some trivial global counts
//

public class Example5_VertexProgram_Degree implements VertexProgram<String> 
{
	// Global variables: 
	private static final String TOTAL_DEGREE = "total_degree";
	private static final String VERTEX_COUNT = "vertex_count";
	private static final String AVERAGE_DEGREE = "average_degree";

	// Vertex variables:
    private String OUT_DEGREE = "degree_o";
    private String IN_DEGREE  = "degree_i";
    
    
    private String TOTAL_ITERATIONS = "totalIterations";

    public MessageScope.Local<String> OUTGOING_EDGES = MessageScope.Local.of(__::outE);
    public MessageScope.Local<String> INCOMING_EDGES = MessageScope.Local.of(__::inE);
    
    private int totalIterations = 1;
    private Set<VertexComputeKey> vertexComputeKeys;
    @SuppressWarnings("rawtypes")
    private Set<MemoryComputeKey> memoryComputeKeys;

    public Example5_VertexProgram_Degree() {}

    @Override
    public void loadState(final Graph graph, final Configuration configuration) 
    {
    	// Parameters of the program:
    	
        this.totalIterations = configuration.getInt(TOTAL_ITERATIONS, totalIterations);
        
        // Vertex properties computed by this program:
        
        this.vertexComputeKeys = new HashSet<>(Arrays.asList
        		(
        				VertexComputeKey.of(OUT_DEGREE, false), 
        				VertexComputeKey.of(IN_DEGREE, false))
        		);
        
        // Global graph properties computed by this program:
        
        this.memoryComputeKeys = new HashSet<>(Arrays.asList(
                MemoryComputeKey.of(TOTAL_DEGREE, Operator.sum, true, true),
                MemoryComputeKey.of(VERTEX_COUNT, Operator.sum, true, true),
        		MemoryComputeKey.of(AVERAGE_DEGREE, Operator.sum, true, false))
        		);
    }

    @Override
    public void storeState(final Configuration configuration) 
    {
        VertexProgram.super.storeState(configuration);
        configuration.setProperty(TOTAL_ITERATIONS, this.totalIterations);
    }

    @Override
    public GraphComputer.ResultGraph getPreferredResultGraph() 
    {
        return GraphComputer.ResultGraph.ORIGINAL;
    }

    @Override
    public GraphComputer.Persist getPreferredPersist() 
    {
        return GraphComputer.Persist.VERTEX_PROPERTIES;
    }

    @Override
    public Set<VertexComputeKey> getVertexComputeKeys() 
    {
        return this.vertexComputeKeys;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Set<MemoryComputeKey> getMemoryComputeKeys() 
    {
      return this.memoryComputeKeys;
    }
    
    @Override
    public Set<MessageScope> getMessageScopes(final Memory memory) 
    {
        final Set<MessageScope> set = new HashSet<>();
        
        set.add(this.OUTGOING_EDGES);
        set.add(this.INCOMING_EDGES);

        return set;
    }

    @Override
    public Example5_VertexProgram_Degree clone() {
        try {
            final Example5_VertexProgram_Degree clone = (Example5_VertexProgram_Degree) super.clone();
            return clone;
        } catch (final CloneNotSupportedException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    // This method is called before first execute method. 
    // Any initialization of global parameters can be done here 
    
    @Override
    public void setup(final Memory memory) 
    {
        memory.set(TOTAL_DEGREE, 0.0);
        memory.set(VERTEX_COUNT, 0.0);
        memory.set(AVERAGE_DEGREE, 0.0);
    }

    @Override
    public void execute(final Vertex vertex, Messenger<String> messenger, final Memory memory) 
    {		
    	if (memory.isInitialIteration()) 
    	{    
    		// Every vertex sends a message along incoming and outgoing edges:
    		
    	    messenger.sendMessage(OUTGOING_EDGES, "in_"+vertex.value("name"));    		
    	    messenger.sendMessage(INCOMING_EDGES, "out_"+vertex.value("name"));		
        } 
    	else if (1 == memory.getIteration()) 
    	{  
    		int in_degree = 0;
    		int out_degree = 0;
    		
    		// Messages are received and counted:
    		
    		Iterator<String> messages = messenger.receiveMessages();
    		
            while(messages.hasNext())
            {
            	String msg = messages.next();
            	
            	if(msg.startsWith("in")) in_degree++;
            	if(msg.startsWith("out")) out_degree++;
            }
            
            // Degrees are set:
            
            vertex.property(OUT_DEGREE, out_degree);
            vertex.property(IN_DEGREE, in_degree);        
            
            // Update global properties:
            
            memory.add(VERTEX_COUNT, 1);
            memory.add(TOTAL_DEGREE, in_degree+out_degree);
        } 
    }

    // This method is executed after each iteration. Any post processing, resetting, or saving of global parameters can be done here.
    
    @Override
    public boolean terminate(final Memory memory) 
    {
    	boolean finished = memory.getIteration() >= this.totalIterations;
    	
    	if(finished) 
    	{
    		double total_degree = memory.get(TOTAL_DEGREE);
    		double vertex_count= memory.get(VERTEX_COUNT);
    		double averageDegree = total_degree/vertex_count;
    		memory.set(AVERAGE_DEGREE, averageDegree);
    	}
        return finished;
    }

    @Override
    public String toString() 
    {
        return StringFactory.vertexProgramString(this, "iterations=" + this.totalIterations);
    }

    public Builder build() 
    {
        return new Builder();
    }

    public final class Builder extends AbstractVertexProgramBuilder<Builder> {

        private Builder() {
            super(Example5_VertexProgram_Degree.class);
        }

        // Parameters can be passed on to vertex programs defining additional builder methods like this:
        
        public Builder iterations(final int iterations) 
        {
            this.configuration.setProperty(TOTAL_ITERATIONS, iterations);
            return this;
        }
    }

    @Override
    public Features getFeatures() {
        return new Features() {
            @Override
            public boolean requiresLocalMessageScopes() {
                return true;
            }

            @Override
            public boolean requiresVertexPropertyAddition() {
                return true;
            }
        };
    }
   
}