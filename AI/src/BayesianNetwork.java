
import java.util.HashMap;
import java.util.Iterator;


public class BayesianNetwork {
	protected HashMap <String, Node> nodesHash;

	public BayesianNetwork(){
		nodesHash = new HashMap<String, Node>();
	}

	public String toSrting() {
		String st = "{ ";
		Iterator<Node> itr = this.iteretor(); 
		while (itr.hasNext()) {
			Node mapNode = itr.next(); 
			st +=mapNode.toSrting()+", ";
		}
		st = st.substring(0, st.length()-2)+" }";
		return st;
	}

	public Iterator<Node> iteretor() {
		Iterator<Node> itr = this.nodesHash.values().iterator();
		return itr;
	}



}
