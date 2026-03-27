package io.github.coderodde.graph.maxflow;

/**
 * This class implements a small wrapper for the maximum flow related 
 * information.
 */
public final class MaximumFlowData {

    /**
     * The value of the maximum flow.
     */
    private final long maximumFlow;
    
    /**
     * The result flow function.
     */
    private final FlowFunction flowFunction;
    
    /**
     * Constructs a new maximum flow data object. 
     * 
     * @param maximumFlow  the maximum flow value.
     * @param flowFunction the maximum flow function.
     */
    MaximumFlowData(long maximumFlow, FlowFunction flowFunction) {
        this.maximumFlow = maximumFlow;
        this.flowFunction = flowFunction;
    }
    
    /**
     * Returns the value of the maximum flow.
     * 
     * @return the value of the maximum flow.
     */
    public long getMaximumFlow() {
        return maximumFlow;
    }
    
    /**
     * Returns the maximum flow function.
     * 
     * @return the maximum flow function. 
     */
    public FlowFunction getFlowFunction() {
        return flowFunction;
    }
}
