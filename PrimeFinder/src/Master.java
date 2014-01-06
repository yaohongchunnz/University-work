import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.util.ArrayList;

import javax.swing.JLabel;


public class Master extends Thread{
	private int low;
	private int high;
	private int numberOfThreads;
	private ArrayList<Worker> workers = new ArrayList<Worker>();
	private PrimeGUI pg;
	private boolean start=false;
	public Master(final PrimeGUI pg){
		this.pg = pg;
		pg.getLaunchButton().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				low = pg.getSearchFrom();
				high = pg.getSearchTo();
				numberOfThreads = pg.getNumberOfThreads();
				if(low!=0&&numberOfThreads!=0){
					start = true;
				}
			}
		});
	}

	public void run(){
		while(true){
			System.out.print("");
			if(start){
				divideAndConquer();
				start=false;
			}
		}
	}
	public void divideAndConquer(){
		workers.clear();
		//Divide the work
		int divide = (high-low)/numberOfThreads;
		int to = divide+low;
		for(int i = 0; i < numberOfThreads;++i){
			Worker worker = new Worker(i+1, low, to+i);
			workers.add(worker);
			low += divide+1;
			to += divide;

		}
		long startTime = System.currentTimeMillis();
		pg.report("Starting workers... ");
		//Start workers/threads
		for(Worker w: workers){
			w.start();
		}
		pg.report("All workers started.");
		//Wait for threads to finish
		try{
			for(Worker w: workers){
				w.join();
			}
		}
		catch(Exception e){}

		//Add up all the primes found
		int totalNumberOfPrimes=0;
		int largestPrime = 0;
		for(Worker w: workers){
			totalNumberOfPrimes = totalNumberOfPrimes + w.getNumberOfPrimesFound();
			if(w.getLargestPrime() > largestPrime){
				largestPrime = w.getLargestPrime();				
			}
		}


		//Calculate total time taken
		long endTime = System.currentTimeMillis();
		float totalTime = endTime - startTime;

		//Report results
		pg.report("Operation took: " + totalTime + " milliseconds.");
		pg.report("Total primes found: " + totalNumberOfPrimes );
		pg.report("Largest prime found: " + largestPrime );


	}


	public static void main(String args[]){
		//Create new GUI
		final PrimeGUI pg = new PrimeGUI();
		Master m = new Master(pg);
		m.start();
	}
}
