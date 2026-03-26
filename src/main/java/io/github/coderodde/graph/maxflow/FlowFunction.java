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
        
        for (Integer node : digraph.getNodes()) {
            for (Integer child : digraph.getChildrenOf(node)) {
                map.computeIfAbsent(node, _ -> new HashMap<>()).put(child, 0L);
            }
        }
    }
    
    public long getArcFlow(Integer from, Integer to) {
        if (!digraph.isConnectedTo(from, to)) {
            return 0L;
        }
        
        if (map.get(from) == null) {
            System.out.println("yeah");
        }
        
        if (map.get(from).get(to) == null) {
            System.out.println("fuck");
        }
        
        return map.get(from).get(to);
    }
    
    public void setArcFlowValue(Integer from, Integer to, Long flow) {
        map.get(from).put(to, flow);
    }
}