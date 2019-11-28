import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Queries {

	public static String VariableElimination(BayesianNetwork BN, String st) {
		String ans ="";

		return ans;
	}

	public static String BayesBall(BayesianNetwork BN, String st) {
		String ans ="";

		return ans;
	}
	

	public static File writeOutpotFile(BufferedReader br, BayesianNetwork BN) throws IOException {
		String st = br.readLine();
		if(!st.contains("Queries")) {
			throw new RuntimeException("This isn't the Queries part.");
		}
		st = br.readLine();
		File output = new File("C:\\Users\\user\\Desktop\\files to run\\java\\AI algoritems\\output.txt");

		BufferedWriter br2 = new BufferedWriter(new FileWriter(output));
		br2.write( "Hellow" );
		while ((st = br.readLine()) != null) {
			if(st.contains("P(") && st.contains(")")) {
				br2.write( Queries.VariableElimination(BN, st) );
			} else if( st.contains("-") && st.contains("|") ) {
				br2.write( Queries.BayesBall(BN, st) );
			}
		}
		
		br2.close();
		//System.out.println(output);
		return output;
	}
}
