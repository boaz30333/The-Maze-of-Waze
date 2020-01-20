/**
 * 
 */
package gameClient;

/**
 * @author User
 *
 */
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent; 
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List; 
import java.awt.BasicStroke;
import java.awt.Color;


import utils.Point3D;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.json.JSONException;
import org.json.JSONObject;

import Server.Game_Server;
import Server.game_service;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import algorithms.Graph_Algo;
import dataStructure.graph;
import dataStructure.node_data;
import dataStructure.DGraph;
import dataStructure.GraphListener;
import dataStructure.edge_data;

import java.util.Collection;
import java.util.Iterator;

public class MyGameGUI extends JFrame implements ActionListener ,Serializable, GraphListener, MouseListener
{
	/**
	 * 
	 */
	public static void main(String[] a) {
		MyGameGUI bb = new MyGameGUI();} 
	private static final long serialVersionUID = 1L;
	protected graph grph;
	protected game_service game;
	double minx= Integer.MAX_VALUE;
	double miny= Integer.MAX_VALUE;
	double maxy= Integer.MIN_VALUE;
	double maxx= Integer.MIN_VALUE;
	BufferedImage image;
	BufferedImage image2;
	BufferedImage image3;
	boolean man;
	int rest_to_locate;
	int Robot_to_move=-1;
	private boolean is_choosen_robot=false;
	private int move_to;
	BufferedImage bufferedImage;// for graph paint
	Graphics2D b;
	private long time;
	private boolean is_choosen_level;
	AutoGame auto; 


	/**
	 * this constructor build the first gui windows
	 */
	public MyGameGUI()
	{
		this.grph = null;
		init();
	}
/**
 * this method create the basic windows of this gui graphic
 */
	public void init()
	{
		bufferedImage = new BufferedImage(2000, 1000, BufferedImage.TYPE_INT_ARGB);
		b = bufferedImage.createGraphics();
		b.setBackground(Color.white);
		b.clearRect(0, 0, 2000,1000);
		try {
			image = ImageIO.read(new File("data//car.png"));
			image2 = ImageIO.read(new File("data//cons.png"));
			image3 = ImageIO.read(new File("data//22.png"));//

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.setSize(2000, 1000);
		this.setTitle("pakmen");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
		MenuBar menuBar = new MenuBar();
		this.setMenuBar(menuBar);
		Menu menu = new Menu("menu");
		menuBar.add(menu);
		Menu option = new Menu("option");
		menuBar.add(option);

		MenuItem saveing = new MenuItem("save the graph");
		saveing.addActionListener(this);

		MenuItem loading = new MenuItem("load the graph");
		loading.addActionListener(this);

		MenuItem chooseLevel = new MenuItem("choose level");
		chooseLevel.addActionListener(this);

		MenuItem choosemode = new MenuItem("select mode");
		choosemode.addActionListener(this);
		MenuItem start = new MenuItem("start game");
		start.addActionListener(this);
		MenuItem stop = new MenuItem("stop game");
		stop.addActionListener(this);
		menu.add(saveing);
		menu.add(loading);
		option.add(chooseLevel);
		option.add(choosemode);
		option.add(start);
		option.add(stop);
		this.addMouseListener(this);

		if(this.grph!=null) {
			get_scale_of_level(this.grph);

		}

	}
/**
 * 
 * @param grph2 the graph that this windows should paint
 * set the scale of the points
 */
	public  void get_scale_of_level(graph grph2) {
		// TODO Auto-generated method stub
		Collection<node_data> b= grph2.getV()	;
		Iterator<node_data> iter=b.iterator();

		while(iter.hasNext()) {
			node_data c = iter.next();
			Point3D of_c= c.getLocation();
			minx= Math.min(minx, of_c.x());
			miny= Math.min(miny, of_c.y());
			maxx= Math.max(maxx, of_c.x());
			maxy= Math.max(maxy, of_c.y());
		}
	}
	@Override
/**
 * this method listen to the menu  	
 */
public void actionPerformed(ActionEvent Command)
	{
		String str = Command.getActionCommand();		
		switch(str) 
		{
		case "save the graph":
			Graph_Algo gg = new Graph_Algo();
			gg.init(this.grph);
			JFrame parentFrame = new JFrame();
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setDialogTitle("Specify a file to save");   
			int userSelection = fileChooser.showSaveDialog(parentFrame);

			if (userSelection == JFileChooser.APPROVE_OPTION)
			{
				File fileToSave = fileChooser.getSelectedFile();
				String file= fileToSave.getAbsolutePath();
				gg.save(file);		
				System.out.println("Save as file: " + fileToSave.getAbsolutePath());
			}
			break;
		case "load the graph":
			Graph_Algo g_a = new Graph_Algo();
			JFrame parentFrame1 = new JFrame();
			JFileChooser fileChooser1 = new JFileChooser();
			fileChooser1.setDialogTitle("Specify a file to load");   
			int userSelection1 = fileChooser1.showOpenDialog(parentFrame1);
			if (userSelection1 == JFileChooser.APPROVE_OPTION) 
			{
				File fileToLoad = fileChooser1.getSelectedFile();
				String file= fileToLoad.getAbsolutePath();
				g_a.init(file);
				this.grph=g_a.copy();
				paintgraph(b);
				System.out.println("Load from file: " + fileToLoad.getAbsolutePath());
			}
			break;
		case "choose level":
			JFrame in = new JFrame();
			try 
			{
				JFrame in1 = new JFrame();
				String level = JOptionPane.showInputDialog(in,"enter level 0-23 ");
				int numlevel=-1;
				try {
					numlevel =Integer.parseInt(level);
				}
				catch(Exception e) {

				}
				if(numlevel>23|| numlevel<0) {
					JOptionPane.showMessageDialog(in1, "please choose level 0-23! , you entered:+"+numlevel);
					break;
				}
				chooselevel(numlevel);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}// show windows with option to choose level after enter vaild option show the graph and fruit
			break;
		case "select mode": //manual = put the robots in vertex ,  auto= put robots auto
			JFrame frame = new JFrame("Select mode");
			JFrame in1 = new JFrame();
			final String[] modes = { "Automatic", "Manual"};
			String mode = (String) JOptionPane.showInputDialog(frame, 
					"How you wanna play?",
					"Mode",
					JOptionPane.QUESTION_MESSAGE, 
					null, 
					modes, 
					modes[0]);
			if(mode==null) {
				JOptionPane.showMessageDialog(in1, "please choose a mode before starting the game");
			}
			else
				selectMode(mode);

			break;
		case "start game": 
			JFrame in11 = new JFrame();
			if(this.game==null||this.game.timeToEnd()==0) {
				JOptionPane.showMessageDialog(in11, "please choose level first");
				break;
			}
			if(this.rest_to_locate>0) {
				JOptionPane.showMessageDialog(in11, "please locate more "+this.rest_to_locate+"robots");
				break;
			}
			this.game.startGame();// if is manual set  listen to mouse and wait for pressin to vetex move any exist robot to this vertex if auto play auto
			// please choose first level and than type
			time= game.timeToEnd();
			Thread b=  new Thread(paintg);
			b.start();
			b.setPriority(Thread.MAX_PRIORITY);



			break;
		case "stop game":
			this.game.stopGame();
			is_choosen_level=false;
			break;

		}
	}
	/**
	 * 
	 * @param numlevel the level of the game between 0-23 
	 * we need to ask this graph from the server and than called to other func to paint it
	 */
	public void chooselevel(int numlevel) {
		this.game= Game_Server.getServer(numlevel);
		String g = game.getGraph();
		this.grph = new DGraph();
		if(this.grph instanceof DGraph) {
			((DGraph)this.grph).addListener(this);
			try {
				((DGraph)this.grph).init(g);
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}}
		String info = game.toString();
		JSONObject line;
		try {
			line = new JSONObject(info);
			JSONObject ttt = line.getJSONObject("GameServer");
			this.rest_to_locate = ttt.getInt("robots");
		}
		catch (JSONException e) {e.printStackTrace();}
		is_choosen_level=true;
		paintgraph(b);
		repaint();
	}
	/**
	 * 
	 * @param mode auto/mannual 
	 * if you choose mannual you need to place the robot by the mouse 
	 * otherwise the auto system do it for you
	 */
	private void selectMode(String mode) {
		JFrame in11=new JFrame();
		// TODO Auto-generated method stub
		if(!is_choosen_level) {
			JOptionPane.showMessageDialog(in11, "please choose level first");
			return;
		}
		JFrame in = new JFrame();
		if(mode=="Manual") {
			this.man=true;
			JOptionPane.showMessageDialog(in, "please locate "+this.rest_to_locate+" robots at vertex");
		}

		else { // starting auto game
			this.man=false;
			try {
				auto= new AutoGame(this.game,this.grph);
				repaint();
				this.rest_to_locate=0;
			} catch (Exception e) {
				JOptionPane.showMessageDialog(in11, "there is game running please wait or stop");
				e.printStackTrace();
			}
			// need to locate robots
			JOptionPane.showMessageDialog(in, "you can start game");

		}

	}
	/**
	 * 
	 * @param b the graphics we paint on it
	 * this method paint the graph
	 */
	public void  paintgraph(Graphics b) {
		b.clearRect(0, 0, 2000, 1000);
		if(this.grph != null)
		{
			Collection <node_data> node = grph.getV();
			for (node_data node_data : node)
			{
				Point3D p = node_data.getLocation();
				int p_x=(int) (( p.x()-minx)*((double)1200/(maxx-minx))+30);
				int p_y=(int) (( p.y()-miny)*((double)600/(maxy-miny))+50);
				b.setColor(Color.gray);
				b.fillOval(p_x,p_y,9,9);

				b.setColor(Color.RED);
				b.drawString(Integer.toString(node_data.getKey()), p_x-3, p_y-3);


				Collection<edge_data> edges = grph.getE(node_data.getKey());
				if(edges!=null) {
					for (edge_data e : edges)
					{	
						b.setColor(Color.GREEN);
						((Graphics2D) b).setStroke(new BasicStroke(2,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
						Point3D p2 = grph.getNode(e.getDest()).getLocation();
						int p2_x=(int) (( p2.x()-minx)*((double)1200/(maxx-minx))+30);
						int p2_y=(int) (( p2.y()-miny)*((double)600/(maxy-miny))+50);
						b.drawLine(p_x+5, p_y+5, p2_x+5, p2_y+5);
						b.setColor(Color.MAGENTA);
						b.fillOval((int)((p_x*0.2)+(0.8*p2_x))+2, (int)((p_y*0.2)+(0.8*p2_y)), 9, 9);
						String sss = ""+String.valueOf((double)((int)(100*e.getWeight()))/100);
						b.drawString(sss, 1+(int)((p_x*0.2)+(0.8*p2_x)), (int)((p_y*0.2)+(0.8*p2_y))-2);
					}
				}
			}	
		}

	}
	/**
	 * this method get the paint of the graph and paint on it the fruits and the robots of this level
	 */
	public void  paint(Graphics d)
	{
		Graphics2D windows = (Graphics2D) d;
		windows.drawImage(bufferedImage, null, 0, 0); 
		if(game!=null) {
			paintfruits(d);
			paintrobot(d);
		}
	}
	/**
	 * 
	 * paint the robots of this level 
	 */
	public void paintrobot(Graphics d) {
		Graphics2D windows = (Graphics2D) d;
		Point3D po=null;
		JSONObject line;
		Iterator<String> r_iter = game.getRobots().iterator();
		while(r_iter.hasNext()) {
			try {
				line = new JSONObject(r_iter.next());
				JSONObject ttt = line.getJSONObject("Robot");
				String rs = ttt.getString("pos");
				po = new Point3D(rs);
				// the list of fruits should be considered in your solutio
			}
			catch (JSONException e) {e.printStackTrace();}
			// TODO Auto-generated method stub
			int p_x=(int) (( po.x()-minx)*((double)1200/(maxx-minx))+30);
			int p_y=(int) (( po.y()-miny)*((double)600/(maxy-miny))+50);
			windows.drawImage(image,
					p_x-image.getWidth()/2, 
					p_y-image.getHeight()/2, 
					null);
		}
	}

/**
 * paint the clock of this level 
 */
	private void printClock() {
		Graphics2D windows= (Graphics2D) this.b;
		String sss="";
		if(game.isRunning()&&time -game.timeToEnd()>=1000) {
			System.out.println("time"+time/1000+"  to end :"+game.timeToEnd()/1000);
			windows.setColor(Color.MAGENTA);				
			windows.fillRect(90, 90,100, 30);
			windows.setStroke(new BasicStroke(20,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
			windows.setColor(Color.green);
			sss = ""+game.timeToEnd()/1000+ " sec to end";
			time=game.timeToEnd();
		}
		windows.drawString(sss,100,100);

	}
/**
 * 
 * this method paint the fruits of this level
 */
private void paintfruits(Graphics d) {
		// TODO Auto-generated method stub
		Graphics2D windows = (Graphics2D) d;
		Point3D po=null;
		JSONObject line;
		int type=0;
		Iterator<String> f_iter = game.getFruits().iterator();
		while(f_iter.hasNext()) {
			try {
				line = new JSONObject(f_iter.next());
				JSONObject ttt = line.getJSONObject("Fruit");
				String rs = ttt.getString("pos");
				type= ttt.getInt("type");
				po = new Point3D(rs);
				// the list of fruits should be considered in your solutio
			}
			catch (JSONException e) {e.printStackTrace();}
			// TODO Auto-generated method stub
			int p_x=(int) (( po.x()-minx)*((double)1200/(maxx-minx))+30);
			int p_y=(int) (( po.y()-miny)*((double)600/(maxy-miny))+50);
			if(type==1) {
				windows.drawImage(image2,
						p_x-image2.getWidth()/2, 
						p_y-image2.getHeight()/2, 
						null);
			}
			else {
				windows.drawImage(image3,
						p_x-image3.getWidth()/2, 
						p_y-image3.getHeight()/2, 
						null);
			}
		}
	}

	@Override
	/**
	 * this method using only on mannual mode for place robots and move to neighber vertex
	 */
	public void mouseClicked(MouseEvent e) {
		if(this.man==false) return;
		System.out.println("mouseClicked"+ this.rest_to_locate);
		int x = e.getX();
		int y = e.getY();
		Point3D p = new Point3D(x,y);
		//points.add(p);
		//repaint();
		if(!this.game.isRunning()&& this.rest_to_locate>0) {
			boolean b=checkAndLocateRobot(p);
			if(b) this.rest_to_locate--;

		}
		if(!this.game.isRunning()&& this.rest_to_locate==0) {
			JFrame in11 =new JFrame();
			JOptionPane.showMessageDialog(in11, "all robots is placed , you can start the game");
		}
		else if(this.game.isRunning()) {
			if(this.is_choosen_robot==false) {
				this.Robot_to_move=-1;
				this.Robot_to_move=	find_robot_is_choosen(p);
				if(this.Robot_to_move!=-1)
					is_choosen_robot= true;
				return;
			}
			if(is_choosen_robot==true) {
				move_to=-1;
				move_to=find_node_move_to(p);
				if(move_to!=-1) {
					game.chooseNextEdge(this.Robot_to_move, move_to);
					is_choosen_robot=false;
				}
			}
		}


	}
/**
 * 
 * @param p get point of the mouse click 
 * @return the key of the vertex that clicked
 */
	private int find_node_move_to(Point3D p) {
		// TODO Auto-generated method stub
		Collection <node_data> node = grph.getV();
		int x=-1;
		for (node_data node_data : node)
		{
			Point3D p1 = node_data.getLocation();
			int p_x=(int) (( p1.x()-minx)*((double)1200/(maxx-minx))+30);
			int p_y=(int) (( p1.y()-miny)*((double)600/(maxy-miny))+50);
			if (Math.abs(p_x-p.x())<12&& Math.abs(p_y-p.y())<12) {
				x= node_data.getKey();
				//				game.chooseNextEdge(this.Robot_to_move, move_to);
				//				is_choosen_robot=false;
				return x;
			}
		}
		return x;
	}
	/**
	 * 
	 * @param p get point of the mouse click 
	 * @return the id of robots that clicked
	 */
	private int find_robot_is_choosen(Point3D p) {
		// TODO Auto-generated method stub
		Iterator<String> f_iter = game.getRobots().iterator();
		while(f_iter.hasNext()) {
			try {
				JSONObject line = new JSONObject(f_iter.next());
				JSONObject ttt = line.getJSONObject("Robot");
				String rs = ttt.getString("pos");
				//			System.out.println(f_iter);
				Point3D place = new Point3D(rs);
				int p_x=(int) (( place.x()-minx)*((double)1200/(maxx-minx))+30);
				int p_y=(int) (( place.y()-miny)*((double)600/(maxy-miny))+50);
				if(Math.abs(p_x-p.x())<image.getWidth()&&Math.abs(p_y-p.y())<image.getHeight()) {
					return ttt.getInt("id");
				}
				// the list of fruits should be considered in your solutio
			}
			catch (JSONException e1) {e1.printStackTrace();}
		}
		return -1;
	}
	@Override
	public void mousePressed(MouseEvent e) {
		if(this.man==false) return;// do man true only after select level

	}
	/**
	 * 
	 * @param clicked get point of the mouse click 
	 * @return true if sucsses to locate robot. false if not
	 */
private boolean checkAndLocateRobot(Point3D clicked) {
		Collection <node_data> node = grph.getV();
		for (node_data node_data : node)
		{
			Point3D p = node_data.getLocation();
			int p_x=(int) (( p.x()-minx)*((double)1200/(maxx-minx))+30);
			int p_y=(int) (( p.y()-miny)*((double)600/(maxy-miny))+50);
			if (Math.abs(p_x-clicked.x())<12&& Math.abs(p_y-clicked.y())<12) {
				boolean b=	this.game.addRobot(node_data.getKey());
				repaint();
				return true;
			}
		}
		return false;
	}
	@Override
	public void mouseReleased(MouseEvent e) {
		if(this.man==false) return;

	}
	@Override
	public void mouseEntered(MouseEvent e) {
		if(this.man==false) return;

	}
	@Override
	public void mouseExited(MouseEvent e) {
		if(this.man==false) return;
	}
/**
 * this Thread called only in mannual mode and called to move robot on the server
 */
	Runnable moveRobots = new Runnable(){   

		public void run() {  
			String results = null;
			while(game!=null&&game.isRunning()){
				{
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}	
					if(game.isRunning())
						game.move();
					results = game.toString();
				}

			}
			System.out.println("Game Over: "+results);
			is_choosen_level=false;
			game.stopGame();

		}
	};
	/**
	 * this thread called again and again to paint the screen after changes
	 */
	Runnable paintg = new Runnable() {

		@Override
		public void run() { 

			if(man==false)
				new Thread(auto.play).start();
			while(game!=null){
				if(man==true) game.move();
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
					repaint();
				printClock();
				if(!game.isRunning()) break;
			}
			System.out.println("Game Over: "+game.toString());
			is_choosen_level=false;

		}
	};
	@Override
	/**
	 * if the grph change this method called to paint graph again
	 */
	public void graphUpdated() {
		if(b!=null)
			paintgraph(b);
	}
	@Override
	public void graphUpdated(double x, double y) {
		// TODO Auto-generated method stub
		minx= Math.min(minx, x);
		miny= Math.min(miny,y);
		maxx= Math.max(maxx, x);
		maxy= Math.max(maxy, y);
		if(b!=null)
			paintgraph(b);
	}
	public graph getGraph() {
		return this.grph;
	}

}

