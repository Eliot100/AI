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
		// Making variables for later use (From the input file queries) 
		query q = query.init(st);
		Factor[] arrayF = getAllNeededFactors(q);
//		System.out.println("Printing: ");
//		for (int i = 0; i < arrayF.length; i++) {
//			arrayF[i].printFactor();
//		}
		Factor[] arrayF2 = arrayF_Init(q);
//		for (int i = 0; i < arrayF.length; i++) {
//			arrayF[i].printFactor();
//		}
//		for (int i = 0; i < arrayF2.length; i++) {
//			arrayF2[i].printFactor();
//		}
		for (int i = 0; i < q.toEliminate.length; i++) {
			arrayF = JoinAndEliminate( q.toEliminate[i], arrayF);
		}
//		System.out.println("Printing2: "+ arrayF.length);
//		for (int i = 0; i < arrayF.length; i++) {
//			System.out.println(i+"   "+arrayF[i]);
//		}
		Factor temp = arrayF[0];
		if(arrayF.length != 1) {
			for (int i = 1; i < arrayF.length; i++) {
				System.out.println("***  "+i+" "+arrayF[i]);
				temp = join(temp, arrayF[i]);
			}
		}
		temp.normalize();
//		temp.printFactor();
		double prob = getfinalProbability(temp, q);
		prob  = round(prob, 5);
		System.out.println(String.format("%.5f", prob)+","+Algorithms.numOfPlus+","+Algorithms.numOfMul);
		return (String.format("%.5f", prob)+","+Algorithms.numOfPlus+","+Algorithms.numOfMul);
	}
	
	public static double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();

	    long factor = (long) Math.pow(10, places);
	    value = value * factor;
	    long tmp = Math.round(value);
	    return (double) tmp / factor;
	}

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

	private static Factor[] getAllNeededFactors(query q) {
		ArrayList<String> lineage = new ArrayList<String>();
		lineage.add(q.target.name);
		for (int i = 0; i < q.GivenNodes.length; i++) {
			lineage.add(q.GivenNodes[i]);
		}
		for (int i = 0; i < lineage.size(); i++) {
//			System.out.println(Ex1.BN.get(lineage.get(i)));
			for (int j = 0; j < Ex1.BN.get(lineage.get(i)).numOfParents; j++) {
				if(!lineage.contains(Ex1.BN.get(lineage.get(i)).ParentsNames[j])) {
					lineage.add(Ex1.BN.get(lineage.get(i)).ParentsNames[j]);
				}
				
			}
		}
		Factor[] arrayF = new Factor[lineage.size()];
		for (int i = 0; i < arrayF.length; i++) {
			arrayF[i] = Ex1.BN.get(lineage.get(i)).cptFactor;
//			Ex1.BN.get(lineage.get(i)).cptFactor.printFactor();
		}
		return arrayF;
	}

	private static Factor[] arrayF_Init(query q) {
		Factor[] arrayF = new Factor[Ex1.BN.size()];
		Iterator<Node> it = Ex1.BN.iteretor();
		int cont = 0;
		while(it.hasNext()) {
			Node tempNode = it.next();
			arrayF[cont] = tempNode.CTPtoFactor();
			if(Factor.contains(arrayF[cont].dependent,q.target.name))
				arrayF[cont].removeGivens(q.target.name, q.valTarget);
//			for (int i = 0; i < q.GivenNodes.length; i++) {
//				if(Factor.contains(arrayF[cont].dependent, q.GivenNodes[i]))
//					arrayF[cont].removeGivens(q.GivenNodes[i], q.GivenValsByNode[i]);
//			}
			removeUnwantedDependecies(arrayF[cont]);
			arrayF[cont].makeMatrix(findNumOfRows(arrayF[cont]));
			cont++;
		}
		return arrayF;
	}

	private static class query {
		public Node target;
		public String valTarget;
		public String[] toEliminate;
		public String[] GivenNodes;
		public String[] GivenValsByNode;
		
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
			q.toEliminate = st.substring( indexEndP+2, st.length() ).split("-");
			q.GivenNodes = new String[GivenPs.length];
			q.GivenValsByNode = new String[GivenPs.length];
			for (int i = 0; i < GivenPs.length; i++) {
				String[] Given = GivenPs[i].split("=");
				q.GivenNodes[i] = Given[0];
				q.GivenValsByNode[i] = Given[1];
			}
			return q;
		}
	}
	
	private static Factor[] JoinAndEliminate(String toEliminate, Factor[] arrayF) {
		boolean[] flags = ifEliminate(arrayF, toEliminate);
//		for (int i = 0; i < flags.length; i++) {
//			System.out.println(flags[i]);
//		}
		int cont = 0;
		for (int j = 0; j < flags.length; j++) { 
			if(flags[j])
				cont++;
		}
		Factor[] Factors2Eliminate = new Factor[cont];
		Factor[] FactorsNot2Eliminate = new Factor[flags.length-cont+1];
		cont = 0;
		int cont2 = 0;
		for (int j = 0; j < arrayF.length; j++) {
			if(cont == 2 || cont2 == 2) 
			{
				System.out.println("j = "+j);
			}
				
			if(flags[j]) {
				Factors2Eliminate[cont] = arrayF[j];
				cont++;
			}
			else {
				FactorsNot2Eliminate[cont2] = arrayF[j];
				cont2++;
			}
		}
//		System.out.println(Factors2Eliminate.length);
//		System.out.println("Printing3: ");
//		for (int i = 0; i < Factors2Eliminate.length; i++) {
//			Factors2Eliminate[i].printFactor();
//		}
		
		FactorsNot2Eliminate[cont2] = joinAll(Factors2Eliminate, toEliminate);
		
		return FactorsNot2Eliminate;
	}
	
	public static double sumArr(double[] arr) 
	{
		double sum = 0; 
		for (int i = 0; i < arr.length; i++)
		{
			sum+=arr[i];
		}
		return sum;
	}
	
	public static Factor joinAll(Factor[] farr, String toEliminate){
		Factor f1 = null;
		if(farr.length > 0 ) 
		{
			f1 = farr[0];
			//System.out.println("f[0]");
			//f1.printFactor();
		}
		else
			return null;
		System.out.println("farr[2] = ");
//		farr[2].printFactor();	
		for (int i = 1; i < farr.length; i++) {
			f1 = join(f1, farr[i]);
			//System.out.println("hey "+ i);
			
		}
		f1.printFactor();
//		System.out.println(toEliminate);
		f1 = f1.Elimination( toEliminate);
//		f1.printFactor();
		return f1;
	}

	private static Factor join(Factor f1, Factor f2) {
		removeUnwantedDependecies(f1);
		removeUnwantedDependecies(f2);
		Factor newfactor = new Factor();
		newfactor.dependent = get_unique(f1.dependent, f2.dependent, Factor.count_unique(f1.dependent, f2.dependent));
		newfactor.switchByVal = makeSwitch(newfactor.dependent, Factor.count_unique(f1.dependent, f2.dependent));
		int numOfRows = findNumOfRows(newfactor);
		newfactor.makeMatrix(numOfRows);
		newfactor.probability = makeProbability(f1, f2, numOfRows, newfactor);
//		newfactor.printFactor();
		return newfactor;
	}
	
	public static int findNumOfRows(Factor newfactor) {
		for (int i = 0; i < newfactor.switchByVal.length; i++) {
			if(newfactor.switchByVal[i] != 0 ) {
				return newfactor.switchByVal[i]*Ex1.BN.get(newfactor.dependent[i]).VarValues.length;
			}
			else
				continue;
		}
		return 1;
	}

	private static double[] makeProbability(Factor f1, Factor f2, int ProbabilitySize, Factor newfactor) {
		double[] probability = new double[ProbabilitySize];
		for (int i = 0; i < probability.length; i++) {
			probability[i] = ProbByRow(i, f1, newfactor)*ProbByRow(i, f2, newfactor);
			Algorithms.numOfMul ++;
		}
		return probability;
	}
	
	private static double ProbByRow(int rowProb, Factor f, Factor newfactor )
	{
		if(f.probability.length == 1 && f.dependent.length == 0) {
			return f.probability[0];
		}
		int rowOldFactor = 0, colOldFactor = 0, counter = 0;//, colNewFactor = 0
		for (; rowOldFactor < f.probability.length; rowOldFactor++) {
			for (; colOldFactor < f.dependent.length; colOldFactor++) {
				if(newfactor.matrix[rowProb][Factor.get_index_by_value(newfactor.dependent,f.dependent[colOldFactor])]!=f.matrix[rowOldFactor][colOldFactor])
					break;
				else
					counter++;

			}
			if(counter == f.dependent.length)
				return f.probability[rowOldFactor];
		}
		return 0;
	}
	
	private static int[] makeSwitch(String[] dependent, int size) {
		int[] switchByVal = new int[size];
		int switch0 = 1;
		for (int i = switchByVal.length-1; i >= 0; i--) {
			switchByVal[i] = switch0;
			switch0 *= Ex1.BN.get(dependent[i]).VarValues.length;
		}
		return switchByVal;
	}
	
	private static void removeUnwantedDependecies(Factor f) 
	{
		boolean[] flags = getZerosSwitch(f);
		int size = 0;
		for (int i = 0; i < flags.length; i++) {
			if(!flags[i])
				size++;
		}
		
		String[] dependents = new String[size];
		int counter = 0;
		for (int j = 0; j < f.dependent.length; j++) 
			if(!flags[j]) {
				dependents[counter] = f.dependent[j];
				counter++;
			}
		
		f.switchByVal = getNonZeroArr(f, flags);
		for (int i = 0; i < f.switchByVal.length; i++) {
			System.out.println(f.switchByVal[i]);
		}
		f.dependent = dependents;
	} 
	
	private static int[] getNonZeroArr(Factor f1, boolean[] flags) {
		int counter = 0;
		for (int i = 0; i < flags.length; i++) {
			if(!flags[i]) 
				counter++;
		}
		int[] switchbByVal = new int[counter];
		counter = 0;
		for (int i = 0; i < flags.length; i++) {
			if(!flags[i]) {
				switchbByVal[counter] = f1.switchByVal[i];
				counter++;
			}
		}
		return switchbByVal;
	}
	
	private static boolean[] getZerosSwitch(Factor f1) {
		System.out.println(f1);
		boolean[] zeroCols = new boolean[f1.switchByVal.length];
		for (int i = 0; i < f1.switchByVal.length; i++) {
			if(f1.switchByVal[i] == 0)
				zeroCols[i] = true;
			else
				zeroCols[i] = false;
		}
		return zeroCols;
	}
	
	private static String[] get_unique(String[] s1, String[] s2, int size) {
		String[] newS = new String[size];
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
		String ans ="\n";

		return ans;
	}

		private static boolean[] ifEliminate( Factor[] allFactors, String nodeNameToEliminate) {
			boolean[] flags = new boolean[allFactors.length];
			for (int i = 0; i < allFactors.length; i++) {
				for (int j = 0; !flags[i] && j < allFactors[i].dependent.length; j++) {
//					System.out.println(allFactors[i].dependent[j]+" =? "+nodeNameToEliminate);
					if(allFactors[i].dependent[j].equals(nodeNameToEliminate))
						flags[i] = true;
				}
			}
			return flags;
		}
}
