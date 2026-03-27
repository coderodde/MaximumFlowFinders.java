package io.github.coderodde.graph.maxflow.impl;

import io.github.coderodde.graph.maxflow.CapacityFunction;
import io.github.coderodde.graph.maxflow.DirectedGraph;
import io.github.coderodde.graph.maxflow.FlowFunction;
import io.github.coderodde.graph.maxflow.MaximumFlowData;
import io.github.coderodde.graph.maxflow.MaximumFlowFinder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 *
 */
public final class PushToFrontMaximumFlowFinder implements MaximumFlowFinder {

    @Override
    public MaximumFlowData findMaximumFlowOf(DirectedGraph digraph, 
                                             Integer source,
                                             Integer sink,
                                             CapacityFunction capacityFunction)
    {
        Objects.requireNonNull(digraph, "The input DirectedGraph is null.");
        Objects.requireNonNull(source, "The source node is null.");
        Objects.requireNonNull(sink, "The sink node is null.");
        Objects.requireNonNull(capacityFunction,
                               "The input CapacityFunction is null.");
        
        if (source.equals(sink)) {
            throw new IllegalArgumentException(
                    "Both the source and sink nodes are the same.");
        }
        
        CapacityFunction c = capacityFunction;
        Map<Integer, Long>    e = new HashMap<>(); // Excess function.
        Map<Integer, Integer> h = new HashMap<>(); // Height function.
        Map<Integer, Integer> currentNeighbourIndex = new HashMap<>();
        FlowFunction f = new FlowFunction(digraph);
        
        initializePreflow(digraph, 
                          source,
                          e,
                          h,
                          f,
                          c);
        
        for (Integer u : digraph) {
            currentNeighbourIndex.put(u, 0);
        }
        
        LinkedList list = buildLinkedList(digraph, 
                                          source, 
                                          sink);
        
        LinkedListNode node = list.head;
        
        while (node != null) {
            Integer u = node.node;
            Integer oldHeight = h.get(u);
            
            discharge(digraph,
                      u,
                      f,
                      c,
                      e,
                      h,
                      currentNeighbourIndex);
            
            LinkedListNode next = node.next;
            
            if (h.get(u) > oldHeight) {
                list.moveToFront(node);
                next = list.head.next;
            }
            
            node = next;
        }
        
        long maximumFlowValue = e.get(sink);
        
        return createFlowData(maximumFlowValue, f);
    }

    private static void initializePreflow(DirectedGraph digraph,
                                          Integer source,
                                          Map<Integer, Long> excess,
                                          Map<Integer, Integer> height, 
                                          FlowFunction f,
                                          CapacityFunction c) {
        Integer s = source; // Alias.
        
        for (Integer u : digraph) {
            excess.put(u, 0L);
            height.put(u, 0);
        }
            
        for (Integer u : digraph) {
            for (Integer v : digraph) {
                f.setArcFlowValue(u, v, 0L);
            }
        }
            
        height.put(s, digraph.size());
        
        for (Integer v : digraph.getChildrenOf(s)) {
            long capacity = c.getArcCapacity(s, v);
            
            if (capacity == 0L) {
                continue;
            }
            
            f.setArcFlowValue(s, v, +capacity);
            f.setArcFlowValue(v, s, -capacity);
            
            excess.put(v, excess.get(v) + capacity);
            excess.put(s, excess.get(s) - capacity);
        }
    }
    
    private static void discharge(
            DirectedGraph digraph,
            Integer u,
            FlowFunction f,
            CapacityFunction c,
            Map<Integer, Long> excess,
            Map<Integer, Integer> height,
            Map<Integer, Integer> currentNeighbourIndex) {
        
        List<Integer> neighbours = getResidualNeighbours(digraph, u);
        
        while (excess.get(u) > 0L) {
            Integer i = currentNeighbourIndex.get(u);
            
            if (i >= neighbours.size()) {
                relabel(digraph,
                        u,
                        f,
                        height,
                        c);
                
                currentNeighbourIndex.put(u, 0);
                neighbours = getResidualNeighbours(digraph, u);
            } else {
                Integer v = neighbours.get(i);
                
                if (isAdmissible(digraph,
                                 u,
                                 v,
                                 f, 
                                 height,
                                 c)) {
                    push(u,
                         v,
                         f,
                         excess,
                         c);
                } else {
                    currentNeighbourIndex.put(u, i + 1);
                }
            }
        }
    }
    
    private static void push(Integer u,
                             Integer v,
                             FlowFunction f,
                             Map<Integer, Long> excess,
                             CapacityFunction c) {
        long residualCapacity = getResidualCapacity(u, v, f, c);
        long delta = Math.min(excess.get(u), residualCapacity);
        
        f.setArcFlowValue(u, v, f.getArcFlow(u, v) + delta);
        f.setArcFlowValue(v, u, f.getArcFlow(v, u) - delta);
        
        excess.put(u, excess.get(u) - delta);
        excess.put(v, excess.get(v) + delta);
    }
    
    private static void relabel(DirectedGraph digraph,
                                Integer u,
                                FlowFunction f,
                                Map<Integer, Integer> height,
                                CapacityFunction c) {
        int minimumHeight = Integer.MAX_VALUE;
        
        for (Integer v : getResidualNeighbours(digraph, u)) {
            if (getResidualCapacity(u, v, f, c) > 0L) {
                minimumHeight = Math.min(minimumHeight, height.get(v));
            }
        }
        
        if (minimumHeight != Integer.MAX_VALUE) {
            height.put(u, minimumHeight + 1);
        }
    }
    
    private static boolean isAdmissible(DirectedGraph digraph,
                                        Integer u,
                                        Integer v,
                                        FlowFunction f,
                                        Map<Integer, Integer> height,
                                        CapacityFunction c) {
        return getResidualCapacity(u, v, f, c) > 0L 
                && height.get(u) == height.get(v) + 1;
    }
    
    private static long getResidualCapacity(Integer u,
                                            Integer v,
                                            FlowFunction f,
                                            CapacityFunction c) {
        return c.getArcCapacity(u, v) - f.getArcFlow(u, v);
    }
    
    private static List<Integer> getResidualNeighbours(DirectedGraph digraph,
                                                       Integer u) {
        List<Integer> neighbours = new ArrayList<>();
        Set<Integer> filter = new HashSet<>();
        
        for (Integer v : digraph.getAllNeighboursOf(u)) {
            if (filter.contains(v)) {
                continue;
            }
            
            neighbours.add(v);
            filter.add(v);
        }
        
        return neighbours;
    }
    
    private static LinkedList buildLinkedList(DirectedGraph digraph,
                                              Integer s,
                                              Integer t) {
        LinkedList list = new LinkedList();
        
        for (Integer u : digraph) {
            if (!u.equals(s) && !u.equals(t)) {
                list.addLinkedListNode(new LinkedListNode(u));
            }
        }
        
        return list;
    }
    
    private static final class LinkedListNode {
        Integer node;
        LinkedListNode next;
        LinkedListNode prev;
        
        LinkedListNode(Integer node) {
            this.node = node;
        }
    }
    
    private static final class LinkedList {
        LinkedListNode head;
        LinkedListNode tail;
        
        void addLinkedListNode(LinkedListNode node) {
            if (head == null) {
                head = node;
                tail = node;
            } else {
                tail.next = node;
                node.prev = tail;
                tail = node;
            }
        }
        
        void moveToFront(LinkedListNode node) {
            if (node == head) {
                return;
            }
            
            LinkedListNode prev = node.prev;
            LinkedListNode next = node.next;
            
            if (prev != null) {
                prev.next = next;
            }
            
            if (next != null) {
                next.prev = prev;
            } else {
                tail = prev;
            }
            
            node.prev = null;
            node.next = head;
            
            if (head != null) {
                head.prev = node;
            }
            
            head = node;
            
            if (tail == null) {
                tail = node;
            }
        }
    }
}
