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
    
    public void setArcWeight(int from, int to, long weight) {
        map.computeIfAbsent(from, _ -> new HashMap<>()).put(to, weight);
    }
    
    public long getArcFlowValue(int from, int to) {
        if (!digraph.isConnectedTo(from, to)) {
            return 0L;
        }
        
        return map.get(from).get(to);
    }
}