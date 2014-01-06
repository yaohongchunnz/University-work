import java.util.ArrayList;

/**
 * This class finds all possible paths from a start to a list of goal nodes.
 * @author Yao Hong Chun
 *
 */
public class AllPathFinder implements Runnable{
	private Node continueNode;
	ArrayList<Integer> currentPath;
	private Graph graph;

	/**
	 * This is the instantiation for other nodes. It is instantiated for every other node except for the start.
	 * @param bfs The continuation node of the next possible path.
	 * @param g The graph
	 * @param path The path so far
	 */
	public AllPathFinder(Node bfs,Graph g,ArrayList<Integer> path){
		this.continueNode = bfs;
		graph = g;
		currentPath = new ArrayList<Integer>(path);

	}
	/**
	 * This is the instantiation for just the start node.
	 * @param g
	 */
	public AllPathFinder(Graph g){
		graph =g;
		continueNode = g.getStartNode();
		currentPath = new ArrayList<Integer>();
	}

	/**
	 * Once the thread is running, this algorithm searches for all possible paths by searching breadth first.
	 * It will create a new thread for every neighbouring node, but not including the start node, the node that it came from, and the path of nodes that it has already been to.
	 * And that thread, will do the exact same thing. This process repeats until there are no paths left to find. 
	 * This algorithm will find paths that might go right through a goal node to other goal nodes.
	 */
	public void run(){
		currentPath.add(continueNode.nodeNumber); //Add the current node to the path so far
		Node goalFound=null;
		for(Node neighbour:continueNode.getNeighbours().values()){ 
			//Instantiate AllPathFinders for every neighbour but not including the start, the node that it came from, and the path of nodes that it has already been to.
			if(neighbour.isGoalNode() && !currentPath.contains(neighbour.nodeNumber)){//If it is a goal, then set goalFound
				goalFound=neighbour;
			}
			if(neighbour.nodeNumber != graph.start && !currentPath.contains(neighbour.nodeNumber)){ //if the neighbor isn't the start node and the neighbor is not contained within the current path then
				graph.submitTask(new AllPathFinder(neighbour,graph, currentPath)); //Create a new task and submit it for the ExecutorService
			}

		}			
		if(goalFound!=null){
			reportFoundPath(goalFound);
		}
	}

	private void reportFoundPath(Node goalFound) {
		currentPath.add(goalFound.nodeNumber);
		String foundpath ="";
		for(int i = 0;i<currentPath.size();++i){
			foundpath +=currentPath.get(i).toString()+" ";
		}
		foundpath+="Steps: " +currentPath.size();
		graph.printPaths(foundpath);

	}

}