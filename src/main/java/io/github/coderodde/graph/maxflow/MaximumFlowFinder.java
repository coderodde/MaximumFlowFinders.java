package io.github.coderodde.graph.maxflow;


public interface MaximumFlowFinder {

    public MaximumFlowData findMaximumFlowOf(DirectedGraph graph, 
                                             Integer souce,
                                             Integer sink,
                                             CapacityFunction capacityFunction);
    
    public default MaximumFlowData createFlowData(
            long maximumFlow,
            FlowFunction flowFunction) {
        return new MaximumFlowData(maximumFlow, flowFunction);
    }
}
