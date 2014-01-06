import java.util.ArrayList;


/**
 * This class represents an edge of a graph. Necessary for parsing the graph.
 * It represents it by having two nodes as its fields.
 * @author Yao Hong Chun
 *
 */
public class Edge {
	Node node1;
	Node node2;

	public Edge(Node n1, Node n2){
		node1 = n1;
		node2 = n2;	
	}
	
	public Node getNode1(){
		return node1;
	}
	public Node getNode2(){
		return node2;
	}
	
	public String toString(){
		return "E "+node1.getNumber() +" " + node2.getNumber();
	}

	
	public boolean isTheSameEdgeAs(Edge edge){
		if(edge.node1.getNumber() == node1.getNumber() && edge.node2.getNumber() == node2.getNumber()){
			return true;
		}
		if(edge.node1.getNumber() == node2.getNumber() && edge.node2.getNumber() == node1.getNumber()){
			return true;
		}
		return false;
	}

	public static boolean edgeAlreadyContainedIn(ArrayList<Edge> edges, Edge e){
		for(Edge edge: edges){
			if(edge.isTheSameEdgeAs(e)){
				return true;
			}
		}
		return false;
	}
	
	
}
