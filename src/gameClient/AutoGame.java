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
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

import Server.game_service;
import algorithms.Graph_Algo;
import dataStructure.Fruit;
import dataStructure.Robot;
import dataStructure.edge;
import dataStructure.edge_data;
import dataStructure.graph;
import dataStructure.node_data;
import utils.Point3D;

public class AutoGame {

	public static int id_maker=0;
	protected game_service game;
	int Robot_to_move=-1;
	protected graph grph;
	HashMap<Point3D,Fruit> fruits= new HashMap<>(); 
	HashMap<Integer,Robot> robots= new HashMap<>();
	int num_of_robots;	
	Graph_Algo algo;
	/**
	 * 
	 * @param game2 
	 * @param grph
	 * @throws Exception
	 */
	public AutoGame(game_service game2,graph grph) throws Exception{
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



	public void play() {

		if(this.game.isRunning()) {
			//on new thread
			new Thread(play).start();

		}
	}
	/**
	 * this method sets new dest to robot when its dest turn to -1 
	 * remove the first target from his list
	 */
	private void SetDestRobots() {
		// TODO Auto-generated method stub
		List<String> log = null;
		if(game!=null)
			log = game.move();

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
						this.robots.get(rid).dest=dest;	
						Collection<Fruit> f= fruits.values();
						Iterator<Fruit> r_iter = f.iterator();
						Fruit a = null;
						while(r_iter.hasNext()) {       // check if there is another fruit on this way
							a=r_iter.next();
							if(a.on_edge.getSrc()==this.robots.get(rid).src&&a.on_edge.getDest()==this.robots.get(rid).dest) {
								a.on_edge=new edge(-1, -1, 0);//instead to delete
							}
						}

						this.robots.get(rid).finish_time=this.robots.get(rid).finish_time-this.grph.getEdge(this.robots.get(rid).src, this.robots.get(rid).dest).getWeight();
						this.robots.get(rid).src=dest;
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
		double time = 0;
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
				System.out.println(game.timeToEnd()+" "+robot_id+time);
			}
		}
		if(robot_id!=-1) {
			System.out.println("huh"+robot_id);
			List<node_data> routh= algo.shortestPath(this.robots.get(robot_id).finish_node,  b.on_edge.getSrc());
			routh.add(this.grph.getNode( b.on_edge.getDest()));
			routh.remove(0);
			this.robots.get(robot_id).targets.addAll(routh);
			this.robots.get(robot_id).finish_node=b.on_edge.getDest();
			this.robots.get(robot_id).finish_time=time;

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


			for (node_data node_data : node)
			{
				Point3D p = node_data.getLocation();

				Collection<edge_data> edges = grph.getE(node_data.getKey());
				if(edges!=null) {
					for (edge_data e : edges)
					{	
						//						System.out.println(Math.abs(grph.getNode(e.getDest()).getLocation().distance2D(grph.getNode(e.getSrc()).getLocation())- grph.getNode(e.getDest()).getLocation().distance2D(b.pos)-b.pos.distance2D(grph.getNode(e.getDest()).getLocation())));

						if(b.type==-1
								&&e.getDest()<e.getSrc()
								&&Math.abs(grph.getNode(e.getDest()).getLocation().distance2D(grph.getNode(e.getSrc()).getLocation())- (grph.getNode(e.getDest()).getLocation().distance2D(b.pos)+b.pos.distance2D(grph.getNode(e.getSrc()).getLocation())))<0.001) {
							b.on_edge=e;

						}
						if(b.type==1
								&&e.getDest()>e.getSrc()
								&&Math.abs(grph.getNode(e.getDest()).getLocation().distance2D(grph.getNode(e.getSrc()).getLocation())
										- grph.getNode(e.getDest()).getLocation().distance2D(b.pos)-
										b.pos.distance2D(grph.getNode(e.getSrc()).getLocation()))<0.001) {
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
			a=r_iter.next();            // find the first free one
			if(a.staffed==false)
				break;
		}
		while(r_iter.hasNext()) {
			Fruit b= r_iter.next();     // maybe threre is another that free with big value
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
		this.robots.put(id_maker, new Robot(id_maker, a.on_edge.getSrc(), a.on_edge.getDest(),a.on_edge.getWeight()));
		//		this.robots.get(id_maker).targets.add(this.grph.getNode(a.on_edge.getSrc()));

		this.robots.get(id_maker++).targets.add(this.grph.getNode(a.on_edge.getDest()));
		//		game.chooseNextEdge(id_maker++,a.on_edge.getDest() );
	}

	/**
	 * this thread using for auto play set dest to robots , find new fruits and add a mission for the first free robot
	 */
	Runnable play = new Runnable() {

		@Override
		public void run() { 
			synchronized(game) {
				List<String> log=game.move();
				while(log!=null){
					log=game.move();
					SetDestRobots();
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
							po = new Point3D(rs);
							Set<Point3D> pp= fruits.keySet();
							Iterator<Point3D> point= pp.iterator();
							boolean exsit=false;
							while(point.hasNext()) {
								Point3D check= point.next();
								if(check.equals(po)) exsit=true;
							}
							if(!exsit) {
								type= ttt.getInt("type");
								value=ttt.getDouble("value");
								Fruit b = new Fruit(po, value, type);
								setEdge(b);
								Collection <Fruit> f = fruits.values();
								Iterator<Fruit> r_iter = f.iterator();
								Fruit a = null;
								boolean need_to_take=true;
								while(r_iter.hasNext()) {       // check if there is another fruit on this way
									a=r_iter.next();
									if(a.on_edge.equals(b.on_edge)) {
										need_to_take=false;
										break;
									}
								}
								if(need_to_take)
									fruits.put(po, b);
							}
							// the list of fruits should be considered in your solutio
						}
						catch (JSONException e) {e.printStackTrace();}}
					Fruit b = findMaxFreeFruit();
					if(b.staffed==false) {
						findRobotAndAddMission(b);
						b.staffed=true;
					}

				}

			}}
	};

}