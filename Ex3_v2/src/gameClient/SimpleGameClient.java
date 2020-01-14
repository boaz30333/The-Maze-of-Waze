package gameClient;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.sun.swing.internal.plaf.basic.resources.basic;

import Server.Game_Server;
import Server.game_service;
import dataStructure.DGraph;
import dataStructure.Robot;
import dataStructure.Robots;
import dataStructure.edge_data;
import dataStructure.edges_json;
import dataStructure.graph;
import dataStructure.graph_json;
import dataStructure.node;
import dataStructure.node_json;
import gui.Gui_Graph;
import oop_dataStructure.OOP_DGraph;
import oop_dataStructure.oop_edge_data;
import oop_dataStructure.oop_graph;
import utils.Point3D;
/**
 * This class represents a simple example for using the GameServer API:
 * the main file performs the following tasks:
 * 1. Creates a game_service [0,23] (line 36)
 * 2. Constructs the graph from JSON String (lines 37-39)
 * 3. Gets the scenario JSON String (lines 40-41)
 * 4. Prints the fruits data (lines 49-50)
 * 5. Add a set of robots (line 52-53) // note: in general a list of robots should be added
 * 6. Starts game (line 57)
 * 7. Main loop (should be a thread) (lines 59-60)
 * 8. move the robot along the current edge (line 74)
 * 9. direct to the next edge (if on a node) (line 87-88)
 * 10. prints the game results (after "game over"): (line 63)
 *  
 * @author boaz.benmoshe
 *
 */
public class SimpleGameClient {
	
	public static MyGameGUI b;





	public static void test1() {
		int scenario_num = 1;
		game_service game = Game_Server.getServer(scenario_num); // you have [0,23] games


		b=new MyGameGUI(game);
		String info = game.toString();
		System.out.println(info);
		JSONObject line;

		
		
		try {
			line = new JSONObject(info);
			JSONObject ttt = line.getJSONObject("GameServer");
			int rs = ttt.getInt("robots");
			System.out.println(game.getFruits());
//			System.out.println(g);
			// the list of fruits should be considered in your solution
			Iterator<String> f_iter = game.getFruits().iterator();
			while(f_iter.hasNext()) {
				System.out.println(f_iter.next());
				}
			int src_node = 0;  // arbitrary node, you should start at one of the fruits
			for(int a = 0;a<rs;a++) {
				game.addRobot(src_node+a);
			}
		}
		catch (JSONException e) {e.printStackTrace();}
		game.startGame();
		// should be a Thread!!!
		Thread viewDial = new Thread(){
			public void run() {
				while(game.isRunning()) {
					moveRobots(game,b.grph);
					try {
						sleep(50);
						b.repaint();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}


				String results = game.toString();
				System.out.println("Game Over: "+results);

			}

//					b.repaint();
//					Iterator<String> f_iter = game.getFruits().iterator();
//					while(f_iter.hasNext()) {
//						System.out.println(f_iter.next());
//						}
				
			
		};
		viewDial.start();
//		Thread viewDial = new Thread(){
//		BufferedImage image;
//		BufferedImage image2;
//		public void run() {
//			try {
//				 image = ImageIO.read(new File("src//sufganiya.jpg"));
//			image2 = ImageIO.read(new File("sufganiya.jpg"));
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			while(true){
//				paintItems(b,game);
//				try {
//					sleep(500);
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
//
//		}
////		private void paintItems(MyGameGUI b, game_service game) {
////			
////			// TODO Auto-generated method stub
////			Point3D p=null;
////			JSONObject line;
////				Iterator<String> r_iter = game.getRobots().iterator();
////
////				while(r_iter.hasNext()) {
////					try {
////						line = new JSONObject(r_iter.next());
////						JSONObject ttt = line.getJSONObject("Robot");
////						String rs = ttt.getString("pos");
////						p = new Point3D(rs);
////						// the list of fruits should be considered in your solutio
////					}
////					catch (JSONException e) {e.printStackTrace();}
////					b.drawImage(image,
////							p.x(),
////							p.y(),
////							null);
////					}
////				b.repaint();
//////				Iterator<String> f_iter = game.getFruits().iterator();
//////				while(f_iter.hasNext()) {
//////					System.out.println(f_iter.next());
//////					}
////			
////		}
//	};
//	viewDial.start();

	}
	/** 
	 * Moves each of the robots along the edge, 
	 * in case the robot is on a node the next destination (next edge) is chosen (randomly).
	 * @param game
	 * @param grph
	 * @param log
	 */
	private static void moveRobots(game_service game, graph grph) {
		List<String> log = game.move();
		if(log!=null) {
			long t = game.timeToEnd();
			for(int i=0;i<log.size();i++) {
				String robot_json = log.get(i);
				Gson gson = new Gson();
				Robots rob = gson.fromJson(robot_json,Robots.class);
//				try {
//					JSONObject line = new JSONObject(robot_json);
//					JSONObject ttt = line.getJSONObject("Robot");
//					int rid = ttt.getInt("id");
//					int src = ttt.getInt("src");
//					int dest = ttt.getInt("dest");
				
					if(rob.Robot.dest==-1) {	
						rob.Robot.dest = nextNode(grph, rob.Robot.src);
						game.chooseNextEdge(rob.Robot.id, rob.Robot.dest);
						System.out.println("Turn to node: "+rob.Robot.dest+"  time to end:"+(t/1000));
						System.out.println(robot_json);
						
							// TODO Auto-generated method stub
							//	Gson gson = new Gson();
								//graph_json rob;
								//rob = gson.fromJson(ttt,Robot.class);

					        }
						
						
						
						
						
						
					}
//				catch (JSONException e) {e.printStackTrace();}
//			}
		}
	}
	


	/**
	 * a very simple random walk implementation!
	 * @param g
	 * @param src
	 * @return
	 */
	private static int nextNode(graph g, int src) {
		int ans = -1;
		Collection<edge_data> ee = g.getE(src);
		Iterator<edge_data> itr = ee.iterator();
		int s = ee.size();
		int r = (int)(Math.random()*s);
		int i=0;
		while(i<r) {itr.next();i++;}
		ans = itr.next().getDest();
		return ans;
	}


}
