/**
 * 
 */
package dataStructure;

import java.util.ArrayList;

import utils.Point3D;

/**
 * @author User
 *
 */
public class  Robot {

public int src;
public int id;
public int dest;
public int speed;
public ArrayList<node_data> targets;
public double finish_time;
public int finish_node;
public int finish_speed;

public Robot(int id,int src,int dest,double finish_time) {
	this.id=id;;
	this.src=src;
	this.dest=dest;
	this.finish_time= finish_time;
	this.finish_node=dest;
	this.speed=1;
	this.finish_speed=2;
}
}

