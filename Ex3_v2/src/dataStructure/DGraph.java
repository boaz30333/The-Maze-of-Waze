package dataStructure;

import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;

import jdk.nashorn.internal.parser.JSONParser;
import utils.Point3D;

/**
 * @author dolev and boaz
 *
 */
/**
 * @author User
 *
 */
public class DGraph implements graph , Serializable
{

	private static final long serialVersionUID = 1L;
	public HashMap<Integer, node_data> nodesMap = new HashMap<Integer, node_data>();
	public HashMap<Integer, HashMap<Integer,edge_data>> edgesMap = new HashMap<Integer, HashMap<Integer,edge_data>>();
	public int edgesCounter=0;
	public int nodeCounter=0;
	public int MC = 0;
	public GraphListener listener;

	/**
	 * default constructor
	 * nodesMap = hashmap the key is the node key  and value is node_data type
	 * edgesmap= hashmap the key is the key of source vertex and the value is hashmap(key is the destination vertex key and value is the edge between src and dest)
	 */
	public DGraph()
	{
		this.nodesMap = new HashMap<Integer, node_data>();
		this.edgesMap = new HashMap<Integer, HashMap<Integer,edge_data>>();
		this.edgesCounter=0;
		this.nodeCounter=0;
		this.MC = 0;
	}
	
	public void addListener(GraphListener listener)
	{
		this.listener = listener;	
	}
	
	public void updateListener()
	{
		if(listener!=null)
			listener.graphUpdated();
	}
	
	/**
	 * @param G
	 * constructor from another graph
	 * DGraph is deep copy to G for vertex and edges
	 */
	public DGraph(graph G)
	{
		Collection<node_data> nodes= G.getV();
		for (node_data b : nodes) {
			node_data copynode= new node(b);
			this.addNode(copynode);
			Collection<edge_data> edegspernode= G.getE(b.getKey());
			this.edgesMap.put(b.getKey(),new HashMap<Integer,edge_data>() );
			if(edegspernode != null)
			{
				for (edge_data c : edegspernode) {////////////////////////////////////
					edge_data copyedge= new edge(c) ;
					this.edgesMap.get(b.getKey()).put(c.getDest(),copyedge);
				}
			}
		}
		this.MC = G.getMC();
		this.edgesCounter=G.edgeSize();

	}

	@Override
	public node_data getNode(int key) 
	{
		if (this.nodesMap.get(key)==null)
			return null; 
		return this.nodesMap.get(key); 
	}

	@Override
	public edge_data getEdge(int src, int dest)
	{
		if (this.edgesMap.get(src).get(dest) != null)
		{
			return  (this.edgesMap.get(src).get(dest)); 
		}
		return null;
	}

	@Override
	public void addNode(node_data n) 
	{

		int key = 	n.getKey();
		if(this.nodesMap.containsKey(n.getKey())) {  // if node with same key dexist plese replace it with th new one
			this.nodesMap.remove(key);
		}
		this.nodesMap.put(key, n);
		this.MC ++;
		nodeCounter++;
		
//		synchronized (this) {
//			this.notifyAll();
//		}
		if(n.getLocation()!=null)
		updateListener(n.getLocation().x(),n.getLocation().y());
	}

	private void updateListener(double x, double y) {
		// TODO Auto-generated method stub
		if(listener!=null)
			listener.graphUpdated(x,y);
	}

	@Override
	public void connect(int src, int dest, double w)
	{
		if (this.nodesMap.get(src)==null || this.nodesMap.get(dest)== null)
		{
			System.out.println("eror - one or more from vertex input to connect non-exist");
		}
		else
		{
			edge_data newedge = new edge(src,dest,w);
			if (this.edgesMap.get(src) == null) 
			{
				this.edgesMap.put(src, new HashMap<Integer,edge_data>());
				this.edgesMap.get(src).put(dest, newedge);
				edgesCounter++;
				this.MC ++;
			}
			else if(this.edgesMap.get(src).containsKey(dest)) {  // if there is a edge between this given vertex we will replace it with the new one 
				this.edgesMap.get(src).replace(dest,this.edgesMap.get(src).get(dest),newedge);
				this.MC++;
			}
			else
			{
				this.edgesMap.get(src).put(dest, newedge);
				edgesCounter++;
				this.MC ++;
			}
//			synchronized (this) {
//				this.notifyAll();
//			}
			updateListener();
		}
	}

	@Override
	public Collection<node_data> getV()
	{
		return this.nodesMap.values();
	}

	@Override
	public Collection<edge_data> getE(int node_id) {
		if (this.edgesMap.isEmpty()) { return null; }
		if (this.edgesMap.get(node_id)==null) { return null; }
		return this.edgesMap.get(node_id).values(); 
	}

	@Override
	public node_data removeNode(int key)
	{
		if (this.nodesMap.containsKey(key))
		{
			int num =0;
			node_data ans = this.nodesMap.remove(key);
			if(this.edgesMap.containsKey(key))
			num = this.edgesMap.get(key).size();
			this.edgesMap.remove(key);
			this.edgesCounter = this.edgesCounter - num; 
			this.nodeCounter--;
			Iterator<HashMap<Integer, edge_data>> d= this.edgesMap.values().iterator();
			while(d.hasNext()) {
				HashMap<Integer, edge_data> gg= d.next();
				if(gg.containsKey(key))
					gg.remove(key);
			}
			
			MC++;
//			synchronized (this) {
//				this.notifyAll();
//			}
			updateListener();
			return ans;
		}
		else
		{
			return null;
		}
	}

	@Override
	public edge_data removeEdge(int src, int dest)
	{
		if (this.edgesMap.get(src).get(dest)==null) 
			return null; 
		edge_data edge = new edge((edge)this.edgesMap.get(src).get(dest));
		this.edgesMap.get(src).remove(dest);
		edgesCounter--;
		this.MC++;
//		synchronized (this) {
//			this.notifyAll();
//		}
		updateListener();
		return edge;
	}

	@Override
	public int nodeSize() {
		return this.nodeCounter;
	}

	@Override
	public int edgeSize() {
		return	edgesCounter;
	}

	@Override
	public int getMC()
	{
		return MC;
	}
//public Point3D getPointFromString(String s) { maybe not needed
//	Point3D p= new  Point3D(s)
//	return p;
//	
//}
	public void init(String g) throws JSONException {
		// TODO Auto-generated method stub
			Gson gson = new Gson();
			graph_json bookstore;
			bookstore = gson.fromJson(g,graph_json.class);
			Iterator<node_json> n = bookstore.Nodes.iterator();
			Iterator<edges_json> e = bookstore.Edges.iterator();
			while(n.hasNext()) {
				node_json nj= n.next();
				this.addNode(new node(nj.id,new Point3D(nj.pos))) ;	
		}
			while(e.hasNext()) {
				edges_json ej= e.next();
				this.connect(ej.src, ej.dest, ej.w); ;	
		}

        }
}
