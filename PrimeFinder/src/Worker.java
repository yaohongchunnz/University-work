
public class Worker extends Thread {
	private int numberOfPrimesFound = 0;
	private int low;
	private int high;
	private int id;
	private int largestPrime;
	
	public Worker(int id, int low, int high){
		if(low <= 1){
			low =2;
		}
		this.id = id;
		this.low = low;
		this.high = high;
		if(low>high){
			System.out.println("Error: Thread " + id + "'s Lower bound cannot be greater than higher bound");
			this.high = low+1000;
			System.out.println("Setting higher bound to " + this.high);
		}
	}
	
	public void run(){
		long startTime = System.currentTimeMillis();
		for(int i = low; i<high; ++i){
			if(isPrime(i)){
				numberOfPrimesFound++;
			}
		}
		long endTime = System.currentTimeMillis();
		float totalTime = (endTime - startTime);
		System.out.println("Thread " + id + " searched from " + low + " to " + high + " and has found " + numberOfPrimesFound + " primes. Time taken: " + totalTime + " milliseconds.");
	}
	
	public boolean isPrime(int number){
		
		int maxTest =(int) Math.sqrt(number)+1;
		
		for(int i = 2; i < maxTest; ++i){
			if(number%i == 0){
				return false;//Not a prime
			}
		}	
		
		largestPrime = number;
		return true;
	}
	
	public int getLargestPrime(){
		return largestPrime;
	}
	
	public int getNumberOfPrimesFound(){
		return numberOfPrimesFound;
	}
	
}
