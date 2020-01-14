package dataStructure;

import java.util.ArrayList;

public class graph_json {
    private ArrayList<edges_json> Edges;
    private ArrayList<node_json> Nodes;
    
	public String toString() {
		return " Edges:\n" + Edges+"\n nodes"+Nodes;
	}
}
