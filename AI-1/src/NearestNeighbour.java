import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import javax.swing.JFileChooser;
import javax.swing.JFrame;


public class NearestNeighbour {

	private ArrayList<Instance> TrainingSet = new ArrayList<Instance>();
	private ArrayList<Instance> TestSet = new ArrayList<Instance>();

	private boolean debug = false;
	//used for kNearestNeighbour
	private double slRange=0;
	private double swRange=0;
	private double plRange=0;
	private double pwRange=0;

	public NearestNeighbour(File file1, File file2){
		debug = false;
		for(int i = 1; i < 76; ++i){
			System.out.println("k = " +i);
			construct(file1,file2);
			kNearestNeighbour(i);
		}
	}

	public void construct(File file1, File file2){
		TrainingSet.clear();
		TestSet.clear();
		try{
			Scanner scan1 = new Scanner(file1);
			while(scan1.hasNext()){
				double sl = Double.parseDouble(scan1.next());
				double sw = Double.parseDouble(scan1.next());
				double pl = Double.parseDouble(scan1.next());
				double pw = Double.parseDouble(scan1.next());
				Instance inst = new Instance(sl,sw,pl,pw, scan1.next());

				TrainingSet.add(inst);
			}
		}
		catch(Exception e){e.printStackTrace();}

		try{
			Scanner scan2 = new Scanner(file2);
			while(scan2.hasNext()){
				double sl = Double.parseDouble(scan2.next());
				double sw = Double.parseDouble(scan2.next());
				double pl = Double.parseDouble(scan2.next());
				double pw = Double.parseDouble(scan2.next());
				Instance inst = new Instance(sl,sw,pl,pw, scan2.next());

				TestSet.add(inst);
			}
		}catch(Exception e){e.printStackTrace();}
	}

	public void kNearestNeighbour(int k){
		computeRanges();
		int correctPredictions=0;
		int wrongPredictions=0;
		for (Instance test: TestSet){
			//double nearest = Double.MAX_VALUE;
			List<Instance> distances = new ArrayList<Instance>();
			for(Instance training: TrainingSet){
				double distance = 0;
				double sum = 0;

				sum += minusSquare(test.getPetalLength(), training.getPetalLength())/plRange;
				sum += minusSquare(test.getPetalWidth(), training.getPetalWidth())/pwRange;
				sum += minusSquare(test.getSepalLength(), training.getSepalLength())/slRange;
				sum += minusSquare(test.getSepalWidth(), training.getSepalWidth())/swRange;
				distance = Math.sqrt(sum);
				Instance tempInstance = new Instance(distance, training.getActual());
				distances.add(tempInstance);
			}
			int[] occurances = new int[3];
			Collections.sort(distances);
			for(int i = 0; i<k ; ++i){
				Iris specie = distances.get(i).getActual();
				//				System.out.println(distances.get(i).distance);
				if(specie == Iris.setosa)
					occurances[0]++;
				else if(specie == Iris.versicolor)
					occurances[1]++;
				else
					occurances[2]++;

			}
			if(occurances[0] > occurances[1] && occurances[0] > occurances[2]){
				test.setPredicted(Iris.setosa);
			}
			else if(occurances[1] > occurances[2])
				test.setPredicted(Iris.versicolor);
			else
				test.setPredicted(Iris.virginica);

			if(test.correctPrediction())
				correctPredictions++;
			else
				wrongPredictions++;

		}

		System.out.println("\tCorrect predictions: " + correctPredictions);
		System.out.println("\tWrong predictions: " + wrongPredictions);
		System.out.println("======================================");

	}

	private double minusSquare(double a, double b){
		return ((a-b)*(a-b));
	}

	private void computeRanges(){
		double slMax = Double.MIN_VALUE;
		double slMin = Double.MAX_VALUE;
		double swMax = Double.MIN_VALUE;
		double swMin = Double.MAX_VALUE;

		double plMax = Double.MIN_VALUE;
		double plMin = Double.MAX_VALUE;

		double pwMax = Double.MIN_VALUE;
		double pwMin = Double.MAX_VALUE;


		for(Instance training: TrainingSet){
			if(training.getSepalLength() > slMax){
				slMax = training.getSepalLength();
			}
			if(training.getSepalLength() < slMin){
				slMin = training.getSepalLength();
			}
			if(training.getSepalWidth() > swMax){
				swMax = training.getSepalWidth();
			}
			if(training.getSepalWidth() < swMin){
				swMin = training.getSepalWidth();
			}

			if(training.getPetalLength() > plMax){
				plMax = training.getPetalLength();
			}
			if(training.getPetalLength() < plMin){
				plMin = training.getPetalLength();
			}
			if(training.getPetalWidth() > pwMax){
				pwMax = training.getPetalWidth();
			}
			if(training.getPetalWidth() < pwMin){
				pwMin = training.getPetalWidth();
			}
		}
		slRange = (slMax-slMin)* (slMax-slMin);
		swRange = (swMax-swMin)*(swMax-swMin);
		plRange = (plMax-plMin)*(plMax-plMin);
		pwRange = (pwMax-pwMin)*(pwMax-pwMin);
		if(debug){
			System.out.println("Sepal Length ranges: " + slMin + " - " + slMax + " : " + slRange);
			System.out.println("Sepal Width ranges: " + swMin + " - " + swMax + " : " + swRange);
			System.out.println("Petal Length ranges: " + plMin + " - " + plMax + " : " + plRange);
			System.out.println("Petal Width ranges: " + pwMin + " - " + pwMax + " : " + pwRange);
		}

	}

	public static void main(String args[]){
		System.out.println("SELECT Training set");
		JFileChooser fc = new JFileChooser();
		int approve = fc.showOpenDialog(new JFrame());
		if(approve == JFileChooser.APPROVE_OPTION){
			File training = fc.getSelectedFile();
			System.out.println("Training set: " + training.getName() + " selected.");
			System.out.println("SELECT Test set");
			
			approve = fc.showOpenDialog(new JFrame());
			
			if(approve == JFileChooser.APPROVE_OPTION){
				File test = fc.getSelectedFile();
				System.out.println("Test set: " + test.getName() + " selected.");
				new NearestNeighbour(training, test);
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

	public static File loadFile(String filename){
		try{
			return new File(filename);
		}
		catch(Exception e){return null;}

	}

	class Instance implements Comparable<Instance>{
		private Iris actualClass;
		private double sepalLength;
		private double sepalWidth;
		private double petalLength;

		private double petalWidth;

		private Iris predictedClass;

		private boolean correct;

		public double distance=0;

		public Instance(double d, Iris specie){
			distance = d;
			actualClass = specie;
		}
		public Instance (double d){
			distance = d;
		}

		public Instance(double sl, double sw, double pl, double pw, String specie){
			if(specie.equalsIgnoreCase("iris-setosa"))
				actualClass = Iris.setosa;
			else if(specie.equalsIgnoreCase("iris-versicolor"))
				actualClass = Iris.versicolor;
			else if(specie.equalsIgnoreCase("iris-virginica"))
				actualClass = Iris.virginica;

			//			System.out.println(actualClass);
			sepalLength = sl;
			sepalWidth = sw;
			petalLength = pl;
			petalWidth = pw;
		}

		public Iris getActual(){
			return actualClass;
		}

		public Iris getPredicted(){
			return predictedClass;
		}

		public void setPredicted(Iris prediction){
			if(actualClass == (prediction)){
				correct = true;
			}
			else{
				correct = false;
			}
			predictedClass = prediction;
		}

		public double getSepalLength() {
			return sepalLength;
		}

		public double getSepalWidth() {
			return sepalWidth;
		}

		public double getPetalLength() {
			return petalLength;
		}

		public double getPetalWidth() {
			return petalWidth;
		}

		public boolean correctPrediction(){
			return correct;
		}


		public int compareTo(Instance dist) {
			if(dist.distance < distance)
				return 1;
			else if(dist.distance>distance)
				return -1;
			return 0;
		}
	}



	enum Iris{
		setosa, versicolor,virginica

	}
}
