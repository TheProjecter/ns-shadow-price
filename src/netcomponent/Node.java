package netcomponent;
import java.util.Hashtable;

import stats.RateStatsMeter;

public abstract class Node extends NetworkComponent{
	private Hashtable<Node,Link> connections;
	int rateListenerTix;
	boolean rateListenerInstalled;
	
	public Node(Network network){
		super(network);
		connections = new Hashtable<Node, Link>();
	}

	public void addConnection(Link l){connections.put(l.getDestination(),l);}

	Hashtable<Node, Link> getAllConnections(){return connections;};

	public Link removeConnection(int i){return connections.remove(i);}
	
	public Link getConnection(Packet p){
		Link result = connections.get(p.getRecipient());
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
}