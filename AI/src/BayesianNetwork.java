
import java.util.HashMap;
import java.util.Iterator;

/**
 * @author Eli Ruvinov
 *	 In this class we create Bayesian Network
 */
public class BayesianNetwork {
	protected HashMap <String, Node> nodesHash;
	/**
	 * Initialize the data structure
	 */
	public BayesianNetwork(){
		nodesHash = new HashMap<String, Node>();
	}
	
	/**
	 * @return String like: {A,D,E,V,H} 
	 * When The vertices A,D,E,V,H in the network
	 */
	public String toString() {
		String st = "{ ";
		Iterator<Node> itr = this.iteretor(); 
		while (itr.hasNext()) {
			Node mapNode = itr.next(); 
			st +=mapNode.toString()+", ";
		}
		st = st.substring(0, st.length()-2)+" }";
		return st;
	}

	/**
	 * @return An iterator on the network Nodes
	 */
	public Iterator<Node> iteretor() {
		Iterator<Node> itr = this.nodesHash.values().iterator();
		return itr;
	}
	
	/**
	 * @return how many Nodes there are in the network
	 */
	public int size() {
		return this.nodesHash.size();
	}
	
	/**
	 * This function used get a node from the network
	 * @param nodeNeme - the string of node you want to get
	 * @return Node - node you want to get with the name - nodeNeme  
	 */
	public Node get(String nodeNeme) {
		return this.nodesHash.get(nodeNeme);
	}
	
	/**
	 * This function used put a node in the network
	 * @param nodeNeme - the string Name of node you want to put
	 * @param n - the the node that you what you want to put
	 */
	public void put(String nodeName, Node n) {
		this.nodesHash.put(nodeName, n);
	}
}
