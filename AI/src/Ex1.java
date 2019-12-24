import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;

/**
 * @author  Eli Ruvinov
 */
public class Ex1 {
	public static BayesianNetwork BN;
	/*
	 * this function is the main function in this project
	 */
	public static void main(String[] args) throws IOException  {
		boolean Dbag = false; // true || false
		File input = new File("input.txt");
		Ex1.BN = new BayesianNetwork();
		BufferedReader br = new BufferedReader(new FileReader(input)); 
		Ex1.bagining(br);
		Ex1.verticesBuild(br);
		File OutputFile = new File("output.txt");
		OutputFile.createNewFile();
		FileWriter fw = new FileWriter(OutputFile);
		PrintWriter pw = new PrintWriter(fw);
		Ex1.writeOutputFile(br, pw);
		pw.close();
		br.close();
		if(Dbag) {
			Iterator<Node> it = Ex1.BN.iteretor();
			while (it.hasNext()) {
				Node temp = it.next();
				System.out.println(temp.name+" CPT:");
				Node.printCPT(temp.name);
			}
		}
	}
	
	// 
	public static void bagining(BufferedReader br) throws IOException {
		String st;
		st = br.readLine();
		if( (st = br.readLine()) != null && !(st.length() <= 12) && (String) st.subSequence(0, 11) != "Variables: ") {  
			try {
				String[] verticesNames = ((String) st.subSequence(11, st.length())).split(",");
				for (int i = 0; i < verticesNames.length; i++) {
					Node n1 = new Node(verticesNames[i]);
					Ex1.BN.put(verticesNames[i], n1);
				}
			} catch (RuntimeException e) {
				throw new RuntimeException("The string: "+ st +" isn't represent Bayesian Network vertices.");
			}
		} 
		st = br.readLine();
	}

	/**
	 * This function is building all the nodes from the input file
	 * and their CTP 
	 * @param br - the BufferedReader of the input file
	 * @throws IOException
	 */
	public static void verticesBuild(BufferedReader br) throws IOException {
		for (int i = 0; i < Ex1.BN.size(); i++) {
			nodeInit(br);
		}
		Iterator<Node> it = Ex1.BN.iteretor(); 
		while (it.hasNext()) {
			Node tempNode = it.next();
			BufferedReader brTemp = new BufferedReader(new FileReader(tempNode.cptText));
			tempNode.buildSwithbyVal();
			tempNode.getCPT(brTemp);
			brTemp.close();
			tempNode.cptText.delete();
			tempNode.cptFactor = tempNode.CTPtoFactor();
		}
	}
	
	/**
	 * This function is initialize node Fields
	 * @param br - the BufferedReader of the input file
	 * @throws IOException
	 */
	private static void nodeInit(BufferedReader br) throws IOException {
		Node tempNode = nodeHeaderHandle(br);
		CPTTextFile(br, tempNode);	
	}
	
	private static void CPTTextFile(BufferedReader br, Node tempNode) throws IOException {
		String st = br.readLine();
		if (st.contains("CPT:")) {
			tempNode.cptText = new File(tempNode.name+" CPT");
			FileWriter fw = new FileWriter(tempNode.cptText);
			PrintWriter pw = new PrintWriter(fw);
			st = br.readLine();
			while (!st.isEmpty()) {
				pw.println(st);
				(st = br.readLine()).replace(" ", "");
			}
			pw.close();
		} 
	}
	
	private static Node nodeHeaderHandle(BufferedReader br) throws IOException {
		boolean Dbag = false; // true || false
		String st ;
		st = br.readLine();
		Node tempNode = null;
		
		//Updates tempNode
		if(st.substring(0,4).equals("Var ") ) {
			if(Dbag){System.out.println(st.substring(4,st.length()));}
			tempNode = Ex1.BN.get(st.substring(4,st.length()));
		} 
		
		//Updates tempNode varValues
		st = br.readLine();
		if(st.substring(0,8).equals( "Values: ")) {
			String[] VarValues = st.substring(8,st.length()).split(",");
			if(Dbag){System.out.println(VarValues[0]);}
			tempNode.VarValues = VarValues;
		}
		
		//Updates tempNode ParentsNames and numOfParents		
		st = br.readLine();
		String[] ParentsNames = null;
		if (st.substring(0,9).equals("Parents: ")) {
			ParentsNames = st.substring(9,st.length()).split(",");
			tempNode.ParentsNames = ParentsNames;
			tempNode.numOfParents = ParentsNames.length;
		}
		if(tempNode.numOfParents == 1 && ParentsNames[0].equals("none")) 
			tempNode.numOfParents = 0;
		
		// adding tempNode as son to his Node parents
		for (int i = 0; i < ParentsNames.length && tempNode.numOfParents != 0; i++) {
//			System.out.println(ParentsNames[i]);
			Ex1.BN.get(ParentsNames[i]).SonsNames.add(tempNode.name);
		}
		
		// adding a list of Nodes parents
		tempNode.Parents = new Node[tempNode.numOfParents];
		for (int i = 0; i < tempNode.numOfParents; i++) {
			tempNode.Parents[i] = Ex1.BN.get(ParentsNames[i]);
		}
		return tempNode;
	}
	
	/**
	 * This function is writing the output File 
	 * @param br - the BufferedReader of the input file
	 * @param pw - the PrintWriter of the output file
	 * @throws IOException
	 */
	public static void writeOutputFile(BufferedReader br, PrintWriter pw) throws IOException {
		Algorithms.numOfPlus = new Integer(0);
		Algorithms.numOfMul = new Integer(0);
		String st = br.readLine();
		if(!st.contains("Queries")) {
			throw new RuntimeException("This isn't the Queries part.");
		}
		st = br.readLine();
		
		while ((st = br.readLine()) != null) {
			if(st.contains("P(") && st.contains(")")) {
				pw.println( Algorithms.VariableElimination(st) );
			} else if( st.contains("-") && st.contains("|") ) {
				pw.println( Algorithms.BayesBall(st) );
			}
		}
	}
	
	public static < E > void printArray(E[] inputArray ) {
		for(E element : inputArray) {
	         System.out.printf("%s ", element);
	      }
	      System.out.println();
	}
	
	public static < E > void printDobArray(E[] inputArray ) {
		for(E element : inputArray) {
	         System.out.printf("%s ", element);
	      }
	      System.out.println();
	}


	public static void printDubArray(double[] probability) {
		for(double element : probability) {
	         System.out.printf("%s ", element);
	      }
	      System.out.println();
	}
	
	public static void printIntArray(int[] probability) {
		for(int element : probability) {
	         System.out.printf("%s ", element);
	      }
	      System.out.println();
	}
}
