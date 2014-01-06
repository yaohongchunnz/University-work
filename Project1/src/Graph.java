import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JFileChooser;



/**
 * This class represents a graph. It contains a map of all the nodes and a list of all the edges.
 * @author Yao Hong CHun
 *
 */
public class Graph implements Runnable{
	protected ArrayList<Edge> edges = new ArrayList<Edge>(); //The list of all edges
	protected HashMap<Integer,Node> nodes = new HashMap<Integer,Node>(); //The map of all nodes with its node number mapped to the node object.
	protected ArrayList<Integer> goalNodes = new ArrayList<Integer>(); //The list of goal nodes.
	protected int start; //The start node
	protected int goal; //The FIRST goal node
	private int task; //The task to perform. 0 for search all paths. 1 for search one path.
	private boolean found=false;
	private int threads; //How many threads the system has.
	private ExecutorService pool; //The pool of threads.
	private GUI gui; //The GUI
	private long timeTaken=0; //How much time it has taken

	public Graph (){
		gui = new GUI(this);

		threads = Runtime.getRuntime().availableProcessors();
		gui.println("System has " + threads + " threads available.");
	}

	public Graph(GUI g){
		gui = g;
		threads = Runtime.getRuntime().availableProcessors();
		pool = Executors.newFixedThreadPool(threads);
		gui.println("System has " + threads + " threads available.");
	}


	/**
	 * This method will bring up a JFileChooser for the user to choose a graph.
	 */
	public void chooseGraph(){
		JFileChooser fc = new JFileChooser();
		int returnVal = fc.showOpenDialog(null);
		if(returnVal != JFileChooser.CANCEL_OPTION){
			gui.setDirectory(fc.getSelectedFile().toString());
			parseGraph(fc.getSelectedFile().toString());
		}
	}


	/** 
	 * Parses the selected graph, adding edges and nodes, sets specified nodes to be goal and start
	 * @param dir The file to be parsed
	 */
	public void parseGraph(String dir){
		nodes = new HashMap<Integer, Node>();
		edges = new ArrayList<Edge>();
		goalNodes = new ArrayList<Integer>();
		gui.println("Parsing graph...");
		long initialTime = System.currentTimeMillis();
		try {
			Scanner scan = new Scanner(new File(dir));

			while(scan.hasNext()){
				String type = scan.next();
				if(type.equalsIgnoreCase(("E"))){

					int node1 = Integer.parseInt(scan.next());
					int node2 = Integer.parseInt(scan.next());
					Node n1 = null;
					Node n2 = null;

					if(!nodes.containsKey(node1)){
						n1 = new Node(node1);
						nodes.put(node1,n1);
					}
					else{
						n1 = nodes.get(node1);
					}
					if(!nodes.containsKey(node2)){
						n2 = new Node(node2);
						nodes.put(node2	,n2);
					}
					else{
						n2 = nodes.get(node2);
					}

					Edge edge = new Edge(n1, n2);
					edges.add(edge);
					n1.addEdge(edge);
					n2.addEdge(edge);

				}
				else if(type.equalsIgnoreCase("S")){
					start = scan.nextInt();
				}
				else if(type.equalsIgnoreCase("G")){
					int aGoalNode = scan.nextInt();
					if(!goalNodes.contains(aGoalNode)){ //Don't add identical goal nodes
						goalNodes.add(aGoalNode);
					}
				}
			}	


			//Set up start node
			nodes.get(start).setAsStart();

			gui.setNodes(String.valueOf(nodes.size()));
			gui.setEdges(String.valueOf(edges.size()));
			if(start == -1){
				gui.setStart("not set!");
			}
			else{
				gui.setStart(String.valueOf(start));
			}

			//Set up goal node(s)
			String goals="Goal(s):";
			if(goalNodes.size()>0){
				goal=goalNodes.get(0);
				for(Integer i:goalNodes){
					nodes.get(i).setAsGoal();		
					goals = goals+" " +String.valueOf(i);
				}
				gui.setGoal(goals);
			}
			else{
				gui.setGoal("none set!");
			}
			gui.println("Finding neighbours for each node...");
			for(Node node:nodes.values()){
				node.findNeighbours();
			}

			gui.println("Graph Parsing successfully finished.");

			long totalTime = System.currentTimeMillis() - initialTime;
			gui.println("Graph parsing took: "+totalTime +" milliseconds");
			scan.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			gui.println("File not found, graph parsing failed.");
		}		
	}

	/**
	 * @return The start node
	 */
	public Node getStartNode(){
		return nodes.get(start);
	}

	/**
	 * @return The goal node
	 */
	public Node getGoalNode(){
		return nodes.get(goal);
	}



	Object lock=new Object();
	public synchronized void setFound(){
		synchronized(lock){
			found=true;
		}
	}
	public synchronized boolean isFound(){
		synchronized(lock){
			return found;
		}
	}
	public void resetNodes(){
		for(Node n: nodes.values()){
			n.resetNode();
		}
	}

	public void run(){
		launch(gui.getTask());
	}
	/**
	 * Launches the search. It will maximize search capabilities by using all threads in the system.
	 */
	public void launch(int t){
		resetGraph();
		timeTaken=System.currentTimeMillis();
		task = t;
		gui.println("");
		if(task==0){
			searchAllPaths();
		}
		else if(task == 1){
			searchSinglePath();
		}
		gui.updateTime(String.valueOf(System.currentTimeMillis()-timeTaken));
		return;
	}

	protected void resetGraph() {
		resetNodes();
		pool = Executors.newFixedThreadPool(threads);
		printed = false;
		paths=0;
	}


	/**
	 * Launches the search on the start node by creating one thread. 
	 * That thread will then create more threads for it's neighbouring nodes whom will also do the same.
	 */
	protected void searchAllPaths() {
		gui.println("Task: Search for ALL possible paths.");
		AllPathFinder pf = new AllPathFinder(this);
		new Thread(pf).start();
	}


	/**
	 * Searches for a single path. It fully utilizes the CPU threads by using it all.
	 * Once a thread finds a path, it will terminate the other threads.
	 */
	protected void searchSinglePath() {
		gui.println("Task: Search for a SINGLE path. Not neccessarily the shortest.");
		for(int i =1;i<threads;i++){
			if(!pool.isShutdown())
				pool.submit(new PathFinder(this,i));
		}
		if(!pool.isShutdown())
			gui.println("Submitted "+threads+" threads to the pool.");
	}

	public synchronized void shutdownFactory(){
		if(!pool.isShutdown()){
			pool.shutdownNow();
			gui.println("Shutdown complete.");
		}
		if(task==0){
			gui.println("Paths found: " + paths);
		}
	}


	public void submitTask(Runnable task){
		pool.submit(task);
	}


	//PRINTING PART
	private int paths = 0; //Number of paths the threads have found to a goal.

	public synchronized void printPaths(String p){
		paths++;
		gui.updatePaths(String.valueOf(paths));
		gui.println("Path " +paths +": " +p);
	}

	private boolean printed = false; //Can only be printed once.
	public synchronized void printResults(String results){
		if(!printed){
			gui.println(results);
			printed=true;
			shutdownFactory();
			gui.println("Thread factory shutdown early because goal has been found.");
		}
	}

	
	public static void main(String args[]){
		Graph g = new Graph();
		g.chooseGraph();
		try{
			g.launch(Integer.parseInt(args[0]));
		}
		catch(Exception e){
			System.out.println("Arguments: Enter 0 or 1. 0 to search all paths, 1 to search for a single path.");
		}
	}

}
