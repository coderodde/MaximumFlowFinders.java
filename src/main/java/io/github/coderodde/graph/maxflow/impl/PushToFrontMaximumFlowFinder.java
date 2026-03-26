package io.github.coderodde.graph.maxflow.impl;

import io.github.coderodde.graph.maxflow.CapacityFunction;
import io.github.coderodde.graph.maxflow.DirectedGraph;
import io.github.coderodde.graph.maxflow.FlowFunction;
import io.github.coderodde.graph.maxflow.MaximumFlowFinder;

/**
 *
 */
public final class PushToFrontMaximumFlowFinder implements MaximumFlowFinder {

    @Override
    public FlowFunction findMaximumFlowOf(DirectedGraph graph, 
                                          Integer source,
                                          Integer sink,
                                          CapacityFunction capacityFunction) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
