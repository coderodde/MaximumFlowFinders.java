package io.github.coderodde.graph.maxflow;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * This class implements the arc capacity function.
 */
public final class CapacityFunction {

    /**
     * The digraph this capacity function belongs to.
     */
    private final DirectedGraph digraph;
    
    /**
     * The actual data for the capacity function.
     */
    private final Map<Integer, Map<Integer, Long>> map = new HashMap<>();
    
    /**
     * Constructs a new empty capacity function.
     * 
     * @param digraph the owner digraph.
     */
    public CapacityFunction(DirectedGraph digraph) {
        this.digraph = 
            Objects.requireNonNull(
                digraph, 
                "The input DirectedGraph is null.");
    }
    
    /**
     * Sets the capacity for the arc {@code (from, to)}.
     * 
     * @param from     the tail node of the arc.
     * @param to       the head node of the arc.
     * @param capacity the capacity to set.
     */
    public void setArcCapacity(Integer from, Integer to, Long capacity) {
        map.computeIfAbsent(from, _ -> new HashMap<>()).put(to, capacity);
    }
    
    /**
     * 
     * Accesses the arc capacity.
     * 
     * @param from the tail node of the arc.
     * @param to   the head node of the arc.
     * @return the arc capacity. 
     */
    public Long getArcCapacity(Integer from, Integer to) {
        if (!digraph.isConnectedTo(from, to)) {
            return 0L;
        }
        
        return map.get(from).get(to);
    }
}