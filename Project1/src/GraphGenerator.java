
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;



/**
 * This class generates a random graph in a format specified in step 0 of the project.
 * @author Yao Hong Chun
 *
 */
public class GraphGenerator {

	public void generateTextGraph(String filename, int numberOfNodes, int minEdgesPerNode, int maxEdgesPerNode, int start, int goal){
		System.out.println("Generating graph with " +numberOfNodes +" nodes, with "+minEdgesPerNode+" minimum edges per node, "+maxEdgesPerNode+" max edges per node.");
		System.out.println("Node "+start+" is set as start node");
		System.out.println("Node "+goal+" is set as goal node");
		System.out.println("Warning: Start node and goal node may not be connected!!!");
		System.out.println("Please wait! This may take a while");
		try {
			ArrayList<Node> addedNodes = new ArrayList<Node>();
			ArrayList<Edge> edges = new ArrayList<Edge>();

			PrintWriter writer = new PrintWriter(new FileWriter(filename));
			for(int i = 0; i< numberOfNodes;++i){
				Node node = new Node(i);
				addedNodes.add(node);
			}			
			Random rand = new Random(); //Randomize number of edges per node
			int numbOfNodes = addedNodes.size(); 
			for(Node n: addedNodes){
				if(!n.isConnected()){
					if(!(n.numberOfEdges()>=maxEdgesPerNode)){
						generateEdges(minEdgesPerNode, maxEdgesPerNode, addedNodes,
								edges, writer, rand, numbOfNodes, n);
					}
				}
//				else{
//					generateEdges(minEdgesPerNode, maxEdgesPerNode, addedNodes,
//							edges, writer, rand, numbOfNodes, n);
//				}

			}

			writer.println("S "+start);
			writer.println("G "+goal);
			writer.close();
			System.out.println("Graph successfully generated.");
		} catch (IOException e) {}

	}

	private void generateEdges(int minEdgesPerNode, int maxEdgesPerNode,
			ArrayList<Node> addedNodes, ArrayList<Edge> edges,
			PrintWriter writer, Random rand, int numbOfNodes, Node n) {

		int numbEdges = rand.nextInt(maxEdgesPerNode-minEdgesPerNode+1)+minEdgesPerNode;
		for(int i=0;i<numbEdges;++i){//Make edges

			Node otherNode =addedNodes.get(rand.nextInt(numbOfNodes));
			while(otherNode.getNumber()==n.getNumber()){
				otherNode =addedNodes.get(rand.nextInt(numbOfNodes));
			}
			Edge edge = new Edge(n, otherNode);//Randomize the target node for each edge

			if(!Edge.edgeAlreadyContainedIn(edges, edge)){
				edges.add(edge);
				writer.println("E "+edge.node1.getNumber()+" "+edge.node2.getNumber());
			}
			else{
				i--;
			}
		}
	}

	public void generateQuickGraph(String filename, int size){
		try {
			System.out.println("Creating a quick graph with approximately " + size +" nodes.");
			ArrayList<Node> addedNodes = new ArrayList<Node>();
			ArrayList<Integer> randomOtherNode = new ArrayList<Integer>();
			PrintWriter writer = new PrintWriter(new FileWriter(filename));
			for(int i = 0; i< size;++i){
				randomOtherNode.add(i);
				Node node = new Node(i);
				addedNodes.add(node);
			}			
			System.out.println("Nodes generated");
			Collections.shuffle(randomOtherNode);
			
			for(int i =0;i<size;++i){
				if(!addedNodes.get(i).isConnectedTo(addedNodes.get(randomOtherNode.get(i)))){
					Edge edge = new Edge(addedNodes.get(i), addedNodes.get(randomOtherNode.get(i)));
					addedNodes.get(i).addEdge(edge);
					addedNodes.get(randomOtherNode.get(i)).addEdge(edge);
					writer.println(edge.toString());
				}
			}
			writer.close();
			System.out.println("Done!");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void main(String args[]){
		GraphGenerator gg = new GraphGenerator();
		gg.generateQuickGraph("CanisMajorisGraph", 10000000);
	}

}
