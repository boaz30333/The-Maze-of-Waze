package dataStructure;

import java.util.ArrayList;

public class graph_json {
    public ArrayList<edges_json> Edges;
    public ArrayList<node_json> Nodes;
    
	public String toString() {
		return " Edges:\n" + Edges+"\n nodes"+Nodes;
	}
}
