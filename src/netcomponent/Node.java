package netcomponent;
import java.util.Hashtable;
import java.util.Iterator;
import stats.RateStatsMeter;

public abstract class Node extends NetworkComponent{
	Hashtable<Node,Link> connections;
	Hashtable<Node,Link> routingTable;
	int rateListenerTix;
	boolean rateListenerInstalled;
	boolean routeTableInitialized;
	
	public Node(Network network){
		super(network);
		connections = new Hashtable<Node, Link>();
		routingTable = new Hashtable<Node,Link>();
		routeTableInitialized = false;
	}
	
	public void receiveDistanceVector(Node source, Hashtable<Node,Link> dVector){
		//update its own.
		Iterator<Node> endNodes = dVector.keySet().iterator();
		while(endNodes.hasNext()){
			Node n = endNodes.next();
			if(!(routingTable.containsKey(n))){
				routingTable.put(n, connections.get(source));
			}
		}
		
		//forward to all except source
		Iterator<Node> neighbours = connections.keySet().iterator();
		while(neighbours.hasNext()){
			Node n = neighbours.next();
			if(!(n.equals(source))){
				n.receiveDistanceVector(this, routingTable);
			}
		}
	}

	public void addConnection(Link l){
		connections.put(l.getDestination(),l);
		routingTable.put(l.getDestination(), l);
	}
	
	public void announcePresence(){
		Iterator<Node> neighbours = connections.keySet().iterator();
		while(neighbours.hasNext()){
			neighbours.next().receiveDistanceVector(this, routingTable);
		}
	}

	Hashtable<Node, Link> getAllConnections(){return connections;};

	public Link removeConnection(int i){return connections.remove(i);}
	
	public Link getConnection(Packet p){
		Link result = routingTable.get(p.getRecipient());
		return result;	//need to check whether result is null.
	}
	
	public void transmitPacket(Packet p){
		NetworkComponent nextHop = getConnection(p);
		nextHop.receivePacket(p);
		getNetwork().outputTxt(generateDataEntry(p,nextHop));
		if(rateListenerInstalled){
			getNetwork().getStatsMeter(this, rateListenerTix).newData(generateDataEntry(p));
		}
	}
	
	public void addRateListener(){
		if (!rateListenerInstalled){
			rateListenerTix = getNetwork().addStatsMeter(this, new RateStatsMeter());
			rateListenerInstalled = true;
		}
	}
	public int getRateListenerTix(){
		if (rateListenerInstalled){
			return rateListenerTix;
		} else{
			return -1;
		}
	}
}