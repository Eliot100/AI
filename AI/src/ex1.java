import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class ex1 {
	public static void main(String[] args) throws IOException  {
		File input = new File("input.txt");
		BayesianNetwork BN = new BayesianNetwork();
		BufferedReader br = new BufferedReader(new FileReader(input)); 
		Queries.bagining(br, input, BN);
		Queries.verticesBuild(br, input, BN);
		File OutputFile = new File("output.txt");
		FileWriter fw = new FileWriter(OutputFile);
		PrintWriter pw = new PrintWriter(fw);
		Queries.writeOutpotFile(br, BN, pw);
		pw.close();
		br.close();
	}
}
