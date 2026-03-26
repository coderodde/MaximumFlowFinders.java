package io.github.coderodde.graph.maxflow;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 
 */
public final class FlowFunction {

    public static final long NON_EXISTENT_WEIGHT = -1L;
    
    private final DirectedGraph digraph;
    private final Map<Integer, Map<Integer, Long>> map = new HashMap<>();
        
    public FlowFunction(DirectedGraph digraph) {
        this.digraph = 
            Objects.requireNonNull(
                digraph,
                "The input DirectedGraph is null.");
    }
    
    public long getArcFlowValue(Integer from, Integer to) {
        if (!digraph.isConnectedTo(from, to)) {
            return 0L;
        }
        
        return map.get(from).get(to);
    }
    
    public void setArcFlowValue(Integer from, Integer to, Long flow) {
        map.get(from).put(to, flow);
    }
}