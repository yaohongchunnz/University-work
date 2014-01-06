import java.io.File;
import java.util.Scanner;


public class DTLearning {
	boolean debug = true;

	String classifier1;
	String classifier2;


	int  liveOccurances;
	int dieOccurances;
	int total;

	String[] attributes = new String[16];

	int[] class1True ;
	int[] class1False ;

	int[] class2True;
	int[] class2False;

	int[] trueOcc ;
	int[] falseOcc ;

	double[] impurities ;

	int numberOfAttributes =  0;

	public DTLearning(){
		loadFile();
		if(debug){
			System.out.println("Size of set: " +total);
			System.out.println(classifier1 + ": " +liveOccurances + "\t\t\t" + classifier2 + ": " + dieOccurances);




			for(int i = 0; i<numberOfAttributes; ++i){
				System.out.print(classifier1+"True:"+ class1True[i] + "\t"+classifier1+"False:" + class1False[i] + "\t"+classifier2+"True:" + class2True[i] + "\t"+classifier2+"False:" + class2False[i] + "\tTrueOcc:" + trueOcc[i] + "\tFalseOcc:"+falseOcc[i] + "\t" + attributes[i]);
				System.out.println();

			}

		}

		calculateImpurities();
		buildTree();
	}

	public void buildTree(){


	}
	public void calculateImpurities(){
		for(int i = 0; i <numberOfAttributes;++i){
			impurities[i] = (imp(class1True[i],class2True[i],trueOcc[i],total)+imp(class1False[i],class2False[i],falseOcc[i],total));
			if(debug)
				System.out.println(impurities[i] + "\t" + attributes[i]);
		}
	}

	public void initialiseFields(){
		class1True = new int[numberOfAttributes];
		class1False = new int[numberOfAttributes];

		class2True = new int[numberOfAttributes];
		class2False = new int[numberOfAttributes];

		trueOcc = new int[numberOfAttributes];
		falseOcc = new int[numberOfAttributes];

		impurities = new double[numberOfAttributes];
	}
	public void loadFile(){
		try{
			Scanner scanLine = new Scanner(new File("src/part3/hepatitis-training.data"));
			Scanner scanNext;
			scanNext = new Scanner(scanLine.nextLine());
			classifier1 = scanNext.next();
			classifier2 = scanNext.next();
			scanNext = new Scanner(scanLine.nextLine());

			while(scanNext.hasNext()){
				attributes[numberOfAttributes] = scanNext.next();
				numberOfAttributes++;
			}
			initialiseFields();

			if(debug)
				System.out.println("NUMBER OF ATTRIBUTES: " + numberOfAttributes);


			while(scanLine.hasNextLine()){
				scanNext = new Scanner(scanLine.nextLine());
				String classifier = scanNext.next();

				if(classifier.equalsIgnoreCase(classifier1)){
					liveOccurances++;
				}
				else
					dieOccurances++;

				for(int i = 0; i<numberOfAttributes; ++i){
					boolean value = scanNext.nextBoolean();
					if(classifier.equalsIgnoreCase(classifier1)){
						if(value)
							class1True[i]++;
						else
							class1False[i]++;
					}
					else{
						if(value)
							class2True[i]++;
						else
							class2False[i]++;


					}
				}

			}


		}catch(Exception e){e.printStackTrace();}

		for(int i = 0; i<numberOfAttributes; ++i){
			trueOcc[i] = class1True[i] + class2True[i];
			falseOcc[i] = class1False[i] + class2False[i];

		}
		total = liveOccurances + dieOccurances;
	}

	//Calculates the impurity of an attribute
	public double imp(double class1, double class2, double count, double total){
		return ((count/total)*((class1/count)*(class2/count)));

	}

	private class DT{

		public DT(){

		}
	}



	public static void main(String args[]){
		DTLearning dt = new DTLearning();

		//System.out.println(dt.imp(4,2,6,10)+dt.imp(1,3,4,10));
	}
}
