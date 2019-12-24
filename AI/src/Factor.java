
public class Factor {
	String[][] matrix = null; // The variables for the probability  
	double[] probability = null; // The probability (0-1)
	String[] dependent = null; // The Strings the factor gets 
	int[] switchByVal = null; // switchByVal[i] is the number when dependent[i] is Repeated itself
	
	public Factor() {;}
	
	/**
	 * Normalizes this Factor probability array (sum/probability)
	 */
	public void normalize() 
	{
		double sum = Algorithms.sumArr(this.probability);
		for (int i = 0; i < this.probability.length; i++) {
			this.probability[i] = this.probability[i]/sum;
		}
	}
	
	/**
	 * 
	 * @param c1 - The first array to check.
	 * @param c2 - The Second array to check
	 * @return The number of unique Strings. 
	 */ 
	public static int count_unique(String[] c1, String[] c2) 
	{
		int counter = c1.length;
		for (int i = 0; i < c2.length; i++)
		{
			if(!contains(c1, c2[i]))
				counter++;
		}
		return counter;
	}
	
	public static boolean contains(String[] c1, String nodeName) 
	{
		for (int i = 0; i < c1.length; i++)
		{
			if(c1[i].equals(nodeName))
				return true;
		}
		return false;
	} 
	
	/**
	 * Removes the redundant rows.
	 * @param nodeName - The name of the Node.
	 * @param givenValue - The value that should stay.
	 */
	public void removeGivens(String nodeName, String givenValue) 
	{
		int index = Factor.get_index_by_value(this.dependent , nodeName);
		if(index >= 0) {
			int valueInCol = this.switchByVal[index];
			for (int i = 0; i < index; i++){
				this.switchByVal[i] /= this.switchByVal[index];
			}
			double[] arr1 = new double[this.probability.length/this.switchByVal[index]];
			boolean[] flag = WhatRowInArr1(nodeName, givenValue,index,arr1.length, valueInCol);

			int j = 0;
			for (int i = 0; i < flag.length; i++) {
				
				if(flag[i]) {
					arr1[j] = this.probability[i];
					j++;
				}
			}
			this.probability = arr1;
			this.switchByVal[index] = 0; 
		}
	}
	
	/**
	 * Makes an array of boolean values that are represent in or out of the future array(true: in , false:out)
	 * @param nodeName - The name of the Node.
	 * @param givenValue - The value needed to keep in the array.
	 * @param index - The index of nodeName in the dependents array. 
	 * @param arr1Size - The size of arr1.
	 * @param valueInCol - this.switchByVal[index].
	 * @return an array of boolean values (true for in).
	 */ 
	private boolean[] WhatRowInArr1(String nodeName, String givenValue, int index, int arr1Size, int valueInCol) {
		boolean[] flag = new boolean[this.probability.length];
		int j = 0;
		for (; j < arr1Size; j++) {
			if(givenValue.equals(Ex1.BN.get(this.dependent[index]).VarValues[j])) {
				break;
			}
		}
		// Makes the boolean array.
		for (int i = 0; i < this.probability.length; i++) {
			int nodeVarValSize = Ex1.BN.get(nodeName).VarValues.length;
			int cicle = valueInCol*nodeVarValSize;
			if(i%cicle >= (j*valueInCol)%cicle && i%cicle < ((j+1)*valueInCol)%cicle)
				flag[i] = true;
			else 
				flag[i] = false;
		}
		return flag;
	}
	/**
	 * 
	 * @param toEliminate - The column (variable that we want to eliminate). 
	 * @return A Factor that is toEliminate eliminated from f1.
	 */
	public Factor Elimination( String toEliminate){
		Factor f2 = new Factor();
		f2.dependentElimination(this , toEliminate);
		f2.switchByValElimination(this , toEliminate);
		f2.makeMatrix(Algorithms.findNumOfRows(f2));
		f2.probabilityElimination(this , toEliminate);
		return f2;
	}
	
	/**
	 * Updates the dependent array to not contain toEliminate.
	 * @param f1 - The Factor from which we eliminate.
	 * @param toEliminate - The column (variable that we want to eliminate).
	 */
	private void dependentElimination(Factor f1, String toEliminate) {
		this.dependent = new String[f1.dependent.length-1];
		int conter = 0;
		for (int i = 0; i < f1.dependent.length; i++) {
			if(!f1.dependent[i].equals(toEliminate))
				this.dependent[i-conter] = f1.dependent[i];
			else 
				conter++;
		}
	}

	/**
	 * Updates the switchByVal array to not contain the value for toEliminate. 
	 * @param f1 - The Factor from which we eliminate.
	 * @param toEliminate - The column (variable that we want to eliminate).
	 */
	private void switchByValElimination(Factor f1 , String toEliminate) {
		this.switchByVal = new int[f1.switchByVal.length-1];
		boolean flag = true;
		int i = 0;
		while ( flag && i < f1.dependent.length ) {
			if(f1.dependent[i].equals(toEliminate) )
				flag = false;
			else
				i++;
		}
		for (int j = 0; j < f1.dependent.length; j++) {
			if(j < i)
				this.switchByVal[j] = f1.switchByVal[j]/Ex1.BN.get(toEliminate).VarValues.length;
			else if (j > i)
				this.switchByVal[j-1] = f1.switchByVal[j];
		}
	}
	
	/**
	 * Updates the probability array to not contain the value for toEliminate. 
	 * @param f1 - The Factor from which we eliminate.
	 * @param toEliminate - The column (variable that we want to eliminate).
	 */
	private void probabilityElimination(Factor f1, String toEliminate) {
		this.probability = new double[this.matrix.length];
		int f1toEliminateIndex = get_index_by_value(f1.dependent, toEliminate);
		boolean flag ;
		for (int i = 0; i < this.probability.length; i++) {
			for (int j = 0; j < f1.probability.length; j++) {
				flag = true;
				for (int j2 = 0; flag && j2 < f1.dependent.length; j2++) {
					if(j2 == f1toEliminateIndex)
						continue;
					if(j2 < f1toEliminateIndex ) {
						if(!f1.matrix[j][j2].equals(this.matrix[i][j2]))
							flag = false;
					}
					else if (j2 > f1toEliminateIndex) {
						if(!f1.matrix[j][j2].equals(this.matrix[i][j2-1]))
							flag = false;
					}
				}
				if(flag) {
					this.probability[i] += f1.probability[j];
					Algorithms.numOfPlus++;
				}
			}	
		}
	}

	public static int get_index_by_value(String[] arr, String s) 
	{
		for (int i = 0; i < arr.length; i++)
		{
			if(arr[i].equals(s))
				return i;
		}
		return -1;
	}
	
	public void print() {
		System.out.println(this.dependent[0]);
		for (int i = 0; i < probability.length; i++) {
			if(i == probability.length-1)
				System.out.println(this.probability[i]);
			else
				System.out.print(this.probability[i]+","+"\n");
		}
		
	}
	
	/**
	 * Makes the Factor matrix and updates this.matrix. 
	 * @param numOfRows - The number of rows for the matrix.
	 */
	public void makeMatrix(int numOfRows ) {
		String[][] matrix = new String[numOfRows][this.dependent.length];
		
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[0].length; j++) {
				int nodeVarValSize = Ex1.BN.get(this.dependent[j]).VarValues.length;
				int cicle = this.switchByVal[j]*nodeVarValSize;
				matrix[i][j] = Ex1.BN.get(this.dependent[j]).VarValues[(i%cicle)/this.switchByVal[j]];
			}
		}
		this.matrix = matrix;
	}
	
	/**
	 * Prints the Factor.
	 */
	public void printFactor() {
		Ex1.printArray(this.dependent);
		for (int i = 0; i < this.matrix.length; i++) {
			for (int j = 0; j < this.matrix[0].length; j++) {
				System.out.print(this.matrix[i][j]+" ");
			}
			System.out.println(this.probability[i]);
		}
		System.out.println();
	}
}
