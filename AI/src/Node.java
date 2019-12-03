import java.io.BufferedReader;
import java.io.IOException;

public class Node {

	String[] ParentsNames;   
	Node[] Parents;     
	String[] VarValues;    
	String name;   
	double[][] CPT;  
	int numOfParents;  
	int place; 
	
	public Node(String name) {
		this.name = name;
	}
	
	public String toSrting() {
		return this.name;
	}
	
	public void getCPT(BufferedReader br, BayesianNetwork BN) {
		try {
			int x = 1;
			for (int i = 0; i < this.numOfParents; i++) {
				x *= BN.nodesHash.get(this.ParentsNames[i]).VarValues.length;
			}
			this.CPT = new double[x][this.VarValues.length];
			String st = br.readLine();
			int[] PrentValuePlace = new int[this.numOfParents];
			int multiplicationOfPrentsValues = 1;
			for (int i = 0; i < this.numOfParents; i++) {
				multiplicationOfPrentsValues *= this.Parents[i].VarValues.length;
			}
			int SwithValueIndex = 1;
			int recacl = 1;
			int[] PrentSwithValueIndex = new int[this.numOfParents];
			int[] PrentRecaclValueIndex = new int[this.numOfParents];
			for (int i = this.numOfParents-1; i >= 0; i--) {
				recacl *=this.Parents[i].VarValues.length;
				PrentRecaclValueIndex[i] = recacl;
				PrentSwithValueIndex[i] = SwithValueIndex;
				SwithValueIndex *=this.Parents[i].VarValues.length;
			}
			
			for (int RowNum = 0; RowNum < multiplicationOfPrentsValues; RowNum++) {
				for (int i = 0; i < this.numOfParents; i++) {
					PrentValuePlace[i] = ((int) RowNum / PrentSwithValueIndex[i]) % PrentRecaclValueIndex[i];
				}
				this.nextCPT_line(multiplicationOfPrentsValues, st, PrentValuePlace, BN, RowNum);
				if(RowNum !=multiplicationOfPrentsValues-1)
					st = br.readLine();
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void nextCPT_line(int multiplicationOfPrentsValues, String st, int[] PrentValuePlace, BayesianNetwork BN, int RowNum) {
		boolean Dbag = false; // true || false
		String[] tempWordArray = st.split(",");
		if (tempWordArray.length != this.numOfParents+2*(this.VarValues.length-1)) {
			throw new RuntimeException("This row dosn't present a part of "+this.name+" CPT.\ngot: "+st );
		}
		
//		for (int i = 0; i < this.numOfParents; i++) {
//			if(!this.Parents[i].VarValues[PrentValuePlace[i]].equals(tempWordArray[i])) {
//				System.out.print(tempWordArray[i]+" == "+this.Parents[i].VarValues[PrentValuePlace[i]]+PrentValuePlace[i]+"\n");
//				throw new RuntimeException("In this row 1 value of parent isn't true.\n"+st+"\nPrent: "+this.Parents[i].name+
//						", parent value: "+this.Parents[i].VarValues[PrentValuePlace[i]] );
//			}
//		}
		
		int SwithValueIndex = 1;
		int numOfvals = 1;
//		int[] PrentSwithValueIndex = new int[this.numOfParents];
		int[] PrentnumOfvalsByIndex = new int[this.numOfParents];
		for (int i = this.numOfParents-1; i >= 0; i--) {
			numOfvals *=this.Parents[i].VarValues.length;
			PrentnumOfvalsByIndex[i] = numOfvals;
			PrentnumOfvalsByIndex[i] = SwithValueIndex;
			SwithValueIndex *= this.Parents[i].VarValues.length;
		}
		
//		int row = 0;
		if(Dbag){System.out.println(this.name);}
//		for (int i = this.numOfParents-1; i >= 0; i--) {
//			if(Dbag){System.out.println(this.Parents[i]);}
//			for (int j = 0; j < this.Parents[i].VarValues.length; j++) {
//				if(Dbag){System.out.println(st);}
//				if(tempWordArray[i].contains(this.Parents[i].VarValues[j])) {
//					
//				}
//
//			}
//
//		}
		
		double sum = 0;
		for (int i = 0; i < this.VarValues.length-1; i++) {
			if(!(tempWordArray[this.numOfParents+2*i].length() > 1 &&
					tempWordArray[this.numOfParents+2*i].charAt(0) == '=' &&
					tempWordArray[this.numOfParents+2*i].subSequence(1, tempWordArray[this.numOfParents+2*i].length()).equals(this.VarValues[i]))) {
				throw new RuntimeException( st+", "+(tempWordArray[this.numOfParents+2*i].length() > 2)+", "+
						(tempWordArray[this.numOfParents+2*i].charAt(0) == '=')+", "+
						tempWordArray[this.numOfParents+2*i].subSequence(1, tempWordArray[this.numOfParents+2*i].length())+" "+(this.VarValues[i]));
			}
			this.CPT[RowNum][i] = Double.parseDouble(tempWordArray[this.numOfParents+2*i+1]);
			sum = sum +this.CPT[RowNum][i];
		}
		double lastValProb =1-sum;
		if(String.valueOf(lastValProb).length() > 10 ) {
			if(String.valueOf(lastValProb).charAt(10) == '9') {
				lastValProb+= 0.00000001;
			}
			lastValProb = ((int) (lastValProb*100000000))/100000000.0;
		}
		
		this.CPT[RowNum][this.VarValues.length-1] = lastValProb;
	}
	
	public static void printCPT(BayesianNetwork BN, String nodeName) {
		for (int i = 0; i < BN.nodesHash.get(nodeName).CPT.length; i++) {
			System.out.print("[");
			for (int j = 0; j < BN.nodesHash.get(nodeName).CPT[0].length; j++) {
				System.out.print(BN.nodesHash.get(nodeName).CPT[i][j]);
				if(j != BN.nodesHash.get(nodeName).CPT[0].length-1)
					System.out.print(", ");
			}
			System.out.println("]");
		}
	}
}
