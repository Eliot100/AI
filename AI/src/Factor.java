
public class Factor {
	String[][] matrix = null; // The variables for the probability 
	double[] unknown = null; // The probability (0-1)
	String[] known = null; // The chars the factor gets 
	//Factor[] Parents = null; // The factor's parent factors
	public Factor() 
	{
		;
	}
	public Factor(String[][] matrix, double[] unknown, String[] known)
	{
		this.matrix = matrix;
		this.unknown = unknown;
		this.known = known;
		
		//this.Parents = Parents;
	}

	public Factor(Factor f1) 
	{
		this.matrix = f1.matrix;
		//this.Parents = f1.Parents;
		this.known = f1.known;
		this.unknown = f1.unknown;
	}

	public Factor JoiningFactors(Factor f1, Factor f2)
	{
		Factor joined = new Factor();
		int counter = count_unique(f1.known, f2.known);
		joined.matrix = new String[(int)Math.pow(2.0, (double)(counter))][counter]; // needs improvement.

		return joined;
	}

	public int count_unique(String[] c1, String[] c2) 
	{
		int counter = c1.length-1;
		for (int i = 0; i < c2.length; i++)
		{
			if(!contains(c1, c2[i]))
				counter++;
		}
		return counter;
	}

	public  boolean contains(char[] c1, char car) 
	{
		for (int i = 0; i < c1.length; i++)
		{
			if(c1[i] == car)
				return true;
		}
		return false;
	}
	
	public  boolean contains(String[] c1, String car) 
	{
		for (int i = 0; i < c1.length; i++)
		{
			if(c1[i].equals(car))
				return true;
		}
		return false;
	} 
	
	/**
	 * This function eliminates the second factor from the first one.
	 * @param f1 - The factor from which a factor is to be eliminated.
	 * @param toeliminate - The factor to eliminate (assuming the know array is of length 1).
	 * @return Returns the first factor when the second factor is eliminated from it. 
	 */
	public Factor Eliminaton(Factor f1 , Factor toeliminate)
	{
		// call joiningFactors
		Factor f2 = new Factor();
		f2.matrix = new String[f1.matrix.length-1][f1.matrix[0].length-1];
		String[] arr = new String[f1.matrix.length-1];
		int[] removebycol = new int[f1.matrix[0].length]; 
		for (int i = 0; i < f1.matrix.length; i++) 
		{
			arr = make_arr(f1,i,toeliminate);
			removebycol = makeremovebycol(f1, i, toeliminate);
			f2.matrix[i] = arr;
			f2.unknown[i] = get_value_to_merge(arr, f1, removebycol);
		}
		f2.known = makeknown(f1, toeliminate);
		return f2;
	}

	public String[] makeknown(Factor f1, Factor toeliminate) 
	{
		String[] arr = new String[f1.known.length-toeliminate.known.length];
		int counter = 0;
		for (int i = 0; i < f1.known.length; i++)
		{
			if(!contains(toeliminate.known, f1.known[i])) 
			{
				arr[counter] = f1.known[i];
				counter++;
			}
		}
		return arr;
	}
	
	/**
	 * 
	 * @param f1 - The factor from which we eliminate.
	 * @param row - The row to make the needed array from.
	 * @param toeliminate - The elimination Factor.
	 * @return The array of columns to check. 
	 */
	public int[] makeremovebycol(Factor f1, int row, Factor toeliminate) 
	{
		int[] arr = new int[f1.known.length-toeliminate.known.length];
		int counter = 0;
		for (int i = 0; i < f1.known.length; i++)
		{
			if(!contains(toeliminate.known,f1.known[i])) 
			{
				arr[counter] = i;
				counter++;	
			}	
		}
		return arr;
	}

	/**
	 * 
	 * @param f1 - The factor from which we eliminate.
	 * @param row - The row to make the needed array from.
	 * @param toeliminate - The elimination Factor.
	 * @return THe array needed without the eliminate Factor.
	 */
	public String[] make_arr(Factor f1, int row, Factor toeliminate) 
	{
		String[] arr = new String[f1.known.length-toeliminate.known.length];
		int counter = 0;
		for (int i = 0; i < f1.known.length; i++)
		{
			if(!contains(toeliminate.known,f1.known[i])) 
			{
				arr[counter] = f1.matrix[row][i];
				counter++;	
			}	
		}
		return arr;
	}

	/**
	 * 
	 * @param arr - The array that is needed.
	 * @param f1 - The factor from which the prbabilites needed are.
	 * @param removebycol - The array of columns to check
	 * @return The value of the row
	 */
	public double get_value_to_merge(String[] arr, Factor f1, int[] removebycol) 
	{
		double sum = 0;
		for (int i = 0; i < f1.matrix.length; i++)
		{
			if(is_row_included(f1.matrix[i], arr, removebycol))
				sum+=f1.unknown[i];
		}
		return sum;
	}

	/**
	 * 
	 * @param row - The row that is needed (Smaller).
	 * @param arr - The row from the matrix of the factor (Bigger).
	 * @param removebycol - Array of columns to check.
	 * @return true if row in the places specified in removebycol equals arr. 
	 */
	public boolean is_row_included(String[] arr, String[] row, int[] removebycol) 
	{
		int counter = 0;
		for (int i = 0; i < row.length; i++) 
		{
			if(contains(removebycol, i)) 
			{
				if(!arr[i].equals(row[counter]))
					return false;
				counter++;
			}
			else
				counter+=2;
		}
		return true;
	}

	public int get_index_by_value(String[] arr, String s) 
	{
		for (int i = 0; i < arr.length; i++)
		{
			if(arr[i].equals(s))
				return i;
		}
		return -1;
	}

	public boolean contains(int[] arr, int q) 
	{
		for (int i = 0; i < arr.length; i++)
		{
			if(arr[i] == q)
				return true;
		}
		return false;
	}
}
