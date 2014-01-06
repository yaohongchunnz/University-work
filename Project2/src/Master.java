import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;


/**
 * Represents the server.
 * The server will wait for a certain amount of workers to connect first.
 * Then it will distribute the work equally among the workers and other required information.
 * It will then wait for the workers to sort using the bubble up and down algorithm.
 * @author Yao Hong Chun
 *
 */
public class Master extends Thread {
	private ServerSocket serverSocket;
	private ServerSocket resultSocket;
	private ArrayList<Connection> connections = new ArrayList<Connection>();
	private int currentConnections=0;//number of connections connected to the master
	private ArrayList<Integer> numbers;
	private int maxConnections;
	private int size;
	boolean finished=false;
	/**
	 * @param maxConnections - Max number of connections to wait for.
	 * @param size - The total number of numbers to distribute.
	 */
	public Master(int maxConnections, int size){
		this.maxConnections=maxConnections;
		this.size = size;
	}
	@Override
	public void run(){
		try {
			//ACCEPT INCOMING CONNECTIONS
			serverSocket = new ServerSocket(9000);
			System.out.println("Server up and running!");

			while(currentConnections < maxConnections){
				System.out.println("Still need " + (maxConnections-currentConnections) + " workers to start.");
				Socket client = serverSocket.accept();	
				Connection connection = new Connection(client);
				System.out.println("Incoming connection...");

				int request = connection.receiveInteger();
				System.out.println("Received request!");


				if(request ==0){
					System.out.println("A new worker has connected.");
					currentConnections++;
					connections.add(connection);
				}
				
				for(Connection c: connections){
					c.sendInteger(maxConnections-currentConnections);
				}

			}
			System.out.println("A total of " + currentConnections + " workers have connected! Now distributing work!");

			//CREATE RANDOM NUMBERS
			numbers = new ArrayList<Integer>();
			for(int i = 1;i<=size;i++){
				numbers.add(i);
			}
			Collections.shuffle(numbers); //Shuffle the numbers around

			int[] nums = convertIntegers(numbers);
			Worker.printArrayValues(nums);
			//SEND NUMBERS TO CLIENTS
			int from = 0;
			int to = size/currentConnections;
			int i = 1;
			ArrayList<AwaitCompletion> await = new ArrayList<AwaitCompletion>();
			for(int k =0;k<connections.size();k++){
				System.out.println(i+":Created numbers to send! Sending...");

				if((k+1)<connections.size()){ //send address of right neighbor
					String address=connections.get(k+1).getAddress();
					byte[] data=address.getBytes("UTF-8");
					connections.get(k).sendInteger(data.length);
					connections.get(k).sendByteArray(data);
				}
				else{
					connections.get(k).sendInteger(-1);
				}
				connections.get(k).sendInteger(i); //send position
				connections.get(k).sendArray(createSegment(from,to,nums));
				i++;
				from = to;
				to = to+(size/currentConnections);
				System.out.println("Done!");
				//Wait for completion
				AwaitCompletion ac= new AwaitCompletion(connections.get(k));
				await.add(ac);
				ac.start();
			}
			while(!finished){ //Constantly check if the workers are finished
				//If all workers are finished then set finished =true;

				for(Connection c:connections){
					if(!c.isDone()){
						finished = false;
						break;
					}
					else{
						finished = true;
						
					}
				}
			}
			System.out.println("All finished!! Notifying all workers...");
			for(Connection c: connections){
				c.sendInteger(1);
			}
			for(AwaitCompletion aw: await){
				aw.shutdown();
				aw.interrupt();
			}

			serverSocket.close();
			System.out.println("Retrieving results...");
			int temp = 0;
			resultSocket = new ServerSocket(8999);
			while(temp < maxConnections){
				Socket s = resultSocket.accept();
				Connection c = new Connection(s);
				System.out.println("Results from worker "+(temp+1)+":");
				Worker.printArrayValues(c.receiveArray());
				temp++;
				System.out.println();
			}

			System.out.println("Operation complete");

		} catch (IOException e1) {
			e1.printStackTrace();
			System.exit(-1);
		}
	}

	/**
	 * This class instantiates for every connection to a worker.
	 * It will keep receiving updates from the worker, whether or not the worker has finished.
	 * This information is required for the server to know whether or not ALL workers have finished,
	 * and if they have all finished, the server will tell the workers to terminate.
	 * @author Yao Hong Chun
	 *
	 */
	private class AwaitCompletion extends Thread{
		private Connection connection;
		public AwaitCompletion(Connection c){
			System.out.println("Started awaitcompletion");
			connection = c;
		}

		@Override
		public void run(){
			try{
			while(!finished){
				int status = connection.receiveInteger();
				if(status > 0){ //if status is over 0 then the worker is done. The worker will send it's position if its done, otherwise -position.
					connection.setDone();
					System.out.println("Worker position " + status + " has sorted");
				}
				else{
					connection.notDone();
				}
			}
			}
			catch(Exception e){
			}
		}
		
		public void shutdown(){
			connection=null;
		}

	}
	public int[] createSegment(int from,int to,int[] nums){
		int[] n = new int[to-from];

		int j =0;
		for(int i = from;i<to;i++){
			n[j] = nums[i];
			j++;
		}
		System.out.println("FROM: " + from + " TO: " + to);
		Worker.printArrayValues(n);
		return n;
	}

	/**
	 * Puts all the numbers in an arraylist into an array
	 * @param numbers The arraylist containing all the numbers
	 * @return The array containing the numbers that was in the arraylist
	 */
	public static int[] convertIntegers(ArrayList<Integer> numbers){
		int[] nums = new int [numbers.size()];
		for(int i = 0;i<nums.length; i++){
			nums[i] = numbers.get(i).intValue();
		}
		return nums;
	}
	public static void main(String args[]){
		Scanner userinput = new Scanner(System.in);
		int workers = 0;
		while(workers < 2){
			System.out.println("Enter number of workers:");
			workers = userinput.nextInt();
			if(workers < 2){
				System.out.println("Must be more than one worker.");
			}
		}
		System.out.println("Enter number of numbers:");
		int numbers = userinput.nextInt();
		new Master(workers,numbers).start();
		userinput.close();
//		new Master(4,4000).start();
	}

}
