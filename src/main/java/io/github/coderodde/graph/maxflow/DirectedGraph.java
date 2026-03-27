package io.github.coderodde.graph.maxflow;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * This class implements directed graphs (<i>digraphs</i> for short).
 */
public final class DirectedGraph implements Iterable<Integer> {

    /**
     * Maps each node to the set of its children.
     */
    private final Map<Integer, Set<Integer>> childMap = new HashMap<>();
    
    /**
     * Maps each node to the set of its parent nodes.
     */
    private final Map<Integer, Set<Integer>> parentsMap = 
            new HashMap<>();
    
    /**
     * Makes sure that {@code nodeId} is included in this digraph. If it already
     * is, it's an no-op.
     * 
     * @param nodeId the node candidate. 
     */
    public void addNode(Integer nodeId) {
        childMap  .computeIfAbsent(nodeId, _ -> new HashSet<>());
        parentsMap.computeIfAbsent(nodeId, _ -> new HashSet<>());
    }
    
    /**
     * Makes sure that the arc {@code (from, to)} is in this digraph. If it
     * already is, it's an no-op.
     * 
     * @param from the tail node of the arc.
     * @param to   the head node of the arc.
     */
    public void addArc(Integer from, Integer to) {
        addNode(from);
        addNode(to);
        childMap.get(from).add(to);
        parentsMap.get(to).add(from);
    }
    
    /**
     * Returns the number of all neighbours of the {@code node}.
     * 
     * @param node the target query node.
     * @return the number of neighbours of {@code node}.
     */
    public int getNumberOfNeighbours(Integer node) {
        return childMap.get(node).size() + parentsMap.get(node).size();
    }
    
    /**
     * Returns the unmodifiable set of all children of {@code node}.
     * 
     * @param node the target query node.
     * @return the set of all children of {@code node}.
     */
    public Set<Integer> getChildrenOf(Integer node) {
        return Collections.unmodifiableSet(childMap.get(node));
    }
    
    /**
     * Returns the unmodifiable set of all parents of {@code node}.
     * 
     * @param node the target query node.
     * @return the set of all parents of {@code node}.
     */
    public Set<Integer> getParentsOf(Integer node) {
        return Collections.unmodifiableSet(parentsMap.get(node));
    }
    
    /**
     * Returns an {@link Iterable} over all neighbours of {@code node}.
     * 
     * @param node the target query node.
     * @return an {@link Iterable} over all neighbours of {@code node}.
     */
    public Iterable<Integer> getAllNeighboursOf(Integer node) {
        return new NeighbourIterable(node);
    }
    
    /**
     * Checks whether there is an arc {@code (from, to)} in the digraph.
     * 
     * @param from the tail node of the arc.
     * @param to   the head node of the arc.
     * @return {@code true} if and only if there is the specified arc in the
     *         digraph.
     */
    public boolean isConnectedTo(Integer from, Integer to) {
        if (!childMap.containsKey(from)) {
            return false;
        }
        
        return childMap.get(from).contains(to);
    }

    /**
     * Returns an {@link Iterator} over all nodes in this digraph.
     * 
     * @return an {@link Iterator} over all nodes in this digraph.
     */
    @Override
    public Iterator<Integer> iterator() {
        return childMap.keySet().iterator();
    }

    /**
     * Returns the number of nodes in this digraph.
     * 
     * @return the number of nodes in this digraph. 
     */
    public int size() {
        return childMap.size();
    }
    
    /**
     * Implements the {@link Iterable} over all neighbours of a target node.
     */
    private final class NeighbourIterable implements Iterable<Integer> {

        private final Iterator<Integer> children;
        private final Iterator<Integer> parents;
        
        NeighbourIterable(Integer node) {
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
