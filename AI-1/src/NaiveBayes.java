import java.io.File;
import java.util.Scanner;

import javax.swing.JFileChooser;
import javax.swing.JFrame;


public class NaiveBayes {
	//TABLES!!!!!!!!!
	//Occurences!
	private int[] spamOccTrue = new int[12];
	private int[] spamOccFalse = new int[12];
	private int[] notspamOccTrue = new int[12];
	private int[] notspamOccFalse = new int[12];
	private int spam=1;
	private int notspam=1;
	private int emails = 2;
	
	//Probabilities!!!!!
	private double probspam;
	private double probnotspam;	
	private double[] probspamTrue = new double[12];
	private double[] probspamFalse = new double[12];
	private double[] probnotspamTrue = new double[12];
	private double[] probnotspamFalse = new double[12];
	
	
	public NaiveBayes(File labelled, File unlabelled){
		initialiseTables(); //Add 1 to all occurrence tables
		countOccurances(labelled); //Count them all!
		computeProbabilities(); //Compute dat prob
		determineClassifiers(unlabelled);
	}
	
	public void determineClassifiers(File unlabelled){
		System.out.println("================= CLASSIFIERS =================");
		try{
			Scanner scanLine = new Scanner(unlabelled);
			
			while(scanLine.hasNextLine()){
				Scanner scanNext = new Scanner(scanLine.nextLine());
				int features[] = new int[12];
				features[0] = scanNext.nextInt();
				features[1] = scanNext.nextInt();
				features[2] = scanNext.nextInt();
				features[3] = scanNext.nextInt();
				features[4] = scanNext.nextInt();
				features[5] = scanNext.nextInt();
				features[6] = scanNext.nextInt();
				features[7] = scanNext.nextInt();
				features[8] = scanNext.nextInt();
				features[9] = scanNext.nextInt();
				features[10] = scanNext.nextInt();
				features[11] = scanNext.nextInt();
				double spamprob=0;
				double notspamprob=0;
				double denominator = 0;
				//calculate numerator
				for(int i = 0; i <12;++i){
					System.out.print(features[i] + " ");
					if(features[i] == 1){ //feature is true
						if(spamprob==0){spamprob+=probspamTrue[i];notspamprob+=probnotspamTrue[i];} //current prob is 0 then add to it
						else{
							spamprob*=probspamTrue[i]; //multiply by next feature prob
							notspamprob*=probnotspamTrue[i]; //multiply by next feature prob
							
						}
					}
					else{ //feature is false
						if(spamprob==0){
							spamprob+=probspamFalse[i];
							notspamprob+=probnotspamFalse[i];
						}
						else{
							spamprob*=probspamFalse[i];
							notspamprob*=probnotspamFalse[i];
						}
						
					}
					
				}
				spamprob*=probspam;
				notspamprob*=probnotspam;//finish off calculating the numerator
				
				//calculating denominator
				for(int i = 0; i<12;++i){
					if(features[i]==1){
						if(denominator==0)
							denominator += (probspamTrue[i] + probnotspamTrue[i]);
						else
							denominator *= (probspamTrue[i] + probnotspamTrue[i]);
						
					}
					else{
						if(denominator==0)
							denominator += (probspamFalse[i] + probnotspamFalse[i]);
						else
							denominator *= (probspamFalse[i] + probnotspamFalse[i]);							
						
					}
					
				}
				
				
				spamprob = spamprob/denominator;
				notspamprob = notspamprob/denominator;
				System.out.print(spamprob + " " + notspamprob + " ");
				 
				if(spamprob >= notspamprob){
					System.out.print("\tSPAM");
				}
				else{
					System.out.print("\tNOT SPAM");

				}
				System.out.println();
			}
			
			
		}catch(Exception e){e.printStackTrace();}
		
	}
	
	public void computeProbabilities(){
		probspam = (double)spam/(double)emails;
		probnotspam = (double)notspam/(double)emails;

		System.out.println("========= PROBABILITIES =========");
		System.out.println("Probability Spam: " + probspam + "\tProbability notspam: " + probnotspam);
		
		for(int i = 0; i<12; ++i){
			probspamTrue[i] = (double)spamOccTrue[i]/(double)(spamOccTrue[i]+spamOccFalse[i]);
			probspamFalse[i] = (double)spamOccFalse[i]/(double)(spamOccTrue[i]+spamOccFalse[i]);
			probnotspamTrue[i] = (double)notspamOccTrue[i]/(double)(notspamOccTrue[i]+notspamOccFalse[i]);
			probnotspamFalse[i] = (double)notspamOccFalse[i]/(double)(notspamOccTrue[i]+notspamOccFalse[i]);
			

			System.out.print("Feature " + (i+1) + " (1):\t" + probspamTrue[i] + "\t" + probnotspamTrue[i]);
			System.out.println();
			System.out.print("Feature " + (i+1) + " (0):\t" + probspamFalse[i] + "\t" + probnotspamFalse[i]);
			System.out.println();
			
		}
		
		
		
	}
	
	
	public void countOccurances(File labelled){

		try{
			Scanner scanLine = new Scanner(labelled);

			while(scanLine.hasNextLine()){
				Scanner scanNext = new Scanner(scanLine.nextLine());
				int features[] = new int[12];
				features[0] = scanNext.nextInt();
				features[1] = scanNext.nextInt();
				features[2] = scanNext.nextInt();
				features[3] = scanNext.nextInt();
				features[4] = scanNext.nextInt();
				features[5] = scanNext.nextInt();
				features[6] = scanNext.nextInt();
				features[7] = scanNext.nextInt();
				features[8] = scanNext.nextInt();
				features[9] = scanNext.nextInt();
				features[10] = scanNext.nextInt();
				features[11] = scanNext.nextInt();
				int classifier = scanNext.nextInt();
				
				if(classifier == 1){ //It is a spam
					spam++;
					for(int i = 0;i<12;++i){
						if(features[i] == 1){
							spamOccTrue[i]++;							
						}
						else{
							spamOccFalse[i]++;							
						}					
					}
				}
				else{ //Not a spam
					notspam++;
					for(int i = 0;i<12;++i){
						if(features[i] == 1){
							notspamOccTrue[i]++;							
						}
						else{
							notspamOccFalse[i]++;							
						}					
					}					
				}
				emails++;
				
			}
			
			System.out.println("===========OCCURANCES===========");
			for(int i = 0; i<12;++i){
				System.out.print("Spam 1: " + spamOccTrue[i]+ "\t");
				System.out.print("NotSpam 1: " + notspamOccTrue[i]);
				System.out.println();
				System.out.print("Spam 0: " + spamOccFalse[i]+ "\t");
				System.out.print("NotSpam 0: " + notspamOccFalse[i]);
				System.out.println();
			}
			System.out.println("Total spam: " + spam);
			System.out.println("Total not spam: " + notspam);
			

		}catch(Exception e){e.printStackTrace();}
	}
	
	public void initialiseTables(){
		for(int i = 0; i<12;++i){
			spamOccTrue[i]++;
			notspamOccTrue[i]++;
			spamOccFalse[i]++;
			notspamOccFalse[i]++;
			
			
		}
		
	}

	public static void main(String args[]){
		System.out.println("SELECT labelled set");
		JFileChooser fc = new JFileChooser();
		int approve = fc.showOpenDialog(new JFrame());
		if(approve == JFileChooser.APPROVE_OPTION){
			File labelled = fc.getSelectedFile();
			System.out.println("Labelled set: " + labelled.getName() + " selected.");
			System.out.println("SELECT unlabelled set");
			
			approve = fc.showOpenDialog(new JFrame());
			
			if(approve == JFileChooser.APPROVE_OPTION){
				File unlabelled = fc.getSelectedFile();
				System.out.println("Unlabelled set: " + unlabelled.getName() + " selected.");
				new NaiveBayes(labelled, unlabelled);
			}
			else{
				System.out.println("Cancelled operation");
				System.exit(0);
			}
		}
		else{
			System.out.println("Cancelled operation");
			System.exit(0);
		}
	}
}
