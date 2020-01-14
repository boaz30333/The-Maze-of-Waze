package dataStructure;

import java.io.Serializable;

public class edge implements edge_data ,Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//Edge parameters:
	private int src, dest, tag;
	private double weight;
	private String info;

	//Constructors:
	public edge(int src, int dest, double w) {
		this.src=src;
		this.dest=dest;
		this.tag=0;
		this.weight=w;
		this.info="";
	}
	
	/**
	 * deep copy constructor
	 * @param o
	 */
	protected edge(edge_data o) {
		this.src=o.getSrc();
		this.dest=o.getDest();
		this.tag=o.getTag();
		this.weight=o.getWeight();
		this.info=o.getInfo();
	}

	//Getters/Setters:
	@Override
	public int getSrc() { return this.src; }

	@Override
	public int getDest() { return this.dest; }

	@Override
	public double getWeight() { return this.weight; }

	@Override
	/**
	 * af
	 */
	public String getInfo() { return this.info; }

	@Override
	public void setInfo(String s) { this.info = s; }

	@Override
	public int getTag() { return this.tag; }

	@Override
	public void setTag(int t) { this.tag = t; }

}