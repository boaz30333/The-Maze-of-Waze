package gameClient;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.io.FileNotFoundException;
import java.sql.Savepoint;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

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

	int id =0;
	static int id_maker=0;
	protected game_service game;
	int Robot_to_move=-1;
	protected graph grph;
	HashMap<Point3D,Fruit> fruits= new HashMap<>(); 
	HashMap<Integer,Robot> robots= new HashMap<>();
	int num_of_robots;	
	Graph_Algo algo;
	private KML_Logger k;
	private int countForKml;

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

		List<String> log = game.getRobots();
		if(log!=null) {
			long time_to_move= (long) Double.MAX_VALUE;
			for(int i=0;i<log.size();i++) {
				String robot_json = log.get(i);
				try {
					JSONObject line = new JSONObject(robot_json);
					JSONObject ttt = line.getJSONObject("Robot");
					int rid = ttt.getInt("id");
					int dest = ttt.getInt("dest");
					int src = ttt.getInt("src");
					Point3D pos= new Point3D(ttt.getString("pos"));
					if(countForKml==0) { // save position of robot once for every five move call
						k.saveRobot(pos, rid);
						}
						else if(countForKml++==5){
							countForKml=0;
							
						}
					if(dest==-1) {
						Collection<Fruit> f= fruits.values();
						Iterator<Fruit> r_iter = f.iterator();
						Fruit a = null;
						 double time=Double.MAX_VALUE;
						 Fruit nearest=null;
						 double time2=Double.MAX_VALUE;
						while(r_iter.hasNext()) {      
							a=r_iter.next();
							if(a.on_edge.getSrc()==-1) continue;
							time=algo.shortestPathDist(src, a.on_edge.getSrc());
							if(time<time2) {
								time2=time;
								nearest=a;
							}  
						}
//						System.out.println(time);
						List<node_data> routh ;
						
						if(time2==0||time==Integer.MIN_VALUE) {
							dest=nearest.on_edge.getDest();
//							double length_edge= this.grph.getNode(nearest.on_edge.getSrc()).getLocation().distance2D(this.grph.getNode(nearest.on_edge.getDest()).getLocation());
//							double time_to_fruit= (this.grph.getNode(nearest.on_edge.getSrc()).getLocation().distance2D(nearest.pos)/length_edge)*nearest.on_edge.getWeight()*1000;
//							if(time_to_fruit<time_to_move)
//								time_to_move=(long) time_to_fruit;
						}
						else {
							routh = algo.shortestPath(src, nearest.on_edge.getSrc());
							dest= routh.get(1).getKey();
//							double time_to_edge=Double.MAX_VALUE;
//
//							Collection<edge_data> edges = grph.getE(src);
//									for (edge_data e : edges)
//									{	
//									if(e.getSrc()==src&&e.getDest()==dest) {
//										if(e.getWeight()<time_to_move)
//											time_to_move=(long) e.getWeight()*1000;
//									}
//									}
						}
						game.chooseNextEdge(rid,dest);
//							System.out.println("robot :"+rid+" move from:"+src+" to :"+dest);
						
						Iterator<Fruit> r_iter1 = f.iterator();
						 a = null;
						while(r_iter1.hasNext()) {       // check if there is another fruit on this way
							a=r_iter1.next();
							if(a.on_edge.getSrc()==src&&a.on_edge.getDest()==dest) {
								a.on_edge=new edge(-1, -1, 0);//instead to delete
							}
						}
//						System.out.println("robot :"+rid+"decrease finish :"+this.robots.get(rid).src+" -  :"+this.robots.get(rid).dest);
						if(this.robots.get(rid).src!=-1)
//					this.robots.get(rid).finish_time=this.robots.get(rid).finish_time-this.grph.getEdge(this.robots.get(rid).src, this.robots.get(rid).dest).getWeight();
						this.robots.get(rid).src=src;
						this.robots.get(rid).dest=dest;
					}
				} 
				catch (JSONException e) {e.printStackTrace();}
			}
			if(game.timeToEnd()>1000)
				log=game.move();

			try {
//				if(time_to_move<(long)Double.MAX_VALUE)
//				Thread.sleep(time_to_move);//0=110
					Thread.sleep(125);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
		}
	}


	/**
	 * build graph algo to do do algorithm on the graph of this level
	 * and the fruits of the starting state and locate the robots near to them
	 */
	void init(){
		this.algo = new Graph_Algo(this.grph);
		initFruits();
		try {
			k=new KML_Logger();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("can't create kml");
		}
		k.createGraph(grph);
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
						if(b.type==-1
								&&e.getDest()<e.getSrc()
								&&Math.abs(grph.getNode(e.getDest()).getLocation().distance2D(grph.getNode(e.getSrc()).getLocation())- 
										(grph.getNode(e.getDest()).getLocation().distance2D(b.pos)+b.pos.distance2D(grph.getNode(e.getSrc()).getLocation())))<0.000001) {
							b.on_edge=e;

						}
						if(b.type==1
								&&e.getDest()>e.getSrc()
								&&Math.abs(grph.getNode(e.getDest()).getLocation().distance2D(grph.getNode(e.getSrc()).getLocation())
										- grph.getNode(e.getDest()).getLocation().distance2D(b.pos)-
										b.pos.distance2D(grph.getNode(e.getSrc()).getLocation()))<0.000001) {
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
		JFrame in1 = new JFrame();
		String level;
		while(true) {
		 level = JOptionPane.showInputDialog(in1,"enter vertex for next robot");
		try {
		game.addRobot(Integer.parseInt(level));
		break;
		}
		catch(Exception e) {
			JOptionPane.showMessageDialog(in1, "please choose vaild vertex");
		}
		}
		a.staffed=true;
		this.robots.put(id_maker, new Robot(id_maker++, Integer.parseInt(level),0,0));
//		this.robots.get(id_maker++).targets.add(this.grph.getNode(a.on_edge.getDest()));
	}

	/**
	 * this thread using for auto play set dest to robots , find new fruits and add a mission for the first free robot
	 */
	Runnable findFruits = new Runnable() {
		@Override
		public void run() { 
			while(true) {
				if(game.timeToEnd()<1000)
					break;
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
					po = new Point3D(rs);//------------------------------------------------------------------------------------------------
					Set<Point3D> pp= fruits.keySet();                       // checking if this fruit already in my data 
					Iterator<Point3D> point= pp.iterator();
					boolean exsit=false;
					while(point.hasNext()) {
						Point3D check= point.next();
						if(check.equals(po)&&fruits.get(check).on_edge.getSrc()!=-1) exsit=true;
					}//------------------------------------------------------------------------------------------------------------------------
					if(!exsit) {                              
						type= ttt.getInt("type");
						value=ttt.getDouble("value");
						Fruit b = new Fruit(po, value, type);
						setEdge(b);

						boolean need_to_take=true;
					
						if(need_to_take) { // for the event that create new fruit on the edge that robot already there
							List<String> log = game.getRobots();
							for(int i=0;i<log.size();i++) {
								String robot_json = log.get(i);
								try {
									JSONObject line1 = new JSONObject(robot_json);
									JSONObject ttt1 = line1.getJSONObject("Robot");
									int dest = ttt1.getInt("dest");
									int src = ttt1.getInt("src");
									if(b.on_edge.getSrc()==src&&b.on_edge.getDest()==dest)
										need_to_take=false;

								}

								catch (JSONException e) {e.printStackTrace();}
				}
						}
						if(need_to_take)	fruits.put(po, b);
					}
				}
				catch (JSONException e) {e.printStackTrace();}}
}}
	};
	Runnable play = new Runnable() {


		@Override
		public void run() { 
			synchronized(game) {
//new Thread(findFruits).start();
				List<String> log=game.move();
				while(log!=null){
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
							value=ttt.getDouble("value");
							po = new Point3D(rs);
							if(countForKml==0) {//this for the kml that draw the game something like every half second. 
	
									k.saveFruit(po, type);
							}

							
							//------------------------------------------------------------------------------------------------
							Set<Point3D> pp= fruits.keySet();                       // checking if this fruit already in my data 
							Iterator<Point3D> point= pp.iterator();
							boolean exsit=false;
							while(point.hasNext()) {
								Point3D check= point.next();
								if(check.equals(po)&&fruits.get(check).on_edge.getSrc()!=-1) exsit=true;
							}//------------------------------------------------------------------------------------------------------------------------
							if(!exsit) {                              
;
								Fruit b = new Fruit(po, value, type);
								setEdge(b);

								boolean need_to_take=true;
							
								if(need_to_take) { // for the event that create new fruit on the edge that robot already there
									List<String> log1 = game.getRobots();
									for(int i=0;i<log1.size();i++) {
										String robot_json = log1.get(i);
										try {
											JSONObject line1 = new JSONObject(robot_json);
											JSONObject ttt1 = line1.getJSONObject("Robot");
											int dest = ttt1.getInt("dest");
											int src = ttt1.getInt("src");

		
											if(b.on_edge.getSrc()==src&&b.on_edge.getDest()==dest)
												need_to_take=false;

										}

										catch (JSONException e) {e.printStackTrace();}
						}
								}
								if(need_to_take)	fruits.put(po, b);
							}
						}
						catch (JSONException e) {e.printStackTrace();}}
					
					
					if(game.timeToEnd()<1000)
						break;
					SetDestRobots();
				}

			}
			String info = game.toString();
			JSONObject line;
			int level=0;
			try {
				line = new JSONObject(info);
				JSONObject ttt = line.getJSONObject("GameServer");
				level = ttt.getInt("game_level");
			}
			catch (JSONException e) {e.printStackTrace();}
			k.save("kmlfor"+level);	
			
		}
	};

}





