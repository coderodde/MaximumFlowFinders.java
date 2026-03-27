package io.github.coderodde.graph.maxflow.impl;

import io.github.coderodde.graph.maxflow.CapacityFunction;
import io.github.coderodde.graph.maxflow.DirectedGraph;
import io.github.coderodde.graph.maxflow.FlowFunction;
import io.github.coderodde.graph.maxflow.MaximumFlowData;
import io.github.coderodde.graph.maxflow.MaximumFlowFinder;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * This class implements
 * <a href="https://en.wikipedia.org/wiki/Edmonds%E2%80%93Karp_algorithm">
 * Edmonds-Karp algorithm
 * </a> for finding maximum flow in a flow network.
 */
public final class EdmondsKarpMaximumFlowFinder implements MaximumFlowFinder {

    /**
     * {@inheritDoc } 
     */
    @Override
    public MaximumFlowData findMaximumFlowOf(DirectedGraph digraph, 
                                            Integer source,
                                            Integer sink,
                                            CapacityFunction capacityFunction) {
        
        Objects.requireNonNull(digraph, "The input DirectedGraph is null.");
        Objects.requireNonNull(source, "The source node is null.");
        Objects.requireNonNull(sink, "The sink node is null.");
        Objects.requireNonNull(capacityFunction,
                               "The input CapacityFunction is null.");
        
        if (source.equals(sink)) {
            throw new IllegalArgumentException(
                    "Both the source and sink nodes are the same.");
        }
        
        long maximumFlow = 0L;
        CapacityFunction c = capacityFunction; // Rename.
        FlowFunction f = new FlowFunction(digraph);
        List<Integer> path;
        
        while ((path = findAugmentingPath(digraph, 
                                          source,
                                          sink,
                                          f, 
                                          c)) != null) {
            
            maximumFlow += findMinimumArcsRemove(digraph,
                                                 path, 
                                                 f, 
                                                 c);
        }
        
        return createFlowData(maximumFlow, f);
    }
    
    private List<Integer> findAugmentingPath(DirectedGraph digraph,
                                             Integer source,
                                             Integer sink,
                                             FlowFunction f,
                                             CapacityFunction c) {
        Deque<Integer> queue = new ArrayDeque<>();
        Map<Integer, Integer> parents = new HashMap<>();
        
        queue.add(source);
        parents.put(source, null);
        
        while (!queue.isEmpty()) {
            Integer current = queue.removeFirst();
            
            if (current.equals(sink)) {
                return tracebackPath(current, parents);
            }
            
            for (Integer neighbor : digraph.getAllNeighboursOf(current)) {
                if (parents.containsKey(neighbor)) {
                    continue;
                }
                
                if (residualArcWeight(digraph, 
                                      current, 
                                      neighbor, 
                                      f, 
                                      c) > 0L) {
                    parents.put(neighbor, current);
                    queue.addLast(neighbor);
                }
            }
        }
        
        return null;
    }
    
    private static long residualArcWeight(DirectedGraph graph,
                                          Integer current, 
                                          Integer child,
                                          FlowFunction f,
                                          CapacityFunction c) {
        if (graph.getChildrenOf(current).contains(child)) {
            return c.getArcCapacity(current, child) -
                   f.getArcFlow(current, child);
        } else {
            return f.getArcFlow(child, current);
        }
    }

    private static List<Integer> tracebackPath(Integer sink, 
                                               Map<Integer, Integer> parents) {
        List<Integer> path = new ArrayList<>();
        Integer current = sink;
        
        while (current != null) {
            path.addLast(current);
            current = parents.get(current);
        }
        
        Collections.reverse(path);
        return path;
    }
    
    private static long findMinimumArcsRemove(
            DirectedGraph digraph,
            List<Integer> path, 
            FlowFunction f, 
            CapacityFunction c) {
        
        long minimumFlowValue = Long.MAX_VALUE;
        
        for (int i = 0; i < path.size() - 1; ++i) {
            Integer u = path.get(i);
            Integer v = path.get(i + 1);
            
            long w = residualArcWeight(digraph, 
                                       u, 
                                       v,
                                       f, 
                                       c);
            
            minimumFlowValue = Math.min(minimumFlowValue, w);
        }
        
        for (int i = 0; i < path.size() - 1; ++i) {
            Integer current = path.get(i);
            Integer child   = path.get(i + 1);
            long flow = f.getArcFlow(current, child);
            
            f.setArcFlowValue(current,
                              child,
                              flow + minimumFlowValue);
        }
        
        return minimumFlowValue;
    }
}  
