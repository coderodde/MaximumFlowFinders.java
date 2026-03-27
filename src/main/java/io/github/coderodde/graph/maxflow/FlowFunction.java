package io.github.coderodde.graph.maxflow;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Implements the flow function.
 */
public final class FlowFunction {

    /**
     * The actual data of this flow function.
     */
    private final Map<Integer, Map<Integer, Long>> map = new HashMap<>();
        
    /**
     * Constructs this empty flow function.
     * 
     * @param digraph the owner digraph. 
     */
    public FlowFunction(DirectedGraph digraph) {
        Objects.requireNonNull(digraph, "The input DirectedGraph is null.");
        
        for (Integer from : digraph) {
            map.computeIfAbsent(from, x -> new HashMap<>());
        }
        
        for (Integer from : digraph) {
            for (Integer to : digraph.getChildrenOf(from)) {
                map.get(from).put(to, 0L);
                map.computeIfAbsent(to, _ -> new HashMap<>()).put(from, 0L);
            }
        }
    }
    
    /**
     * Gets the arc flow of the arc {@code (from, to)}.
     * 
     * @param from the tail node of the query arc.
     * @param to   the head node of the query arc.
     * @return the flow across the arc {@code (from, to)}.
     */
    public long getArcFlow(Integer from, Integer to) {
        Map<Integer, Long> innerMap = map.get(from);
        
        if (innerMap == null) {
            return 0L;
        }
        
        return innerMap.getOrDefault(to, 0L);
    }
    
    /**
     * Sets the arc flow.
     * 
     * @param from the tail node of the target arc.
     * @param to   the head node of the target arc.
     * @param flow the flow value to associated with the arc {@code (from, to)}.
     */
    public void setArcFlowValue(Integer from, Integer to, Long flow) {
        map.get(from).put(to, flow);
    }
}