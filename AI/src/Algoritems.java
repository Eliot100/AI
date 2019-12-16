
public class Algoritems {

	public static String VariableElimination(BayesianNetwork BN, String st) {
		boolean Dbag= true; // true || false
		String ans ="";
		boolean flag1 = true;
		boolean flag2 = true;
		int indexLine = 0, indexEndP = 0;
		for (int i = 0; i < st.length() && flag2 ; i++) {
			if (st.charAt(i)== '|' && flag1) {
				indexLine = i;
				flag1 =  false;
			}
			else if (st.charAt(i)== ')') {
				indexEndP = i;
				flag2 =  false;
			}
		}
		Node target = BN.get(""+st.charAt(2));
		String valTarget = st.substring(4, indexLine);
		String[] GivenPs = st.substring(indexLine+1, indexEndP).split(",");
		String[] factorsSeq = st.substring( indexEndP+2, st.length() ).split("-");
		String[] GivenNodes = new String[GivenPs.length];
		String[] GivenValsByNode = new String[GivenPs.length];
		for (int i = 0; i < GivenPs.length; i++) {
			GivenNodes[i] = ""+GivenPs[i].charAt(0);
			GivenValsByNode[i] = ""+GivenPs[i].substring(2);
		}
		Factor[] arrayF = new Factor[factorsSeq.length];
		for (int i = 0; i < factorsSeq.length; i++) {
			arrayF[i] = new Factor(BN.get(factorsSeq[i]).CPT, BN.get(factorsSeq[i]).PrentnumSwithValueIndex, BN.get(factorsSeq[i]).ParentsNames);
		}
		if(Dbag) {
			System.out.println("Node target : "+target.name);
			System.out.println("value Target : "+valTarget);
			System.out.print("GivenNodes : ");
			for (int i = 0; i < GivenNodes.length; i++) {
				System.out.print(GivenNodes[i]+" ");
			}
			System.out.print("\nGivenValsByNode : ");
			for (int i = 0; i < GivenValsByNode.length; i++) {
				System.out.print(GivenValsByNode[i]+" ");
			}
			System.out.print("\nfactorsSeq : ");
			for (int i = 0; i < factorsSeq.length; i++) {
				System.out.print(factorsSeq[i]+" ");
			}
			System.out.println("\n");
		}
		return ans;
	}

	public static String BayesBall(BayesianNetwork BN, String st) {
		String ans ="\n";

		return ans;
	}
	
	
	
}
