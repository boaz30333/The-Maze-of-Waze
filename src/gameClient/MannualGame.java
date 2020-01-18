package gameClient;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import Server.game_service;
import algorithms.Graph_Algo;
import dataStructure.Fruit;
import dataStructure.Robot;
import dataStructure.edge_data;
import dataStructure.graph;
import dataStructure.node_data;
import utils.Point3D;

public class MannualGame implements modeGame {
	
	public static int id_maker=0;
	protected game_service game;
	int Robot_to_move=-1;
	private int move_to;
	protected graph grph;
	HashMap<Point3D,Fruit> fruits= new HashMap<>(); 
	HashMap<Integer,Robot> robots= new HashMap<>();
	int num_of_robots;	
	Graph_Algo algo;
	
	public MannualGame(game_service game2,graph grph) throws Exception{
		if(this.game==null||this.game.isRunning()==false) {
			this.game=game2;
			this.grph=grph;
			init();
			play();
		}
		else{
			throw new Exception(); 
		}
	}




	@Override
	public int chooseNextNode() {
		return Robot_to_move;
		
	}

	@Override
	public void play() {
while(this.game.isRunning()) {
	//on new thread
		Iterator<String> f_iter = game.getFruits().iterator();
		Point3D po=null;
		JSONObject line;
		int type=0;
		double value=0;
		while(f_iter.hasNext()) {
			try {
				String fruit=f_iter.next().toString();
				line = new JSONObject(fruit);
				JSONObject ttt = line.getJSONObject("Fruit");
				String rs = ttt.getString("pos");
				if(fruits.containsKey(po)) {
					po = new Point3D(rs);
				type= ttt.getInt("type");
				value=ttt.getDouble("value");
				Fruit b = new Fruit(po, value, type);
				setEdge(b);
				this.fruits.put(po, b);
				}
				// the list of fruits should be considered in your solutio
			}
			catch (JSONException e) {e.printStackTrace();}}
		Fruit b = findMaxFreeFruit();
		findRobotAndAddMission(b);
		SetDestRobots();
		
	}
	}




	private void SetDestRobots() {
		// TODO Auto-generated method stub
		List<String> log = game.move();
//		System.out.println(log);
		if(log!=null) {
			for(int i=0;i<log.size();i++) {
				String robot_json = log.get(i);
				try {
					JSONObject line = new JSONObject(robot_json);
					JSONObject ttt = line.getJSONObject("Robot");
					int rid = ttt.getInt("id");
					int dest = ttt.getInt("dest");
					int src = ttt.getInt("src");
					if(dest==-1&&!this.robots.get(rid).targets.isEmpty()) {	
						dest= this.robots.get(rid).targets.remove(0).getKey();
						game.chooseNextEdge(rid, dest);
						this.robots.get(rid).finish_time-=this.grph.getEdge(this.robots.get(rid).src, this.robots.get(rid).dest).getWeight();
						this.robots.get(rid).dest=dest;					
						this.robots.get(rid).src=src;
					}
				} 
				catch (JSONException e) {e.printStackTrace();}
			}
		}
	}




	/**
	 * 
	 * @param b the fruit is need to take 
	 * the func find the robot that will reach this fruit fastest and add to this targets the routh to this fruit
	 * 
	 */
	private void findRobotAndAddMission(Fruit b) {
		// TODO Auto-generated method stub
		double time;
		double min_time=Double.MAX_VALUE;
		double finish_time;
		int robot_id=-1;
		Collection<Robot> r = robots.values();
		Iterator<Robot> r_iter = r.iterator();
		Robot a = null;
		while(r_iter.hasNext()) {       
			a=r_iter.next();
			finish_time= a.finish_time;
			int src= a.finish_node;
			time=algo.shortestPathDist(src, b.on_edge.getDest())+finish_time;
			if(time<min_time) {
				robot_id=a.id;
				min_time=time;
			}
		}
		if(robot_id!=-1) {
		Collection<node_data> routh= algo.shortestPath(this.robots.get(robot_id).finish_node,  b.on_edge.getDest());
		this.robots.get(robot_id).targets.addAll(routh);
		this.robots.get(robot_id).finish_node=b.on_edge.getDest();
		this.robots.get(robot_id).finish_time+=min_time;

		}
		

	}



/**
 * build graph algo to do do algorithm on the graph of this level
 * and the fruits of the starting state and locate the robots near to them
 */
	void init(){
this.algo = new Graph_Algo(this.grph);
		initFruits();
		this.num_of_robots= findnrobots();
		for(int i=0;i<this.num_of_robots;i++)
		locateRobot();
		
	}
	/**
	 * add all the fruits on this level to fruits hashmap
	 */
	private void initFruits() {
		Iterator<String> f_iter = game.getFruits().iterator();
		Point3D po=null;
		JSONObject line;
		int type=0;
		double value=0;
		while(f_iter.hasNext()) {
			try {
				String fruit=f_iter.next().toString();
				line = new JSONObject(fruit);
				JSONObject ttt = line.getJSONObject("Fruit");
				String rs = ttt.getString("pos");
				type= ttt.getInt("type");
				po = new Point3D(rs);
				value=ttt.getDouble("value");
				Fruit b = new Fruit(po, value, type);
				setEdge(b);
				this.fruits.put(po, b);
				// the list of fruits should be considered in your solutio
			}
			catch (JSONException e) {e.printStackTrace();}
		
	}}
/**
 * 
 * @param b find the edge on the graph that the fruit b is locate on is
 */
	private void setEdge(Fruit b) {
		// TODO Auto-generated method stub
		if(this.grph != null)
		{
			Collection <node_data> node = grph.getV();
			System.out.println(grph.getNode(9).getLocation().distance2D(grph.getNode(8).getLocation())+"ggg");
			System.out.println(grph.getNode(8).getLocation().distance2D(grph.getNode(9).getLocation())+"ddd");
			System.out.println( grph.getNode(8).getLocation().distance2D(b.pos)+
					b.pos.distance2D(grph.getNode(9).getLocation())+"ddd");


			for (node_data node_data : node)
			{
				Point3D p = node_data.getLocation();

				Collection<edge_data> edges = grph.getE(node_data.getKey());
				if(edges!=null) {
					for (edge_data e : edges)
					{	
						System.out.println(Math.abs(grph.getNode(e.getDest()).getLocation().distance2D(grph.getNode(e.getSrc()).getLocation())- grph.getNode(e.getDest()).getLocation().distance2D(b.pos)-b.pos.distance2D(grph.getNode(e.getDest()).getLocation())));

						if(b.type==-1
								&&e.getDest()<e.getSrc()
								&&Math.abs(grph.getNode(e.getDest()).getLocation().distance2D(grph.getNode(e.getSrc()).getLocation())- (grph.getNode(e.getDest()).getLocation().distance2D(b.pos)+b.pos.distance2D(grph.getNode(e.getSrc()).getLocation())))<0.1) {
							b.on_edge=e;
							
						}
						if(b.type==1
								&&e.getDest()>e.getSrc()
								&&Math.abs(grph.getNode(e.getDest()).getLocation().distance2D(grph.getNode(e.getSrc()).getLocation())
								- grph.getNode(e.getDest()).getLocation().distance2D(b.pos)-
										b.pos.distance2D(grph.getNode(e.getSrc()).getLocation()))<0.01) {
							b.on_edge=e;
						}
						}
					}
				}
			}	
		}

/**
 *  
 * @return the number of robots in this level
 */
	private int findnrobots() {
		// TODO Auto-generated method stub
		String info = game.toString();
		JSONObject line;
		int num=0;
		try {
			line = new JSONObject(info);
			JSONObject ttt = line.getJSONObject("GameServer");
			num = ttt.getInt("robots");
		}
		catch (JSONException e) {e.printStackTrace();}
		return num;
	}
	private Fruit findMaxFreeFruit() {
		Collection <Fruit> f = fruits.values();
		Iterator<Fruit> r_iter = f.iterator();
		Fruit a = null;
		while(r_iter.hasNext()) {       // find fruit is free (not staffed) with biggest value
			a=r_iter.next();
			if(a.staffed==false)
				break;
		}
		while(r_iter.hasNext()) {
			Fruit b= r_iter.next();
			if(b.value>a.value&&b.staffed==false)
			a=b;
		}
		return a;
	}
/**
 * locate robot in src of biggest free fruit that exist
 * add the robot to robots hashmap
 */
	private void locateRobot() {
		// TODO Auto-generated method stub
Fruit a= findMaxFreeFruit();
		game.addRobot(a.on_edge.getSrc());
		a.staffed=true;
		this.robots.put(id_maker, new Robot(id_maker++, a.on_edge.getSrc(), a.on_edge.getDest(),a.on_edge.getWeight()));
		
	}
	@Override
	public int chooseRobot() {
		// TODO Auto-generated method stub
		return 0;
	}

}
