package io.github.coderodde.graph.maxflow;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
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
    
    public Iterable<Integer> getAllNeighboursOf(Integer node) {
        return new AllNodeIterable(node);
    }
    
    public boolean isConnectedTo(Integer from, Integer to) {
        if (!childMap.containsKey(from)) {
            return false;
        }
        
        return childMap.get(from).contains(to);
    }

    Iterable<Integer> getNodes() {
        return childMap.keySet();
    }
    
    private final class AllNodeIterable implements Iterable<Integer> {

        private final Iterator<Integer> children;
        private final Iterator<Integer> parents;
        
        AllNodeIterable(Integer node) {
            this.children = childMap  .get(node).iterator();
            this.parents  = parentsMap.get(node).iterator();
        }
        
        public Iterator<Integer> iterator() {
            return new Iterator<>() {
                @Override
                public boolean hasNext() {
                    if (children.hasNext()) {
                        return true;
                    }

                    return parents.hasNext();
                }

                @Override
                public Integer next() {
                    if (children.hasNext()) {
                        return children.next();
                    }

                    return parents.next();
                }
            };
        }
    }
}
