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
 * 
 */
public final class EdmondsKarpMaximumFlowFinder implements MaximumFlowFinder {

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
        
        long flow = 0L;
        CapacityFunction c = capacityFunction; // Rename.
        FlowFunction f = new FlowFunction(digraph);
        List<Integer> path;
        
        while ((path = findAugmentingPath(digraph, 
                                          source,
                                          sink,
                                          f, 
                                          c)) != null) {
            
            flow += findMinimumArcsRemove(digraph,
                                          path, 
                                          f, 
                                          c);
        }
        
        return createFlowData(flow, f);
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
            
            for (Integer child : digraph.getChildrenOf(current)) {
                if (parents.containsKey(child)) {
                    continue;
                }
                
                if (residualArcWeight(digraph, 
                                      current, 
                                      child, 
                                      f, 
                                      c) > 0L) {
                    parents.put(child, current);
                    queue.addLast(child);
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
            return c.getArcFlowValue(current, child) -
                   f.getArcFlowValue(current, child);
        } else {
            return f.getArcFlowValue(child, current);
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
            long w = residualArcWeight(digraph, i, i, f, c);
            
            minimumFlowValue = Math.min(minimumFlowValue, w);
        }
        
        for (int i = 0; i < path.size() - 1; ++i) {
            Integer current = path.get(i);
            Integer child   = path.get(i + 1);
            long flow = f.getArcFlowValue(current, child);
            
            f.setArcFlowValue(current,
                              child,
                              flow + minimumFlowValue);
        }
        
        return minimumFlowValue;
    }
}
