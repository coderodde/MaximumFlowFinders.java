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
        FlowFunction f = new FlowFunction(digraph);
        
        Map<Integer, Long>    e = new HashMap<>(); // Excess function.
        Map<Integer, Integer> h = new HashMap<>(); // Height function.
        Map<Integer, Integer> currentNeighbourIndex = new HashMap<>();
        
        Map<Integer, List<Integer>> neighbourMap = 
            buildResidualNeighbourMap(digraph);
        
        initializePreflow(digraph, 
                          source,
                          e,
                          h,
                          currentNeighbourIndex,
                          f,
                          c);
        
        LinkedList list = buildLinkedList(digraph, 
                                          source, 
                                          sink);
        
        LinkedListNode node = list.head;
        
        while (node != null) {
            Integer u = node.node;
            Integer oldHeight = h.get(u);
            
            discharge(u,
                      f,
                      c,
                      e,
                      h,
                      currentNeighbourIndex,
                      neighbourMap);
            
            LinkedListNode next = node.next;
            
            if (h.get(u) > oldHeight) {
                list.moveToFront(node);
                next = list.head.next;
            }
            
            node = next;
        }
        
        long maximumFlowValue = e.getOrDefault(sink, 0L);
        
        return createFlowData(maximumFlowValue, f);
    }

    private static void initializePreflow(
            DirectedGraph digraph,
            Integer source,
            Map<Integer, Long> excess,
            Map<Integer, Integer> height,
            Map<Integer, Integer> currentNeighbourIndex,
            FlowFunction f,
            CapacityFunction c) {
        
        Integer s = source; // Alias.
        
        for (Integer u : digraph) {
            excess.put(u, 0L);
            height.put(u, 0);
            currentNeighbourIndex.put(u, 0);
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
            Integer u,
            FlowFunction f,
            CapacityFunction c,
            Map<Integer, Long> excess,
            Map<Integer, Integer> height,
            Map<Integer, Integer> currentNeighbourIndex,
            Map<Integer, List<Integer>> neighbourMap) {
        
        List<Integer> neighbours = neighbourMap.get(u);
        
        while (excess.get(u) > 0L) {
            Integer index = currentNeighbourIndex.get(u);
            
            if (index >= neighbours.size()) {
                relabel(u,
                        f,
                        height,
                        c,
                        neighbourMap);
                
                currentNeighbourIndex.put(u, 0);
            } else {
                Integer v = neighbours.get(index);
                
                if (isAdmissible(u,
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
                    currentNeighbourIndex.put(u, index + 1);
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
        
        if (delta == 0L) {
            return;
        }
        
        f.setArcFlowValue(u, v, f.getArcFlow(u, v) + delta);
        f.setArcFlowValue(v, u, f.getArcFlow(v, u) - delta);
        
        excess.put(u, excess.get(u) - delta);
        excess.put(v, excess.get(v) + delta);
    }
    
    private static void relabel(Integer u,
                                FlowFunction f,
                                Map<Integer, Integer> height,
                                CapacityFunction c,
                                Map<Integer, List<Integer>> neighbourMap) {
        int minimumHeight = Integer.MAX_VALUE;
        
        for (Integer v : neighbourMap.get(u)) {
            if (getResidualCapacity(u, v, f, c) > 0L) {
                minimumHeight = Math.min(minimumHeight, height.get(v));
            }
        }
        
        if (minimumHeight == Integer.MAX_VALUE) {
            throw new IllegalStateException("Cannot relabel a node " + u);
        }
        
        int oldHeight = height.get(u);
        int newHeight = minimumHeight + 1;
        
        if (newHeight <= oldHeight) {
            throw new IllegalStateException("Should not get here.");
        }
        
        height.put(u, newHeight);
    }
    
    private static boolean isAdmissible(Integer u,
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
    
    private static Map<Integer, List<Integer>> 
        buildResidualNeighbourMap(DirectedGraph digraph) {
        
        Map<Integer, List<Integer>> neighbourMap = new HashMap<>();
        Map<Integer, Set<Integer>> neighbourFilterMap = new HashMap<>();
        
        for (Integer u : digraph) {
            neighbourMap.put(u, new ArrayList<>());
            neighbourFilterMap.put(u, new HashSet<>());
        }
        
        for (Integer u : digraph) {
            for (Integer v : digraph.getChildrenOf(u)) {
                addUnique(neighbourMap.get(u), neighbourFilterMap.get(u), v);
                addUnique(neighbourMap.get(v), neighbourFilterMap.get(v), u);
            }
        }
        
        return neighbourMap;
    }
        
    private static void addUnique(List<Integer> list, 
                                  Set<Integer> filter, 
                                  Integer node) {
        if (!filter.contains(node)) {
            list.add(node);
            filter.add(node);
        }
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
