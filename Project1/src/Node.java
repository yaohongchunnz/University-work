import java.util.ArrayList;
import java.util.HashMap;


/**
 * Represents a node in the graph
 * @author Yao Hong Chun
 *
 */
public class Node {

	//Private fields with initial values
	private boolean startNode=false;
	private boolean goalNode=false;
	public int nodeNumber=-1;
	//connected edges and neighboring nodes
	private ArrayList<Edge> edges = new ArrayList<Edge>();
	private HashMap<Integer, Node> otherNodes = new HashMap<Integer, Node>();
	private boolean visited = false;
	public int cameFrom = -1;
	
	Object lock2=new Object();
	public synchronized int getCameFrom() {

		synchronized(lock2){
			return cameFrom;
		}
	}
	public synchronized void setCameFrom(int cameFrom) {
		synchronized(lock2){
			this.cameFrom = cameFrom;
		}
	}
	public Node(int numb){
		nodeNumber = numb;
	}
	
	Object lock=new Object();
	public synchronized boolean hasBeenSearched(){
		synchronized(lock){
			return visited;
		}
	}
	
	public synchronized void setSearched(){
		synchronized(lock){
			visited = true;
		}
	}
	public boolean isStartNode(){
		return startNode;	
	}

	public boolean isGoalNode(){
		return goalNode;
	}

	public void setAsGoal(){
		goalNode = true;
	}

	public void setAsStart(){
		startNode=true;
	}

	public void addEdge(Edge e){
		if(!Edge.edgeAlreadyContainedIn(edges, e)){
			edges.add(e);
		}
	}

	public int getNumber(){
		return nodeNumber;
	}

	public void findNeighbours(){
		for(Edge e: edges){
			if(e.node1.getNumber()!=getNumber()){
				otherNodes.put(e.node1.nodeNumber,e.node1);
			}
			else{
				otherNodes.put(e.node2.nodeNumber,e.node2);
			}
		}
	}
	
	public boolean isConnected(){
		return edges.size()>0;
	}
	
	public int numberOfEdges(){
		return edges.size();
	}
	
	public boolean isConnectedTo(Node n){
		return otherNodes.containsKey(n.nodeNumber);
	}
	
	public ArrayList<Edge> getConnectedEdges(){
		return edges;
	}
	
	public HashMap<Integer, Node> getNeighbours(){
		return otherNodes;
	}
	public void resetNode(){
		cameFrom=-1;
		visited=false;
	}
	public void debugNode(){
		System.out.println("Node " + nodeNumber + " has: " + edges.size() + " neighbours. "+otherNodes.values().size());
	}
}
