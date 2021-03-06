import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
/**
 * @author Eli Ruvinov and Dvir Sadon
 * In this class we create the Node that we using in the Bayesian Network
 */
public class Node {
	String[] ParentsNames; 
	ArrayList<String> SonsNames;  
	String[] VarValues;    
	String name;   
	double[][] CPT;  
	int numOfParents;  
	File cptText;
	int[] PrentSwithVal;
	Factor cptFactor;
	/**
	 * This function initialize Node by: name
	 */
	public Node(String name) {
		this.name = name;
		this.SonsNames = new ArrayList<String>();
		this.VarValues = null;
	}

	/**
	 * @return String - the node name 
	 */
	public String toString() {
		return this.name;
	}

	/**
	 * This function initialize the CPT of this node
	 * @param br - is BufferedReader which contains lines containing probabilities by the Nodes parents Values  
	 */
	public void getCPT(BufferedReader br) {
		try {
			boolean Dbag= false; // true || false
			int x = 1;
			for (int i = 0; i < this.numOfParents; i++) {
				x *= Ex1.BN.get(this.ParentsNames[i]).VarValues.length;
			}
			this.CPT = new double[x][this.VarValues.length];
			String st = br.readLine();

			for (int RowNum = 0; RowNum < x; RowNum++) {
				if(Dbag){System.out.println(st);}
				this.nextCPT_line(st);
				if(RowNum !=x-1)
					st = br.readLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("");
		}
	}

	/**
	 * This function sets a row in the CTP matrix
	 * @param st - String that contain this node probability
	 */
	public void nextCPT_line( String st) {

		String[] tempWordArray = st.split(",");
		if (tempWordArray.length != this.numOfParents+2*(this.VarValues.length-1)) 
			throw new RuntimeException("This row dosn't present a part of "+this.name+" CPT.\ngot: "+st );

		int RowNum = 0;
		for (int i = 0; i < this.numOfParents; i++) {
			for (int j = 0; j < Ex1.BN.get(this.ParentsNames[i]).VarValues.length; j++) 
				if(tempWordArray[i].equals(Ex1.BN.get(this.ParentsNames[i]).VarValues[j]))
					RowNum += j*this.PrentSwithVal[i];
		}

		double lastValProb = lastProbability(tempWordArray, RowNum);
		this.CPT[RowNum][this.VarValues.length-1] = lastValProb;
	}

	/**
	 * @param tempWordArray - Array of values between ','.
	 * @param RowNum - Index to check in tempWordArray.
	 * @return A double value that represents the lastProbability needed.
	 */
	private double lastProbability(String[] tempWordArray, int RowNum) {
		double sum = 0;
		for (int i = 0; i < this.VarValues.length-1; i++) {
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
		return lastValProb;
	}

	/**
	 * Makes a Factor from the CTP. 
	 * @return A Factor that represents the CTP.
	 */
	public static void printCPT( String nodeName) {
		for (int i = 0; i < Ex1.BN.get(nodeName).CPT.length; i++) {
			System.out.print("[");
			for (int j = 0; j < Ex1.BN.get(nodeName).CPT[0].length; j++) {
				System.out.print(Ex1.BN.get(nodeName).CPT[i][j]);
				if(j != Ex1.BN.get(nodeName).CPT[0].length-1)
					System.out.print(", ");
			}
			System.out.println("]");
		}
	}

	public Factor CTPtoFactor() {
		Factor f = new Factor();
		double[] probabilities = new double[this.CPT[0].length*this.CPT.length];
		int row = 0;
		for (int i = 0; i < this.CPT[0].length; i++) {
			for (int j = 0; j < this.CPT.length; j++) {
				probabilities[row] = this.CPT[j][i];
				row++;
			}
		}
		String pernts =this.name;
		if(this.numOfParents > 0)
			pernts += ",";
		for (int i = 0; i < this.numOfParents; i++) {
			if (i == this.numOfParents-1)
				pernts += this.ParentsNames[i];
			else
				pernts += this.ParentsNames[i]+",";
		}
		f.dependent = pernts.split(",");
		f.switchByVal = makeSwitch(f.dependent);
		f.probability = probabilities;
		f.makeMatrix();
		return f;
	}
	
	// Creates the switchByVal array.
		public static int[] makeSwitch(String[] dependent) {
			int[] switchByVal = new int[dependent.length];
			int switch0 = 1;
			for (int i = switchByVal.length-1; i >= 0; i--) {
				switchByVal[i] = switch0;
				switch0 *= Ex1.BN.get(dependent[i]).VarValues.length;
			}
			return switchByVal;
		}

	public void buildSwithbyVal() {
		int SwithValueIndex = 1;
		int[] PrentnumSwithValueIndex = new int[this.numOfParents];
		for (int i = this.numOfParents-1; i >= 0; i--) {
			PrentnumSwithValueIndex[i] = SwithValueIndex;
			SwithValueIndex *= Ex1.BN.get(this.ParentsNames[i]).VarValues.length;
		}
		this.PrentSwithVal = PrentnumSwithValueIndex;
	}
}
