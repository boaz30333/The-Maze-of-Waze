package dataStructure;

import java.util.Comparator;

import utils.Point3D;

public class Fruit {
public Point3D pos;
public double value;
public int type;
public boolean staffed;
public edge_data on_edge;
public Fruit(Point3D pos, double value,int type) {
	this.pos=pos;
	this.type=type;
	this.value=value;
}

}
