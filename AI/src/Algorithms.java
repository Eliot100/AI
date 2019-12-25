import java.util.ArrayList;
import java.util.Iterator;


/**
 * @author Eli and Dvir
 * in this class we write the Variable Elimination algorithm
 */
public class Algorithms {
	public static int numOfMul;
	public static int numOfPlus;

	/**
	 * @param st - the String query
	 * @return String that contain the query answer, comma
	 * with The number of connection operations required by the algorithm to answer the query, comma, 
	 * and then the number of multiplication operations required by the algorithm.
	 * like:  
	 * 0.28417,7,16
	 * 0.84902,7,12
	 */
	public static String VariableElimination( String st) {
		Algorithms.numOfPlus = 0;
		Algorithms.numOfMul = 0;
		query q = query.init(st);
		if(q.flagIfImmediate) {
			double prob = getfinalProbability(q.target.CTPtoFactor(), q);
			prob  = round(prob, 5);
			System.out.println(String.format("%.5f", prob)+",0,0");
			return (String.format("%.5f", prob)+","+Algorithms.numOfPlus+","+Algorithms.numOfMul);
		}
		Factor[] arrayF = getAllNeededFactors(q);
//		Factor[] arrayF = arrayF_Init(q);
		for (int i = 0; i < arrayF.length; i++) 
			arrayF[i].printFactor();
		for (int i = 0; i < q.toEliminate.length; i++) 
			arrayF = JoinAndEliminate( q.toEliminate[i], arrayF);
		Factor temp = arrayF[0];
		if(arrayF.length != 1) {
			for (int i = 1; i < arrayF.length; i++) 
				temp = join(temp, arrayF[i]);
		}
		temp.normalize();
		double prob = getfinalProbability(temp, q);
		prob  = round(prob, 5);
		System.out.println(String.format("%.5f", prob)+","+Algorithms.numOfPlus+","+Algorithms.numOfMul);
		return (String.format("%.5f", prob)+","+Algorithms.numOfPlus+","+Algorithms.numOfMul);
	}

	// Rounds out the double number gotten (Was taken from Geeks For Geeks)  
	public static double round(double value, int places) {
		if (places < 0) throw new IllegalArgumentException();
		long factor = (long) Math.pow(10, places);
		value = value * factor;
		long tmp = Math.round(value);
		return (double) tmp / factor;
	}

	// Returns a double that represents the final probability of the query.
	private static double getfinalProbability(Factor temp, query q) {
		boolean flag;
		for (int i = 0; i < temp.matrix.length; i++) {
			flag = true;
			for (int j = 0; j < temp.matrix[0].length; j++) {
				if(temp.dependent[j].equals(q.target.name)) {
					if(!temp.matrix[i][j].equals(q.valTarget))
						flag = false;
				}
				else {
					for (int j2 = 0; j2 < q.GivenNodes.length; j2++) {
						if(temp.dependent[j].equals(q.GivenNodes[j2])) {
							if(!temp.matrix[i][j].equals(q.GivenValsByNode[j2]))
								flag = false;
						}
					}
				}
			}
			if (flag)
				return temp.probability[i];
		}
		return 0;
	}

	// Returns a Factor array containing all the lineage Factors.
	private static Factor[] getAllNeededFactors(query q) {
		ArrayList<String> lineage = new ArrayList<String>();
		lineage.add(q.target.name);
		for (int i = 0; i < q.GivenNodes.length; i++) {
			lineage.add(q.GivenNodes[i]);
		}
		for (int i = 0; i < lineage.size(); i++) {
			for (int j = 0; j < Ex1.BN.get(lineage.get(i)).numOfParents; j++) {
				if(!lineage.contains(Ex1.BN.get(lineage.get(i)).ParentsNames[j])) {
					lineage.add(Ex1.BN.get(lineage.get(i)).ParentsNames[j]);
				}

			}
		}
		Factor[] arrayF = new Factor[lineage.size()];
		for (int i = 0; i < arrayF.length; i++) {
			arrayF[i] = Ex1.BN.get(lineage.get(i)).cptFactor;
//			arrayF[i].EliminateByTargetVal( q.target.name, q.valTarget); 
			
			
			
//			for (int j = 0; j < q.GivenNodes.length; j++) {
//				if(Factor.contains(arrayF[i].dependent, q.GivenNodes[j])) {
//					arrayF[i].removeGivens(q.GivenNodes[j], q.GivenValsByNode[j]);;
//					arrayF[i].removeUnwantedDependecies();
//					arrayF[i].makeMatrix();
//					arrayF[i].
//				}
//			}
//			for (int j = 0; j < q.GivenNodes.length; j++) {
//				if(Factor.contains(arrayF[i].dependent, q.GivenNodes[j]))
//					arrayF[i].removeGivens(q.GivenNodes[j], q.GivenValsByNode[j]);
//			}
//			arrayF[i].removeUnwantedDependecies();
//			arrayF[i].makeMatrix();
//			arrayF[i].probabilityElimination(f1, q.GivenNodes[j]);
		}
		return arrayF;
	}

	private static class query {
		public Node target;
		public String valTarget;
		public String[] toEliminate;
		public String[] GivenNodes;
		public String[] GivenValsByNode;
		public boolean flagIfImmediate;

		/**
		 * 
		 * @param st - The String to make the query out of
		 * @return a query object made from the String gotten.
		 */
		public static query init(String st) {
			query q = new query();
			boolean flag1 = true;
			boolean flag2 = true;
			boolean flag3 = true;
			int indexLine = 0, indexEndP = 0, indexeq = 0;
			for (int i = 0; flag3  ; i++) {
				if (flag1 && st.charAt(i) == '=') {
					indexeq = i;
					flag1 =  false;
				}
				else if (!flag1 && flag2 && st.charAt(i)== '|' ) {
					indexLine = i;
					flag2 =  false;
				} 
				else if (!flag2 && st.charAt(i)== ')') {
					indexEndP = i;
					flag3 =  false;
				}
			}
			q.target = Ex1.BN.get(""+st.substring(2, indexeq));
			q.valTarget = st.substring(indexeq+1, indexLine);
			String[] GivenPs = st.substring(indexLine+1, indexEndP).split(",");
			q.GivenNodes = new String[GivenPs.length];
			q.GivenValsByNode = new String[GivenPs.length];
			for (int i = 0; i < GivenPs.length; i++) {
				String[] Given = GivenPs[i].split("=");
				q.GivenNodes[i] = Given[0];
				q.GivenValsByNode[i] = Given[1];
			}
			boolean flag = true;
			for (int i = 0; flag && i < q.GivenNodes.length; i++) {
				if(Factor.contains(q.target.ParentsNames, q.GivenNodes[i]))
					;
				else
					flag = false;
			}
			if(flag && q.GivenNodes.length == q.target.numOfParents) 
				q.flagIfImmediate = true;	
			else {
				q.toEliminate = st.substring( indexEndP+2, st.length() ).split("-");
				q.flagIfImmediate = false;
			}
			return q;
		}
	}

	/**
	 * Joins and eliminates all the related Factors
	 * @param toEliminate - The Factor to eliminate.
	 * @param arrayF - Array of all the Factors related
	 * @return A final Factor after joining and eliminating 
	 */
	private static Factor[] JoinAndEliminate(String toEliminate, Factor[] arrayF) {
		boolean[] flags = ifEliminate(arrayF, toEliminate);
		int cont = 0;
		for (int j = 0; j < flags.length; j++) { 
			if(flags[j])
				cont++;
		}
		Factor[] Factors2Eliminate = new Factor[cont];
		int x = 1;
		if(cont == 0) 
			x = 0;
		Factor[] FactorsNot2Eliminate = new Factor[flags.length-cont+x];
		cont = 0;
		int cont2 = 0;
		for (int j = 0; j < arrayF.length; j++) {
			if(flags[j]) {
				Factors2Eliminate[cont] = arrayF[j];
				cont++;
			}
			else {
				FactorsNot2Eliminate[cont2] = arrayF[j];
				cont2++;
			}
		}
		if(Factors2Eliminate.length > 0) {
			FactorsNot2Eliminate[cont2] = joinAll(Factors2Eliminate, toEliminate);
		}
		return FactorsNot2Eliminate;
	}


	// Joins all the related Factors.
	public static Factor joinAll(Factor[] farr, String toEliminate){
		Factor f1 = farr[0];

		for (int i = 1; i < farr.length; i++) {
			f1 = join(f1, farr[i]);

		}
		f1 = f1.Elimination( toEliminate);
		return f1;
	}

	// Gets two Factors, joins them and returns the joined Factor.
	private static Factor join(Factor f1, Factor f2) {
		f2.printFactor();
		f1.removeUnwantedDependecies();
		f2.removeUnwantedDependecies();
		Factor newfactor = new Factor();
		newfactor.dependent = get_unique(f1.dependent, f2.dependent);
		newfactor.switchByVal = Node.makeSwitch(newfactor.dependent);
		newfactor.makeMatrix();
		newfactor.makeProbability(f1, f2);
		return newfactor;
	}

	/**
	 * 
	 * @param s1 - First String array.
	 * @param s2 - Second String array
	 * @param size - The size of the new String array that is returned.
	 * @return A String array that contains all unique values in the two String arrays
	 */
	private static String[] get_unique(String[] s1, String[] s2) {
		int size0 = s1.length;
		for (int i = 0; i < s2.length; i++) {
			if(!Factor.contains(s1, s2[i]))
				size0++;
		}
		String[] newS = new String[size0];
		int counter = 0;
		for ( ; counter < s1.length; counter++) 
			newS[counter] = s1[counter];

		for (int i = 0; i < s2.length; i++) {
			if(!Factor.contains(s1,s2[i])) {
				newS[counter+i] = s2[i];
			}
			else 
				counter--;
		}
		return newS;
	}

	/**
	 * We dosn't need to do this algorithm at the moment
	 * @param st - the String query
	 * @return String - the query answer.
	 */
	public static String BayesBall( String st) {
		String ans ="";
		return ans;
	}

	private static boolean[] ifEliminate( Factor[] allFactors, String nodeNameToEliminate) {
		boolean[] flags = new boolean[allFactors.length];
		for (int i = 0; i < allFactors.length; i++) {
			for (int j = 0; !flags[i] && j < allFactors[i].dependent.length; j++) {
				if(allFactors[i].dependent[j].equals(nodeNameToEliminate))
					flags[i] = true;
			}
		}
		return flags;
	}
	//	// Creates the switchByVal array used in joining.
	//	public static int[] makeSwitch(String[] dependent, int size) {
	//		int[] switchByVal = new int[size];
	//		int switch0 = 1;
	//		for (int i = switchByVal.length-1; i >= 0; i--) {
	//			switchByVal[i] = switch0;
	//			switch0 *= Ex1.BN.get(dependent[i]).VarValues.length;
	//		}
	//		return switchByVal;
	//	}
	//	
	// Creates the arrayF array from the gotten query.
	@SuppressWarnings("unused")
	private static Factor[] arrayF_Init(query q) {
		Factor[] arrayF = new Factor[Ex1.BN.size()];
		Iterator<Node> it = Ex1.BN.iteretor();
		int cont = 0;
		while(it.hasNext()) {
			Node tempNode = it.next();
			arrayF[cont] = tempNode.CTPtoFactor();
//			if(Factor.contains(arrayF[cont].dependent,q.target.name))
//				arrayF[cont].removeGivens(q.target.name, q.valTarget);
//			for (int i = 0; i < q.GivenNodes.length; i++) {
//				if(Factor.contains(arrayF[cont].dependent, q.GivenNodes[i]))
//					arrayF[cont].removeGivens(q.GivenNodes[i], q.GivenValsByNode[i]);
//			}
//			arrayF[cont].removeUnwantedDependecies();
//			arrayF[cont].makeMatrix();
			cont++;
		}
		return arrayF;
	}
}
