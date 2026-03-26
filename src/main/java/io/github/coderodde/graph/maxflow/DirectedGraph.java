package io.github.coderodde.graph.maxflow;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 
 */
public final class DirectedGraph {

    private final Map<Integer, Set<Integer>> childMap = new HashMap<>();
    private final Map<Integer, Set<Integer>> parentsMap = 
            new HashMap<>();
    
    public void addNode(Integer nodeId) {
        childMap  .computeIfAbsent(nodeId, (_) -> new HashSet<>());
        parentsMap.computeIfAbsent(nodeId, (_) -> new HashSet<>());
    }
    
    public void addArc(Integer from, Integer to) {
        addNode(from);
        addNode(to);
        childMap.get(from).add(to);
        parentsMap.get(to).add(from);
    }
    
    public Set<Integer> getChildrenOf(Integer node) {
        return Collections.unmodifiableSet(childMap.get(node));
    }
    
    public Set<Integer> getParentsOf(Integer node) {
        return Collections.unmodifiableSet(parentsMap.get(node));
    }
    
    public boolean isConnectedTo(Integer from, Integer to) {
        if (!childMap.containsKey(from)) {
            return false;
        }
        
        return childMap.get(from).contains(to);
    }
}
