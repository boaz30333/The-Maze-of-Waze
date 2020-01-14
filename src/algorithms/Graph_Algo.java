package algorithms;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;

import dataStructure.DGraph;
import dataStructure.HeapMin;
import dataStructure.edge;
import dataStructure.edge_data;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import dataStructure.graph;
import dataStructure.node;
import dataStructure.node_data;
/**
 * This empty class represents the set of graph-theory algorithms
 * which should be implemented as part of Ex2 - Do edit this class.

/**
 * @author boaz and dolev
 *
 */
public class Graph_Algo implements graph_algorithms , Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	graph grph;
	public Graph_Algo(graph _graph) {
		this.grph= new DGraph(_graph);
	}

	public Graph_Algo() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void init(graph g) {
		// TODO Auto-generated method stub
		this.grph= new DGraph(g);
	}

	@Override
	public void init(String file_name) {
        try
        {    
            FileInputStream file = new FileInputStream(file_name); 
            ObjectInputStream in = new ObjectInputStream(file); 
              
            this.grph = (graph)in.readObject(); 
              
            in.close(); 
            file.close(); 
              
            System.out.println("Object has been deserialized"); 
        } 
          
        catch(IOException ex) 
        { 
            System.out.println("IOException is caught"); 
        } 
          
        catch(ClassNotFoundException ex) 
        { 
            System.out.println("ClassNotFoundException is caught"); 
        }
		
	}

	@Override
	public void save(String file_name) {
	       
        String filename = file_name; 
        
        try
        {    
            FileOutputStream file = new FileOutputStream(filename); 
            ObjectOutputStream out = new ObjectOutputStream(file); 
              
            out.writeObject(this.grph); 
              
            out.close(); 
            file.close(); 
              
            System.out.println("Object has been serialized"); 
        }   
        catch(IOException ex) 
        { 
            System.out.println("IOException is caught"); 
        } 
		
	}

	@Override
	public boolean isConnected() {
		clearInfo();                                   //avoid erors
		Collection<node_data> nodes= this.grph.getV();
		for (node_data b : nodes) {
			dijkstra(b.getKey());                           // do dijkstra on all vertex and check that every vertex is reach to all other
			Collection<node_data> nodes_after= this.grph.getV();
			for (node_data c : nodes_after) {
				if(c.getWeight()== Integer.MAX_VALUE) return false;
			}
			clearInfo();
		}
		return true;
	}
	/**
	 * clear the changed info doing by some algo
	 */
private void clearInfo() {
		// TODO Auto-generated method stub
	Collection<node_data> nodes= this.grph.getV();
	for (node_data b : nodes) {
		clearNodeInfo(b);
		Collection<edge_data> nodes_edges= this.grph.getE(b.getKey());
		for (edge_data c : nodes_edges) {
			clearEdgeInfo(c);
		}
		
	}}
/**
 * 
 * @param node a node for clear its info after algo
 */
private void clearNodeInfo(node_data node) {
	// TODO Auto-generated method stub
node.setInfo("");
node.setTag(-1);
node.setWeight(0);
}
/**
 * 
 * @param edge a edge for clear its info after algo except weight
 */
private void clearEdgeInfo(edge_data edge) {
	// TODO Auto-generated method stub
	edge.setInfo("");
	edge.setTag(-1);
}

// TODO do diaxtra from all and chacked in each diaxtra thre isnt infinty node wight
	
	
	@Override
	public double shortestPathDist(int src, int dest) {
		// TODO Auto-generated method stub
		//TODO dixtra and than get wight of dest
		clearInfo();
		dijkstra(src);
		return this.grph.getNode(dest).getWeight();
	}

	@Override
	public List<node_data> shortestPath(int src, int dest) {
		clearInfo();
		List<node_data> a= new ArrayList<node_data>();
		dijkstra(src);
		if(this.grph.getNode(dest).getWeight()==Integer.MAX_VALUE) return null;
		node_data node = this.grph.getNode(dest);
		while(node.getTag()!=node.getKey()) {
			a.add(node);
			node= this.grph.getNode(node.getTag());
		}
		a.add(this.grph.getNode(src));
		Collections.reverse(a);
		return a;
                                                //TODO  if weight of dest not infinty there is path so go to dest and find who is dad until get to src

	}

	@Override
	public List<node_data> TSP(List<Integer> targets) {
		clearInfo();
//	TODO find with who to start and make hash of nodes in target(or copy list) than do diaxtra with first node find the minimum node wight and choose him for next until get all 
		List<node_data> a= new ArrayList<node_data>(); // the nodes in the TSP route


		int key = 0;
		int src=targets.get(0);             // the first node is the first target
		
		a.add(this.grph.getNode(targets.remove(0)));  // adding the first node target
		
		
		while(!targets.isEmpty()) {
			double weight=Integer.MAX_VALUE;
			dijkstra(src);                      // do dijkstra algo from exist node target to find the route to the next target
			Collection<node_data> nodes= this.grph.getV();
			for (node_data b : nodes) {                 // find the closest next target from unvisited targets
				if(b.getWeight()<weight&&targets.contains(b.getKey())) {
					key=b.getKey();
					weight=b.getWeight();
				}
			}
			if(weight==Integer.MAX_VALUE) return null;   // there no route to any next target ~ = the targets node not SCC 
			
			List<node_data> add_path=shortestPath(src, key); // find the path between exsit target to the next target
			add_path.remove(0); // remove the first cause its adding in previous iteration
			a.addAll(add_path); // adding the route to TSP path
			this.clearInfo();  // clear info of algo running from the graph
			src=key;      // the next src is the dest node key of this iteration
			targets.remove(targets.indexOf(src)); //we are passed through 'src' target we can remove it from the list
		}
		
		return a;
	}

	@Override
	public graph copy() {
		// TODO Auto-generated method stub
		graph copy = 	new DGraph(this.grph);	
		return copy;
	}
																																									
																													
																																									

    /**
     * @param start The source vertex on which the algorithm works
     * the function get src verex and doing dijkstra algo . meaning in the end of this function on each vertex v in the graph saved on its "weight" field- the destination  from "start"
     * vertex and on its "tag" field the  previous vertex from it we reach to v
     * if there isnt path between "start" and "v" - the field weight in "v" will be  infinity(max value of integer) and the field tag will be "-1"
     */
    public void dijkstra(int start) {
        if (grph.getNode(start)==null) {
            System.err.printf("Graph doesn't contain start vertex \"%s\"\n", start);
            return;
        }
		HeapMin NodeWeight= new HeapMin(grph.getV().size());
		Collection<node_data> nodes= grph.getV();
		for (node_data b : nodes) {
			b.setWeight(Integer.MAX_VALUE);
			NodeWeight.minHeapInsert(b);
			b.setTag(-1);
		}
		grph.getNode(start).setWeight(0);
		grph.getNode(start).setTag(start);
		grph.getNode(start).setInfo("visited");
		NodeWeight.heapDecreaseKey(grph.getNode(start));
		int key=start;
		node_data next =grph.getNode(start);
		while(next!=null) {
			key= next.getKey();
		Collection<edge_data> edegspernode= grph.getE(key);
		for (edge_data c : edegspernode) { 
		if(	grph.getNode(c.getDest()).getInfo()=="visited") continue;
			if(grph.getNode(c.getDest()).getWeight()>grph.getNode(c.getSrc()).getWeight()+c.getWeight()) {
				grph.getNode(c.getDest()).setWeight(grph.getNode(c.getSrc()).getWeight()+c.getWeight())	;
				NodeWeight.heapDecreaseKey(grph.getNode(c.getDest()));
				grph.getNode(c.getDest()).setTag(c.getSrc());//set fater
			}
			}
		 next.setInfo("visited");
		 next= NodeWeight.heapExtractMin();
		}
    }
}
