import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;

public class Ex1 {
	public static BayesianNetwork BN;
	/*
	 * this function is the main function in this project
	 */
	public static void main(String[] args) throws IOException  {
		boolean Dbag = false; // true || false
		File input = new File("input.txt");
		Ex1.BN = new BayesianNetwork();
		BufferedReader br = new BufferedReader(new FileReader(input)); 
		Parts.bagining(br);
		Parts.verticesBuild(br);
		File OutputFile = new File("output.txt");
		OutputFile.createNewFile();
		FileWriter fw = new FileWriter(OutputFile);
		PrintWriter pw = new PrintWriter(fw);
		Parts.writeOutputFile(br, pw);
		pw.close();
		br.close();
		if(Dbag) {
			Iterator<Node> it = Ex1.BN.iteretor();
			while (it.hasNext()) {
				Node temp = it.next();
				System.out.println(temp.name+" CPT:");
				Node.printCPT(Ex1.BN, temp.name);
			}
		}
	}
}
