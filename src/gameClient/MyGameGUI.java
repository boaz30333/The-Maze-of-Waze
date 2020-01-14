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
import java.awt.Font;

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

import java.util.ArrayList;
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
	game_service game;
	private static int if_change;
	double minx= Integer.MAX_VALUE;
	double miny= Integer.MAX_VALUE;
	double maxy= Integer.MIN_VALUE;
	double maxx= Integer.MIN_VALUE;
	BufferedImage image;
	BufferedImage image2;
	BufferedImage image3;
	boolean man;
	int rest_to_locate;

	BufferedImage bufferedImage;
	Graphics2D b;


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
			int rs = ttt.getInt("robots");
		}
			catch (JSONException e) {e.printStackTrace();}
		paintgraph(b);
		repaint();
	}
	public MyGameGUI()
	{
		this.grph = null;
		init();
	}
	public MyGameGUI( game_service game)
	{
		this.game=game;
		String g = game.getGraph();
		//			System.out.println(g);
		this.grph = new DGraph();
		if(this.grph instanceof DGraph) {
			((DGraph)this.grph).addListener(this);
			try {
				((DGraph)this.grph).init(g);
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}}
		init();
		paintgraph(b);
		//			viewDial.start();

	}
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
		option.add(start);
		option.add(stop);
		option.add(choosemode);
		this.addMouseListener(this);

		if(this.grph!=null) {
			Collection<node_data> b= this.grph.getV()	;
			Iterator<node_data> iter=b.iterator();

			while(iter.hasNext()) {
				node_data c = iter.next();
				Point3D of_c= c.getLocation();
				minx= Math.min(minx, of_c.x());
				miny= Math.min(miny, of_c.y());
				maxx= Math.max(maxx, of_c.x());
				maxy= Math.max(maxy, of_c.y());
			}
			//if(minx<0) minx= Math.abs(minx)+40;
			//if(miny<80)miny= Math.abs(miny)+90;
		}

	}


	@Override
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
				String level = JOptionPane.showInputDialog(in,"enter level 1-24 ");
				int numlevel =Integer.parseInt(level);
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
		            "Favorite Pizza",
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
		case "start game": // if is manual set  listen to mouse and wait for pressin to vetex move any exist robot to this vertex if auto play auto
 // please choose first level and than type
			break;
		case "stop game":

			break;

		}
	}

	private void selectMode(String mode) {
		// TODO Auto-generated method stub
		if(mode=="Manual") {
			this.man=true;
			JFrame in = new JFrame();
			JOptionPane.showMessageDialog(in, "please locate the robots at vertex");
		}
		else { // starting auto game
			this.man=false;
			
		}
		
	}
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

	public void  paint(Graphics d)
	{
		Graphics2D windows = (Graphics2D) d;
		windows.drawImage(bufferedImage, null, 0, 0); 
		Point3D po=null;
		JSONObject line;
		if(game!=null) {
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
							p_x, 
							p_y, 
							null);
				}
				else {
					windows.drawImage(image3,
							p_x, 
							p_y, 
							null);
				}
			}
			if(game.isRunning()) {
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
							p_x, 
							p_y, 
							null);
				}
				Iterator<String> s = game.getFruits().iterator();
				while(f_iter.hasNext()) {
					int type1=0;
					try {
						line = new JSONObject(s.next());
						JSONObject ttt = line.getJSONObject("Fruit");
						String rs = ttt.getString("pos");
						type1= ttt.getInt("type");
						po = new Point3D(rs);
						// the list of fruits should be considered in your solutio
					}
					catch (JSONException e) {e.printStackTrace();}
					// TODO Auto-generated method stub
					int p_x=(int) (( po.x()-minx)*((double)1200/(maxx-minx))+30);
					int p_y=(int) (( po.y()-miny)*((double)600/(maxy-miny))+50);
					if(type1==1) {
						windows.drawImage(image2,
								p_x, 
								p_y, 
								null);
					}
					else {
						windows.drawImage(image3,
								p_x, 
								p_y, 
								null);
					}
				}
			}
		}
	}





	@Override
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

	@Override
	public void mouseClicked(MouseEvent e) {
		if(this.man==false) return;
		System.out.println("mouseClicked");
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if(this.man==false) return;// do man true only after select level
		int x = e.getX();
		int y = e.getY();
		Point3D p = new Point3D(x,y);
		//points.add(p);
		//repaint();
		if(!this.game.isRunning()&& this.rest_to_locate>0) {
			boolean b=checkAndLocateRobot(p);
			if(b) this.rest_to_locate--;	
		}
		
		
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


}





