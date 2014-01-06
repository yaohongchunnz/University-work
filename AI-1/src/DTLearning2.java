import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class DTLearning2 {
	private List<String> categoryNames = new ArrayList<String>(); // class
	private List<String> attributes = new ArrayList<String>();
	private List<Instance> allInsts = new ArrayList<Instance>();
	private Node root;
	public DTLearning2(){
		try{
			Scanner scan = new Scanner(new File("src/part3/hepatitis-training.data"));
			Scanner next = new Scanner(scan.nextLine());

			categoryNames.add(next.next());
			categoryNames.add(next.next());
			next = new Scanner(scan.nextLine());

			while(next.hasNext()){
				attributes.add(next.next());
			}
			System.out.println("Read " + attributes.size() + " attributes");


			List<Instance> instances = getInstances(scan);
			allInsts.addAll(instances);
			root = buildTree(instances, new ArrayList<String>(attributes));
			System.out.println(attributes.size());
		}catch(Exception e){e.printStackTrace();}

		root.report();
	}
	public List<Instance> getInstances(Scanner din){
		List<Instance> instances = new ArrayList<Instance>();
		try{
			String ln;
			while (din.hasNext()){
				Scanner line = new Scanner(din.nextLine());
				instances.add(new Instance(categoryNames.indexOf(line.next()),line));
			}
			System.out.println("Read " + instances.size()+" instances");
		}catch(Exception e){e.printStackTrace();}
		return instances;

	}

	public boolean isPure(List<Instance> instances){
		Instance firstInstance = instances.get(0);

		for(Instance i:instances){
			if(i.getCategory() != firstInstance.getCategory()){
				return false;
			}
		}
		return true;
	}

	//Build tree algorithm
	public Node buildTree(List<Instance> instances, List<String> attributes){
		if(instances.isEmpty()){
			MostFrequentCat mostFreq = findMostFrequent(allInsts);
			return new Leaf(mostFreq.category,mostFreq.probability,mostFreq.num);
		}
		//if instances are pure
		if(isPure(instances)){
			return new Leaf(instances.get(0).getCategory(), 1, instances.size());
		}
		if(attributes.isEmpty()){
			MostFrequentCat mostFreq = findMostFrequent(allInsts);
			return new Leaf(mostFreq.category,mostFreq.probability,mostFreq.num);
		}
		else{//find best attribute
			double bestAtt = Double.MAX_VALUE; //purity value
			List<Instance> bestInstsTrue = null;
			List<Instance> bestInstsFalse = null;
			String bestAttname = "";
			for(String attr: attributes){
				//Get instances for which the attribute is true
				List<Instance> trueinstances = new ArrayList<Instance>(getInstances(true, instances, attributes.indexOf(attr)));
				//Get instances for which the attribute is false
				List<Instance> falseinstances = new ArrayList<Instance>(getInstances(false, instances, attributes.indexOf(attr)));
				double temp = calculateImpurity(trueinstances,falseinstances);
				if(temp<bestAtt){
					bestAtt= temp;
					bestAttname = attr;
					bestInstsTrue = trueinstances;
					bestInstsFalse = falseinstances;
				}
			}

			List<String> temp = new ArrayList<String>();
			temp.addAll(attributes);
			temp.remove(bestAttname); //attributes - bestAtt

			Node left = buildTree(bestInstsTrue, temp);
			Node right = buildTree(bestInstsFalse, temp);

			return new SubNode(bestAttname, left,right);

		}
	}


	private class MostFrequentCat{
		public int category;
		public int num;
		public double probability;
		public MostFrequentCat(int category,double probability,int num){
			this.probability = probability;
			this.category = category;
			this.num = num;
		}
	}
	public MostFrequentCat findMostFrequent(List<Instance> instances){
		List<Integer> categoryNum = new ArrayList<Integer>();
		categoryNum.add(0);
		categoryNum.add(0);
		for(Instance i: instances){
			categoryNum.set(i.category, categoryNum.get(i.category)+1);
		}	
		int max=0;int maxPos=0;
		for(int i = 0;i<categoryNum.size();i++){
			if(categoryNum.get(i)>max){
				max = categoryNum.get(i);
				maxPos = i;
			}
		}
		return new MostFrequentCat(maxPos,((double)max)/(categoryNum.size()),max);
	}

	//calculates and returns the lowest impurity value of the two sets
	public double calculateImpurity(List<Instance> trueInstances, List<Instance> falseInstances){
		double impurityTrue=0;
		double impurityFalse=0;

		int categoryTrue = 0;
		for(Instance i: trueInstances){
			if(i.getCategory()==1){
				categoryTrue ++;
			}
		}
		impurityTrue = ((double)categoryTrue/(double)trueInstances.size())*(((double)trueInstances.size()-(double)categoryTrue)/(double)trueInstances.size());

		categoryTrue=0;
		for(Instance i: falseInstances){
			if(i.getCategory()==0){
				categoryTrue++;
			}
		}
		impurityFalse = ((double)categoryTrue/(double)falseInstances.size())*((falseInstances.size()-categoryTrue)/(double)falseInstances.size());
		if(impurityTrue<impurityFalse){
			return impurityTrue;
		}
		else{
			return impurityFalse;
		}
	}

	//TESTED AND CORRECT
	public ArrayList<Instance> getInstances(boolean b, List<Instance> instances, int attr){
		ArrayList<Instance> insts = new ArrayList<Instance>();
		for(Instance i : instances){
			if(i.getAtt(attr) == b){
				insts.add(i);
			}
		}
		return insts;
	}


	private class SubNode implements Node{
		private String name; //Best attribute
		private Node left;
		private Node right;

		public SubNode(String name, Node left, Node right){
			this.name = name;
			this.left = left;
			this.right = right;
		}
		public String getName() {
			return name;
		}
		public void report() {
			System.out.println(name);
			left.report();
			right.report();
		}
	}

	private class Leaf implements Node{
		private int category;
		private double probability;
		private int number;
		public Leaf(int name, double probability, int number){
			this.category = name;
			this.probability = probability;
			this.number = number;
		}
		public int getCategory() {
			return category;
		}
		public double getProbability() {
			return probability;
		}
		public void report() {
			System.out.println("Class: " + categoryNames.get(category) + " " + number + " " +probability);
		}
	}

	private class Instance {

		private int category;
		private List<Boolean> vals;

		public Instance(int cat, Scanner s){
			category = cat;
			vals = new ArrayList<Boolean>();
			while (s.hasNextBoolean()) vals.add(s.nextBoolean());
		}

		public boolean getAtt(int index){
			return vals.get(index);
		}

		public int getCategory(){
			return category;
		}

		public String toString(){
			StringBuilder ans = new StringBuilder(categoryNames.get(category));
			ans.append(" ");
			for (Boolean val : vals)
				ans.append(val?"true  ":"false ");
			return ans.toString();
		}
	}

	public interface Node{
		void report();
	}
	
	public static void main(String args[]){
		new DTLearning2();
	}
}
