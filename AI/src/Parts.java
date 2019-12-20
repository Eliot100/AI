import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;

/**
 * @author Eli Ruvinov
 * In this class we write 3 part of the work on the input file 
 */
public class Parts {
	/**
	 * In this function we insert the Nodes to our Bayesian Network
	 * @param br - the BufferedReader of the input file
	 * @param BN - our Bayesian Network
	 * @throws IOException
	 */
	public static void bagining(BufferedReader br) throws IOException {
		String st;
		boolean Dbag = false; // false  true
		if((st = br.readLine()) == null || !st.contains("Network")) { 
			throw new RuntimeException("The file input: isn't a representation of Bayesian Network" );
		}
		if( (st = br.readLine()) != null && !(st.length() <= 12) && (String) st.subSequence(0, 11) != "Variables: ") {   //
			if(Dbag){System.out.println(st.subSequence(0, 11));}
			try {
				String[] verticesNames = ((String) st.subSequence(11, st.length())).split(",");
				for (int i = 0; i < verticesNames.length; i++) {
					Node n1 = new Node(verticesNames[i]);
					n1.place = i;
					Ex1.BN.put(verticesNames[i], n1);
				}
				if(Dbag){System.out.println(Ex1.BN.toSrting());}
			} catch (RuntimeException e) {
				throw new RuntimeException("The string: "+ st +" isn't represent Bayesian Network vertices.");
			}
		} else {
			if(Dbag){System.out.println(st.subSequence(0, 11));}
			throw new RuntimeException("The file input isn't a represent of Bayesian Network" );
		}
		st = br.readLine();
	}

	/**
	 * This function is building all the nodes from the input file
	 * and their CTP 
	 * @param br - the BufferedReader of the input file
	 * @param BN - our Bayesian Network
	 * @throws IOException
	 */
	public static void verticesBuild(BufferedReader br) throws IOException {
		for (int i = 0; i < Ex1.BN.size(); i++) {
			nodeInit(br);
		}
		Iterator<Node> it = Ex1.BN.iteretor(); 
		while (it.hasNext()) {
			Node tempNode = it.next();
			BufferedReader brTemp = new BufferedReader(new FileReader( tempNode.cptText));
			tempNode.getCPT(brTemp);
			brTemp.close();
			tempNode.cptText.delete();
		}
	}
	
	/**
	 * This function is initialize node Fields
	 * @param br - the BufferedReader of the input file
	 * @param BN - our Bayesian Network
	 * @throws IOException
	 */
	private static void nodeInit(BufferedReader br) throws IOException {
		boolean Dbag = false; // true || false
		String st ;
		st = br.readLine();
		Node tempNode;
		if(st.substring(0,4).equals("Var ") ) {
			if(Dbag){System.out.println(st.substring(4,st.length()));}
			tempNode = Ex1.BN.get(st.substring(4,st.length()));
		} else 
			throw new RuntimeException("This isn't the right row. (1):\n"+st);
		
		st = br.readLine();
		if(st.substring(0,8).equals( "Values: ")) {
			String[] VarValues = st.substring(8,st.length()).split(",");
			if(Dbag){System.out.println(VarValues[0]);}
			tempNode.VarValues = new String[VarValues.length];
			for (int i = 0; i < VarValues.length; i++) {
				tempNode.VarValues[i] = VarValues[i];
			}
		} else 
			throw new RuntimeException("This isn't the right row. (2):\n"+st);
		
		st = br.readLine();
		String[] ParentsNames = null;
		if (st.substring(0,9).equals("Parents: ")) {
			ParentsNames = st.substring(9,st.length()).split(",");
			tempNode.ParentsNames = ParentsNames;
			tempNode.numOfParents = ParentsNames.length;
		} else 
			throw new RuntimeException("This isn't the right row. (3):\n"+st);
		
		if(tempNode.numOfParents == 1 && ParentsNames[0].equals("none")) {
			tempNode.numOfParents = 0;
		}
		tempNode.Parents = new Node[tempNode.numOfParents];
		for (int i = 0; i < tempNode.numOfParents; i++) {
			tempNode.Parents[i] = Ex1.BN.get(ParentsNames[i]);
		}
		st = br.readLine();
		if(Dbag){System.out.println("Number of parents = "+tempNode.numOfParents);}
		if (st.contains("CPT:")) {
			tempNode.cptText = new File(tempNode.name+" CPT");
			FileWriter fw = new FileWriter(tempNode.cptText);
			@SuppressWarnings("resource")
			PrintWriter pw = new PrintWriter(fw);
			st = br.readLine();
			while (!st.isEmpty()) {
				pw.println(st);
				(st = br.readLine()).replace(" ", "");
			}
			pw.close();
		} else 
			throw new RuntimeException("This isn't the right row. (4):\n"+st);
		
	}
	
	/**
	 * This function is writeing the output File 
	 * @param br - the BufferedReader of the input file
	 * @param BN - our Bayesian Network
	 * @param pw - the PrintWriter of the output file
	 * @throws IOException
	 */
	public static void writeOutputFile(BufferedReader br, PrintWriter pw) throws IOException {
		String st = br.readLine();
		if(!st.contains("Queries")) {
			throw new RuntimeException("This isn't the Queries part.");
		}
		st = br.readLine();

		while ((st = br.readLine()) != null) {
			if(st.contains("P(") && st.contains(")")) {
				pw.println( Algorithms.VariableElimination( st) );
			} else if( st.contains("-") && st.contains("|") ) {
				pw.println( Algorithms.BayesBall( st) );
			}
		}
	}
}
