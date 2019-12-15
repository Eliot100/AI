import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;

public class Node {

	String[] ParentsNames;   
	Node[] Parents;     
	String[] VarValues;    
	String name;   
	double[][] CPT;  
	int numOfParents;  
	int place; 
	File cptText;
	
	public Node(String name) {
		this.name = name;
		this.VarValues = null;
	}

	public String toSrting() {
		return this.name;
	}
	
	public void getCPT(BufferedReader br, BayesianNetwork BN) {
		try {
			boolean Dbag= false; // true || false
			int x = 1;
			for (int i = 0; i < this.numOfParents; i++) {
				x *= this.Parents[i].VarValues.length;
			}
			this.CPT = new double[x][this.VarValues.length];
			String st = br.readLine();
			
			for (int RowNum = 0; RowNum < x; RowNum++) {
				if(Dbag){System.out.println(st);}
				this.nextCPT_line(st, BN);
				if(RowNum !=x-1)
					st = br.readLine();
			}
			
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("");
		}
	}
	
	public void nextCPT_line( String st, BayesianNetwork BN) {
		boolean Dbag = false; // true || false
		String[] tempWordArray = st.split(",");
		if (tempWordArray.length != this.numOfParents+2*(this.VarValues.length-1)) {
			throw new RuntimeException("This row dosn't present a part of "+this.name+" CPT.\ngot: "+st );
		}
		
		int SwithValueIndex = 1;
		int[] PrentSwithValueIndex = new int[this.numOfParents];
		for (int i = this.numOfParents-1; i >= 0; i--) {
			PrentSwithValueIndex[i] = SwithValueIndex;
			SwithValueIndex *=this.Parents[i].VarValues.length;
		}
		
		SwithValueIndex = 1;
		int[] PrentnumSwithValueIndex = new int[this.numOfParents];
		for (int i = this.numOfParents-1; i >= 0; i--) {
			PrentnumSwithValueIndex[i] = SwithValueIndex;
			SwithValueIndex *= this.Parents[i].VarValues.length;
		}
		if(Dbag){System.out.println(this.name);}
		if(Dbag){System.out.println(this.numOfParents);}
		int RowNum = 0;
		String RowString = "";
		for (int i = 0; i < this.numOfParents; i++) {
			for (int j = 0; j < this.Parents[i].VarValues.length; j++) {
				if(tempWordArray[i].equals(BN.get(this.ParentsNames[i]).VarValues[j])){
					RowNum += j*PrentSwithValueIndex[i];
					RowString +=PrentSwithValueIndex[i]+" ";
				}
			}
		}
		
		double sum = 0;
		for (int i = 0; i < this.VarValues.length-1; i++) {
			if(Dbag){System.out.println(RowNum);}
			if(Dbag){System.out.println(RowString);}
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
