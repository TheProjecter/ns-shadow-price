package netcomponent;
import java.util.LinkedList;

public abstract class Node extends NetworkComponent{
	private LinkedList<Link> connections;

	public Node(Network network){
		super(network);
		connections = new LinkedList<Link>();
	}

	public void addConnection(Link l){connections.add(l);}

	public LinkedList<Link> getAllConnections(){return connections;};

	public Link removeConnection(int i){return connections.remove(i);}

	public abstract Link getConnection();
}