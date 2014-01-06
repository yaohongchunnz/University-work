import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;


/**
 * This represents a connection between a client and a server.
 * A worker will connect to the server and both the server and worker will create a connection so 
 * that they can communicate back and forth.
 * @author Yao Hong Chun
 *
 */
public class Connection {
	Socket s;
	DataOutputStream out;
	DataInputStream in;
	boolean done=false;
	/**
	 * @param sock - The socket of the connected client or server.
	 */
	public Connection(Socket sock){
		s = sock;
		try {
			out = new DataOutputStream(s.getOutputStream());
			in = new DataInputStream(s.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**Sends an integer to the server or client
	 * @param i - The integer to send
	 */
	public void sendInteger(int i){
		try {
			out.writeInt(i);
		} catch (IOException e) {
		}
	}

	/** Receives an integer from the client or server
	 * @return The integer that was received
	 */
	public int receiveInteger(){
		try {
			return in.readInt();
		} catch (IOException e) {
		}
		return -1;
	}

	/** Sends an array to the client or server, contents of array must be >= 0
	 * @param array - The array to be sent
	 */
	public void sendArray(int[] array){
		try {
			for(int i =0;i<array.length;i++){
				out.writeInt(array[i]); //send values
			}
			out.writeInt(-1); //end
			System.out.println("Array successfully sent.");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public int[] receiveArray(){
		ArrayList<Integer> nums = new ArrayList<Integer>();
		while(true){//receive number of numbers to sort
			int reply = receiveInteger();
			if(reply!=-1)
				nums.add(reply);
			else{
				break;
			}
		}
		int[] numbers = Master.convertIntegers(nums);
		return numbers;
	}

	public void sendByteArray(byte[] b){
		try{
			out.write(b);
		}
		catch(Exception e){}

	}

	public void readFully(byte[] b){
		try{
			in.readFully(b);
		}catch(Exception e){}
	}

	public void shutdown(){
		try {
			out.close();
			in.close();
			s.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void setDone(){
		done=true;
	}
	public void notDone(){
		done=false;
	}
	
	public boolean isDone(){
		return done;
	}
	public String getAddress(){
		return s.getInetAddress().getHostAddress();
	}
	
}
