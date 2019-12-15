
public class Algoritems {

	public static String VariableElimination(BayesianNetwork BN, String st) {
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
//		String[] = st.substring(4, indexLine).split(regex, limit);
		
		return ans;
	}

	public static String BayesBall(BayesianNetwork BN, String st) {
		String ans ="";

		return ans;
	}
	
}
