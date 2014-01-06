import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;


/**
 * @author Yao Hong Chun
 *
 */
public class Worker implements Runnable{
	private int position;//position of where the worker is working 
	private boolean finished = false;
	private boolean sorted = false;
	//Communication to the server
	private Connection serverConnection;

	//Communication to neighbors
	private ServerSocket serverSocket;
	private Socket rightSocket; //Right neighbor
	private int leftPort=9000;
	private int rightPort=9000;
	private int thisPort = 9000;

	Connection leftConnection;
	Connection rightConnection;

	String rightAddress;


	private int[] numbersToSort;
	int length;//length of array
	String serverIp;
	public Worker(String serverIP){
		serverIp = serverIP;
		initializeNumbers(serverIP);
		initializeNeighborCommunications();

		new Thread(this).start();
	}

	//
	@Override
	public void run(){
		System.out.println("Started.");
		while(!finished){
			if(rightConnection!=null){
				int highest = bubbleUp(numbersToSort);
				rightConnection.sendInteger(highest);
				int rightLowest = rightConnection.receiveInteger();
				if(rightLowest < numbersToSort[numbersToSort.length-1]){
					numbersToSort[numbersToSort.length-1]=rightLowest;
				}
			}

			if(thisPort!=9001 && leftConnection!=null){
				int lowest = bubbleDown(numbersToSort);
				leftConnection.sendInteger(lowest);
				int leftHighest = leftConnection.receiveInteger();
				if(leftHighest > numbersToSort[0]){
					numbersToSort[0]=leftHighest;
				}
			}
			if(thisPort==9001) {
				bubbleDown(numbersToSort);
			}
			if(rightAddress == null){
				bubbleUp(numbersToSort);
			}
		}	
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		//close neighboring connections
		if(rightAddress != null){
			rightConnection.shutdown();
		}
		if(thisPort!=9001)
			leftConnection.shutdown();

		//Send results to server
		try {
			Thread.sleep(position*1000);
			System.out.println("Sending result to server...");
			Socket resultSocket = new Socket(serverIp, 8999);
			Connection c = new Connection(resultSocket);
			c.sendArray(numbersToSort);
			c.shutdown();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		serverConnection.shutdown();
		System.out.println("All connections closed.");
		System.out.println("Result:");
		//print results
		printArrayValues(numbersToSort);
		System.exit(0);
	}

	/**
	 * This runnable object runs concurrently on a separate thread. It keeps updating the server by telling its current 
	 * state. Whether it is sorted or not. If the server knows that ALL the workers are sorted then the server will tell all workers
	 * to terminate.
	 */
	private Runnable finishSearch = new Runnable(){
		@Override
		public void run(){
			while(!finished){
				if(sorted){
					System.out.println("Sorted!! Telling server now...");
					serverConnection.sendInteger(position); //Tell the server that this worker has sorted its array completely
					System.out.println("Waiting for reply...");
				}
				else{
					serverConnection.sendInteger(-(position)); //Tell the server that this worker hasn't sorted its array completely yet
				}

				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				System.out.println("Resending...");
			}
		}
	};

	/**
	 * This runnable object waits for the server to reply. If the reply is 1, then it is ok to terminate everything.
	 */
	private Runnable receiveReply = new Runnable(){
		@Override
		public void run(){
			while(!finished){
				int reply = serverConnection.receiveInteger();
				if(reply == 1){ //1 means all workers are sorted
					System.out.println("Server says all other workers are done and sorted. Terminating now...");
					finished = true;
				}
			}
		}
	};

	/**
	 * Communicates to the master on port 9000. Then waits until there are sufficient workers connected to the server.
	 * Once enough workers have connected the master will send address of right neighbor, length of array, numbers to sort and a position.
	 * Otherwise it will terminate.
	 */
	public void initializeNumbers(String serverIP){
		boolean connected = false;
		while(!connected)
			try{
				//contact server on port 9000
				Socket contactServerSocket = new Socket(serverIP,9000);
				serverConnection = new Connection(contactServerSocket); 
				serverConnection.sendInteger(0); //Send 0 to server.
				connected = true;

				System.out.println("Waiting until server has enough workers"); //Wait until server has enough workers.
				int reply = 1;
				while(reply != 0 ){
					reply = serverConnection.receiveInteger();
					if(reply>0)
						System.out.println(reply + " more workers needed!");
				}
				if(reply == 0){//OK to receive numbers
					System.out.println("Enough workers!");
					int strLength=serverConnection.receiveInteger();
					if(strLength != -1){
						byte[] data=new byte[strLength];
						serverConnection.readFully(data);
						rightAddress=new String(data,"UTF-8"); //receive address of the right neighbor
					}
					else{
						rightAddress = null; //Then this worker is on the right most side
					}

					position = serverConnection.receiveInteger(); //receive position
					//				length =serverConnection.receiveInteger();//receive length of array
					numbersToSort =serverConnection.receiveArray();//receive array
					System.out.println(rightAddress);
					System.out.println("Received numbers!");
					leftPort = leftPort +position-1;
					rightPort = rightPort + position +1;
					thisPort = thisPort + position;

					System.out.println("Position: "+position);
					System.out.println("# Numbers to sort: " + numbersToSort.length);
					System.out.println("This port: " + thisPort);
					System.out.println("Left port: " + leftPort);
					System.out.println("Right port: " + rightPort);
					printArrayValues(numbersToSort);
					try {
						serverSocket = new ServerSocket(thisPort);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				else {
					System.exit(0);
				}
			}
		catch(Exception e){
			System.out.println("Could not reach the server, please enter a valid IP Address.");
			Scanner userinput = new Scanner(System.in);
			System.out.println("Enter IP Address of server:");
			serverIP = userinput.next();
			userinput.close();
		}
	}

	/**
	 * Initializes neighbor communications. If the workers port is 9001 it means the worker is on the left most side of the collection of workers therefore
	 * it doesn't have a left neighbor. If the worker did not receive an address to the right neighbor then the worker is on the rightmost side of the collection
	 * of workers therefore it doesn't have a right neighbor. Otherwise the worker will need to connect using a socket to the right neighbor and 
	 * wait for a client from the left to connect to its server.
	 */
	public void initializeNeighborCommunications(){
		System.out.println("\nInitializing neighbor communications...");
		boolean connected = false;
		if(thisPort!=9001){ //If not the leftmost worker
			new Thread(grabLeftNeighborServer).start(); //Then get leftneighbor
		}
		if(rightAddress!=null){ //if not the rightmost worker
			while(!connected){ //Keep trying until connected to the right neighbor.
				try{
					System.out.println("Attempting to connect to right neighbor...");
					rightSocket = new Socket(rightAddress,thisPort+1);
					rightConnection = new Connection(rightSocket);
					connected = true;
					System.out.println("Connected to right neighbor.");
				}catch(Exception e){
					try {
						Thread.sleep(1000); //Retry every second
					} catch (InterruptedException e1) {
					}
					System.out.println("Connecting to right neighbor failed... retrying...");
				};
			}
		}
		new Thread(finishSearch).start(); 
		new Thread(receiveReply).start(); 
	}


	/**
	 * This Runnable object runs on a separate thread concurrently and constantly waits for the left neighbor to connect.
	 * If the port is 9001 it means the worker is on the left most side of the collection of workers therefore
	 * it doesn't have a left neighbor so it doesn't need to run this thread.
	 */
	Runnable grabLeftNeighborServer = new Runnable(){
		@Override
		public void run(){
			try {
				serverSocket = new ServerSocket(thisPort);
				System.out.println("Started server.");
			} catch (IOException e) {
				//				e.printStackTrace();
			}
			boolean connected = false;
			while(!connected)
				try {
					if(thisPort != 9001){
						Socket leftNeighbor = serverSocket.accept();
						System.out.println("Left neighbor connected");
						leftConnection = new Connection(leftNeighbor);
						connected = true;
					}
				} catch (IOException e1) {
					e1.printStackTrace();
				}

		}
	};

	/**
	 * @param numbers - The array to bubble up on
	 * @return the highest number in the array
	 */
	public int bubbleUp(int[] numbers){
		int swaps =0;
		for(int i = 0; i<numbers.length-1;i++){
			if(numbers[i] > numbers[i+1]){
				int temp = numbers[i];
				numbers[i] = numbers[i+1];
				numbers[i+1] = temp;
				swaps++;
			}
		}
		checkIfSorted(swaps);
		return numbers[numbers.length-1];
	}

	/**
	 * @param numbers - The array to bubble down on.
	 * @return the lowest number in the array.
	 */
	public int bubbleDown(int[] numbers){
		int swaps =0;
		for(int i =numbers.length-1; i>1;i--){
			if(numbers[i] < numbers[i-1]){
				int temp = numbers[i];
				numbers[i] = numbers[i-1];
				numbers[i-1] = temp;
				swaps++;
			}
		}
		checkIfSorted(swaps);
		return numbers[0];
	}

	/**
	 * Checks if the array is sorted. There are two conditions:
	 * 1) Can only be sorted if certain connections are not null 
	 * 2) swaps == 0
	 * @param swaps - number of swaps, if swaps == 0 that means the numbers are sorted.
	 */
	protected void checkIfSorted(int swaps) {
		boolean previous = sorted; 
		//Can only be sorted if certain connections are not null and swaps == 0
		try {
			if(leftConnection==null)
				sorted = swaps==0&&rightConnection!=null;// if no swaps has been performed then the array is sorted.
			if(leftConnection!=null&&rightConnection!=null){
				sorted = swaps==0;
			}
			if(rightConnection==null){
				sorted = swaps==0 && leftConnection!=null;
			}

			if(previous && !sorted){ //If it was sorted before and became unsorted again then tell the server immediately.
				System.out.println("Not sorted anymore, telling the server...");
				Thread.sleep(20); //Prevent it from clogging up the stream
				serverConnection.sendInteger(-(position)); //Tell the server that this worker hasn't sorted its array completely yet
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * prints all elements in the array in a line
	 * @param num The array to be printed
	 */
	public static void printArrayValues(int[] num){
		for(int i = 0;i<num.length;i++){
			System.out.print(num[i] + " ");
		}
	}

	public static void main(String args[]){
		Scanner userinput = new Scanner(System.in);
		System.out.println("Enter IP Address of server:");
		String ip = userinput.next();
		System.out.println();
		new Worker(ip);
		userinput.close();
		//		new Worker("192.168.1.50");
	}
}
