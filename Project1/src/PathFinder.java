import java.util.Stack;


/**
 * This class represents a path finder. There can be many path finders searching for a path concurrently.
 * It uses the depth first search algorithm to search for a path to the goal node.
 * @author Yao Hong Chun
 *
 */
public class PathFinder implements Runnable{
	//Used for depth first search
	private Stack<Node> searchStack = new Stack<Node>(); //This stack is used for depth first
	private Stack<Integer> pathStack = new Stack<Integer>(); //This stack is used for recording path

	//General information about the graph
	Graph graph;
	boolean foundGoal=false;
	long timeTaken = 0;

	//Thread info
	int id;

	//The current task to perform
	public PathFinder(Graph g, int threadID){
		graph=g;
		id=threadID;
	}
	public void run(){
		Node startNode = graph.getStartNode();
		depthFirstSearch(startNode);

	}


	/**
	 * This algorithm searches a graph for the goal from the start by searching depth first.
	 */
	public void depthFirstSearch(Node node){
		long startTime = System.currentTimeMillis();
		searchStack.push(node);
		foundGoal=false;
		try{
			while(!searchStack.isEmpty()&&!graph.isFound()){ //keep searching if the stack is not empty and the goal is not found
				Node n =searchStack.pop(); //Pop the next node
				if(n.nodeNumber!=graph.start) //if the popped node is not the start node
					while(n.hasBeenSearched()){ //Find another node that hasn't been searched
						n =searchStack.pop();
					}
				if(n.nodeNumber!=graph.goal) //if the node isn't the goal node
					n.setSearched(); //Set visited

				if(n.nodeNumber==graph.goal){ //if we find the goal
					setUpPathStack(n);
					foundGoal = true;
					graph.setFound();
					graph.printResults(getResult());
					break; //stop searching
				}

				for(Node neighbour:n.getNeighbours().values()){ //If we get to here, then add all neighbors to the stack but not including visited nodes and start node
					if(!neighbour.hasBeenSearched() && neighbour.nodeNumber!=graph.start){ //Don't add start node and nodes that already has been searched
						neighbour.setCameFrom(n.nodeNumber); //Set where the neighboring node came from
						searchStack.push(neighbour); //put the neighbor node into the stack
					}
				}
			}
		}
		catch(Exception e){};
		timeTaken = System.currentTimeMillis() - startTime;
	}
	public String getResult(){
		String result= "";
		if(foundGoal){
			result="Thread " + id + " has found the goal. Time taken: "+timeTaken+"ms. Path: ";
			while(!pathStack.isEmpty()){
				result=result+pathStack.pop()+" ";
				
			}
		}
		return result;
	}
	public void setUpPathStack(Node goal){
		int next=-10;
		while(next != graph.start){
			pathStack.push(goal.nodeNumber);
			next = goal.cameFrom;
			goal = graph.nodes.get(next);
			if(next == -1){
				break;
			}
		}
		pathStack.push(graph.start);
	}

	public int getPathLength(){
		System.out.println("Time taken : " + timeTaken);
		if(foundGoal){
			return pathStack.size();
		}
		else{
			return -1;
		}
	}
}
