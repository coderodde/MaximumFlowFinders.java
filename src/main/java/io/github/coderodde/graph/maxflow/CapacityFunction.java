package io.github.coderodde.graph.maxflow;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 
 */
public final class CapacityFunction {

    private final DirectedGraph digraph;
    private final Map<Integer, Map<Integer, Long>> map = new HashMap<>();
    
    public CapacityFunction(DirectedGraph digraph) {
        this.digraph = 
            Objects.requireNonNull(
                digraph, 
                "The input DirectedGraph is null.");
    }
    
    public void setArcCapacity(Integer from, Integer to, Long weight) {
        map.computeIfAbsent(from, _ -> new HashMap<>()).put(to, weight);
    }
    
    public long getArcCapacity(Integer from, Integer to) {
        if (!digraph.isConnectedTo(from, to)) {
            return 0L;
        }
        
        return map.get(from).get(to);
    }
}