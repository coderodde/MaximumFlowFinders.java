package io.github.coderodde.graph.maxflow;

/**
 * 
 */
public final class MaximumFlowData {

    private final long maximumFlow;
    private final FlowFunction flowFunction;
    
    MaximumFlowData(long maximumFlow, FlowFunction flowFunction) {
        this.maximumFlow = maximumFlow;
        this.flowFunction = flowFunction;
    }
    
    public long getMaximumFlow() {
        return maximumFlow;
    }
    
    public FlowFunction getFlowFunction() {
        return flowFunction;
    }
}
