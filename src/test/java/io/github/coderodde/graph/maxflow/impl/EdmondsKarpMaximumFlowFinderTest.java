package io.github.coderodde.graph.maxflow.impl;

import io.github.coderodde.graph.maxflow.CapacityFunction;
import io.github.coderodde.graph.maxflow.DirectedGraph;
import io.github.coderodde.graph.maxflow.FlowFunction;
import io.github.coderodde.graph.maxflow.MaximumFlowData;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class EdmondsKarpMaximumFlowFinderTest {

    @Test
    public void smallFlow() {
        final Integer s = 0;
        final Integer a = 1;
        final Integer b = 2;
        final Integer c = 3;
        final Integer t = 4;
        
        DirectedGraph digraph = new DirectedGraph();
        
        digraph.addNode(s);
        digraph.addNode(a);
        digraph.addNode(b);
        digraph.addNode(c);
        digraph.addNode(t);
        
        digraph.addArc(s, a);
        digraph.addArc(s, b);
        digraph.addArc(a, b);
        digraph.addArc(a, c);
        digraph.addArc(a, t);
        digraph.addArc(b, c);
        digraph.addArc(b, t);
        digraph.addArc(c, t);
        
        CapacityFunction cf = new CapacityFunction(digraph);
        
        cf.setArcCapacity(s, a, 4L);
        cf.setArcCapacity(s, b, 3L);
        cf.setArcCapacity(a, b, 1L);
        cf.setArcCapacity(a, c, 2L);
        cf.setArcCapacity(a, t, 1L);
        cf.setArcCapacity(b, c, 2L);
        cf.setArcCapacity(b, t, 2L);
        cf.setArcCapacity(c, t, 4L);
        
        MaximumFlowData data = 
            new EdmondsKarpMaximumFlowFinder()
                .findMaximumFlowOf(digraph, s, t, cf);
        
        assertEquals(7L, data.getMaximumFlow());
    }
    
    @Test
    public void sevenNodeFlow() {
        final Integer s = 0;
        final Integer a = 1;
        final Integer b = 2;
        final Integer c = 3;
        final Integer d = 4;
        final Integer e = 5;
        final Integer t = 6;

        DirectedGraph digraph = new DirectedGraph();

        digraph.addNode(s);
        digraph.addNode(a);
        digraph.addNode(b);
        digraph.addNode(c);
        digraph.addNode(d);
        digraph.addNode(e);
        digraph.addNode(t);

        digraph.addArc(s, a);
        digraph.addArc(s, b);
        digraph.addArc(a, c);
        digraph.addArc(a, d);
        digraph.addArc(b, c);
        digraph.addArc(b, e);
        digraph.addArc(c, d);
        digraph.addArc(c, t);
        digraph.addArc(d, t);
        digraph.addArc(e, t);

        CapacityFunction cf = new CapacityFunction(digraph);

        cf.setArcCapacity(s, a, 5L);
        cf.setArcCapacity(s, b, 3L);
        cf.setArcCapacity(a, c, 3L);
        cf.setArcCapacity(a, d, 2L);
        cf.setArcCapacity(b, c, 2L);
        cf.setArcCapacity(b, e, 2L);
        cf.setArcCapacity(c, d, 1L);
        cf.setArcCapacity(c, t, 4L);
        cf.setArcCapacity(d, t, 2L);
        cf.setArcCapacity(e, t, 2L);

        MaximumFlowData data =
            new EdmondsKarpMaximumFlowFinder()
                .findMaximumFlowOf(digraph, s, t, cf);

        assertEquals(8L, data.getMaximumFlow());
    }
}
