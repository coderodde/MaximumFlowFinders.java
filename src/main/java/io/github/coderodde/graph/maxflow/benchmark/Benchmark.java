package io.github.coderodde.graph.maxflow.benchmark;

import io.github.coderodde.graph.maxflow.CapacityFunction;
import io.github.coderodde.graph.maxflow.DirectedGraph;
import io.github.coderodde.graph.maxflow.MaximumFlowData;
import io.github.coderodde.graph.maxflow.MaximumFlowFinder;
import io.github.coderodde.graph.maxflow.impl.EdmondsKarpMaximumFlowFinder;
import io.github.coderodde.graph.maxflow.impl.PushToFrontMaximumFlowFinder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * This class implements a simple performance comparison of the two maximum flow
 * algorithm.
 */
public final class Benchmark {

    private static final int NODES = 300;
    private static final int ARCS = 60_000;
    private static final long MAXIMUM_FLOW = 20L;
    
    private Benchmark() {
        
    }
    
    /**
     * Runs this performance comparison.
     * 
     * @param args ignored.
     */
    public static void main(String[] args) {
        long seed = System.currentTimeMillis();
        Random random = new Random(seed);
        System.out.printf("Seed = %d\n", seed);
        
        BenchmarkData data = createRandomGraph(random);
        
        MaximumFlowFinder finderEdmondsKarp = 
                new EdmondsKarpMaximumFlowFinder();
        
        MaximumFlowFinder finderPushRelabel = 
                new PushToFrontMaximumFlowFinder();
        
        long ta = System.currentTimeMillis();
        
        MaximumFlowData maximumFlowDataEdmonds =
            finderEdmondsKarp
                .findMaximumFlowOf(data.digraph,
                                   data.source, 
                                   data.sink,
                                   data.capacityFunction);
        
        long tb = System.currentTimeMillis();
        
        System.out.printf(
            "%s in %d milliseconds.%n",
            finderEdmondsKarp.getClass().getSimpleName(), 
            tb - ta);
        
        ta = System.currentTimeMillis();
        
        MaximumFlowData maximumFlowDataPushToFront =
            finderPushRelabel
                .findMaximumFlowOf(data.digraph,
                                   data.source, 
                                   data.sink,
                                   data.capacityFunction);
        
        tb = System.currentTimeMillis();
        
        System.out.printf(
            "%s in %d milliseconds.%n",
            finderPushRelabel.getClass().getSimpleName(), 
            tb - ta);
        
        System.out.printf(
            "Algorithms agree: %b%n", 
            maximumFlowDataEdmonds.getMaximumFlow() == 
            maximumFlowDataPushToFront.getMaximumFlow());
    }
    
    private static BenchmarkData createRandomGraph(Random random) {
        DirectedGraph g = new DirectedGraph();
        List<Integer> v = new ArrayList<>();
        CapacityFunction c = new CapacityFunction(g);
        
        for (int i = 0; i < NODES; ++i) {
            g.addNode(i);
            v.add(i);
        }
        
        int arcs = 0;
        
        List<Integer> v2 = new ArrayList<>(v);
        Collections.shuffle(v);
        Collections.shuffle(v2);
        
        outerLoop:
        for (Integer u : v) {
            for (Integer i : v2) {
                if (u.equals(i) || g.isConnectedTo(u, i)) {
                    continue;
                }
                
                g.addArc(u, i);
                c.setArcCapacity(u, i, randomFlow(random));
                ++arcs;
                
                if (arcs == ARCS) {
                    break outerLoop;
                }
            }
        }
        
        Integer s = choose(v, random);
        Integer t = choose(v, random);
        return new BenchmarkData(g, c, s, t);
    }

    private static Integer choose(List<Integer> v, Random random) {
        return v.get(random.nextInt(v.size()));
    }

    private static Long randomFlow(Random random) {
        return random.nextLong(MAXIMUM_FLOW) + 1L;
    }
    
    private static final class BenchmarkData {
        DirectedGraph digraph;
        CapacityFunction capacityFunction;
        Integer source;
        Integer sink;
        
        BenchmarkData(DirectedGraph digraph,
                      CapacityFunction capacityFunction,
                      Integer source,
                      Integer sink) {
            this.digraph = digraph;
            this.capacityFunction = capacityFunction;
            this.source = source;
            this.sink = sink;
        }
    }
}
