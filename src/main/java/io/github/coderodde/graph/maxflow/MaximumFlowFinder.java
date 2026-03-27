package io.github.coderodde.graph.maxflow;

/**
 * This interface specifies the API for the maximum flow algorithms.
 */
public interface MaximumFlowFinder {

    /**
     * Finds the maximum flow.
     * 
     * @param digraph          the target digraph.
     * @param souce            the source node.
     * @param sink             the sink node.
     * @param capacityFunction the capacity function.
     * @return the maximum flow data.
     */
    public MaximumFlowData findMaximumFlowOf(DirectedGraph digraph, 
                                             Integer souce,
                                             Integer sink,
                                             CapacityFunction capacityFunction);
    
    /**
     * Allows any instance of {@code MaximumFlowFinder} in other packages to 
     * construct the result data.
     * 
     * @param maximumFlow  the maximum flow value.
     * @param flowFunction the maximum flow function.
     * @return the wrapper object for the two above parameters.
     */
    public default MaximumFlowData createFlowData(
            long maximumFlow,
            FlowFunction flowFunction) {
        return new MaximumFlowData(maximumFlow, flowFunction);
    }
}
