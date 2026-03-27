package io.github.coderodde.graph.maxflow;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 
 */
public final class FlowFunction {

    public static final long NON_EXISTENT_WEIGHT = -1L;
    
    private final Map<Integer, Map<Integer, Long>> map = new HashMap<>();
        
    public FlowFunction(DirectedGraph digraph) {
        Objects.requireNonNull(digraph, "The input DirectedGraph is null.");
        
        for (Integer from : digraph) {
            map.computeIfAbsent(from, x -> new HashMap<>());
        }
        
        for (Integer from : digraph.getNodes()) {
            for (Integer to : digraph.getChildrenOf(from)) {
                map.get(from).put(to, 0L);
                map.computeIfAbsent(to, x -> new HashMap<>()).put(from, 0L);
            }
        }
    }
    
    public long getArcFlow(Integer from, Integer to) {
        Map<Integer, Long> innerMap = map.get(from);
        
        if (innerMap == null) {
            return 0L;
        }
        
        return innerMap.getOrDefault(to, 0L);
    }
    
    public void setArcFlowValue(Integer from, Integer to, Long flow) {
        map.get(from).put(to, flow);
    }
}