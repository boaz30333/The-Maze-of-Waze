package Test;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Duration;

import static java.time.Duration.ofMillis;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.sun.corba.se.impl.orbutil.graph.NodeData;

import dataStructure.DGraph;
import dataStructure.edge;
import dataStructure.edge_data;
import dataStructure.graph;
import dataStructure.node;
import dataStructure.node_data;
import gui.Gui_Graph;
import utils.Point3D;

class DGraphTest {

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	@Test
	void testDGraph() {
		
			// Simulate task that takes more than 10 ms.
			graph d= new DGraph();
			int i=1;
			for(i=0;i<1000000;i++) {
				d.addNode(new node(i));// TODO
			}
			for(i=0;i<1000;i++)  {
				int j=0;
				for(j=10000;j<20000;j++) {
						d.connect(i, j, 20);
				}
			}
	}

	@Test
	void testGetNode() 
	{

		DGraph g = new DGraph();
		node_data temp = new node(8);
		g.addNode(temp);
		assertEquals(g.getNode(temp.getKey()),temp);			
	}

	@Test
	void testGetEdge()
	{
		DGraph g = new DGraph();
		node_data n1 = new node(10);
		node_data n2 = new node(20);
		node_data n3 = new node(30);
		g.addNode(n1);
		g.addNode(n2);
		g.addNode(n3);
		g.connect(n1.getKey(), n2.getKey(),100);
		g.connect(n3.getKey(), n2.getKey(),200);
		assertEquals(g.getEdge(n1.getKey(), n2.getKey()).getSrc(),n1.getKey());
		assertEquals(g.getEdge(n1.getKey(), n2.getKey()).getDest(),n2.getKey());
	}

	@Test
	void testAddNode() 
	{
		DGraph g = new DGraph();
		node_data temp1 = new node(new Point3D(15,8,2),5);
		node_data temp2 = new node(new Point3D(10,2,18),8);
		node_data temp3 = new node(1,new Point3D(10,2,18));
		node_data temp4 = new node(1,new Point3D(15,8,2));
		g.addNode(temp1);
		g.addNode(temp2);
		g.addNode(temp3);
		g.addNode(temp4);
		assertEquals(g.getNode(temp1.getKey()), temp1);
		assertEquals(g.getNode(temp2.getKey()), temp2);
	}


	@Test
	void testConnect()
	{
		DGraph g = new DGraph();
		Point3D p1 = new Point3D(15,8,2);
		Point3D p2 = new Point3D(10,2,18);
		Point3D p3 = new Point3D(10,12,2);
		node n1 = new node(p1,1);
		node n2 = new node(p2,2);
		node n3 = new node(p3,3);
		g.addNode(n1);
		g.addNode(n2);
		g.addNode(n3);
		g.connect(n1.getKey(), n2.getKey(), 2);
		g.connect(n2.getKey(), n3.getKey(), 3);
		if (g.edgeSize()!=2) 
		{
			fail("eror");
		}
	}

	@Test
	void testGetV() 
	{
		DGraph g = new DGraph();
		boolean ans = (g.getV().size()==g.nodeSize());
		assertTrue(ans);	
	}

	@Test
	void testGetE()
	{
		DGraph g = new DGraph();
		
		Point3D p1 = new Point3D(15,8,2);
		Point3D p2 = new Point3D(10,2,18);
		Point3D p3 = new Point3D(10,12,2);
		
		node n1 = new node(p1,1);
		node n2 = new node(p2,2);
		node n3 = new node(p3,3);
		
		g.addNode(n1);
		g.addNode(n2);
		g.addNode(n3);

		
		g.connect(n1.getKey(), n2.getKey(), 2);
		g.connect(n1.getKey(), n3.getKey(), 3);
		if(g.getE(n1.getKey()).size()!=2)
		{
			fail ("eror");
		}
	}

	@Test
	void testRemoveNode() 
	{
			DGraph g = new DGraph();
			Point3D p1 = new Point3D(15,8,2);
			Point3D p2 = new Point3D(10,2,18);
			Point3D p3 = new Point3D(10,12,2);
			
			node n1 = new node(p1,1);
			node n2 = new node(p2,2);
			node n3 = new node(p3,3);
			
			g.addNode(n1);
			g.addNode(n2);
			g.addNode(n3);

	        g.removeNode(n1.getKey());
	        g.removeNode(n2.getKey());
	        g.removeNode(n3.getKey());
	        
	        
	        assertEquals(null,g.getNode(n1.getKey()));
	        assertEquals(null,g.getNode(n2.getKey()));
	        assertEquals(null,g.getNode(n3.getKey()));
	}

	@Test
	void testRemoveEdge() 
	{
		DGraph g = new DGraph();
		
		Point3D p1 = new Point3D(15,8,2);
		Point3D p2 = new Point3D(10,2,18);
		
		node n1 = new node(p1,1);
		node n2 = new node(p2,2);
		
		g.addNode(n1);
		g.addNode(n2);
		
		g.connect(n1.getKey(),n2.getKey(),22);
		
		g.removeEdge(n1.getKey(),n2.getKey());
        assertEquals(null ,g.getEdge(n1.getKey(),n2.getKey()));
	}

	@Test
	void testNodeSize()
	{
		DGraph g = new DGraph();
		Point3D p1 = new Point3D(15,8,2);
		Point3D p2 = new Point3D(10,2,18);
		Point3D p3 = new Point3D(10,12,2);
		
		node n1 = new node(p1,1);
		node n2 = new node(p2,2);
		node n3 = new node(p3,3);
		
		g.addNode(n1);
		g.addNode(n2);
		g.addNode(n3);
		
		 assertEquals(3,g.nodeSize());
	}

	@Test
	void testEdgeSize() {
	DGraph g = new DGraph();
		
		Point3D p1 = new Point3D(15,8,2);
		Point3D p2 = new Point3D(10,2,18);
		
		node n1 = new node(p1,1);
		node n2 = new node(p2,2);
		
		g.addNode(n1);
		g.addNode(n2);
		
		 g.connect(n1.getKey(),n2.getKey(),222);
	        assertEquals(1, g.edgeSize());
	}

	@Test
	void testGetMC() {
		DGraph g = new DGraph();
		Point3D p1 = new Point3D(15,8,2);
		Point3D p2 = new Point3D(10,2,18);
		Point3D p3 = new Point3D(10,12,2);
		
		node n1 = new node(p1,1);
		node n2 = new node(p2,2);
		node n3 = new node(p3,3);
		
		g.addNode(n1);
		g.addNode(n2);
		g.addNode(n3);
		
		  g.connect(n1.getKey(),n2.getKey(),100);
		  g.connect(n2.getKey(),n3.getKey(),200);
		  g.connect(n3.getKey(),n1.getKey(), 300);
		  assertEquals(6 , g.getMC());
		  
	}

	@Test
	void testToString() throws InterruptedException {
		DGraph g = new DGraph();
		Point3D p1 = new Point3D(-100,-100,2);
		Point3D p2 = new Point3D(-50,100,18);
		Point3D p3 = new Point3D(200,100,2);
		
		node n1 = new node(p1,1);
		node n2 = new node(p2,2);
		node n3 = new node(p3,3);
		
		g.addNode(n1);
		g.addNode(n2);
		g.addNode(n3);
		
		  g.connect(n1.getKey(),n2.getKey(),100);
		  g.connect(n2.getKey(),n3.getKey(),200);
		  g.connect(n3.getKey(),n1.getKey(), 300);
		Gui_Graph a = new Gui_Graph(g);
		Point3D p4 = new Point3D(300,300,2);
		node n4 = new node(p4,1);
		g.addNode(n4);
		  g.connect(n3.getKey(),n4.getKey(), 500);
Thread.sleep(2000);

	}

}