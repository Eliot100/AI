import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class ex1 {
	public static void main(String[] args) throws IOException  {
		File input = new File("C:\\Users\\user\\Desktop\\files to run\\java\\AI algoritems\\input.txt");
		BayesianNetwork BN = new BayesianNetwork();
		BufferedReader br = new BufferedReader(new FileReader(input)); 
		BayesianNetwork.bagining(br, input, BN);
		BayesianNetwork.verticesBuild(br, input, BN);
		File OutpotFile = new File("C:\\Users\\user\\Desktop\\files to run\\java\\AI algoritems\\output.txt");
		OutpotFile = Queries.writeOutpotFile(br, BN);
		br.close();
		System.out.println(OutpotFile.createNewFile());
	}
}
