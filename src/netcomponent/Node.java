package netcomponent;
import java.util.Hashtable;
import java.util.PriorityQueue;
import java.util.Iterator;
import stats.RateStatsMeter;
import datastruct.Tuple;

public abstract class Node extends NetworkComponent{
	Hashtable<Node,Link> connections;
	Hashtable<Node,PriorityQueue<Tuple<Integer,Link>>> routingTable;
	int rateListenerTix;
	boolean rateListenerInstalled;

	public Node(Network network){
		super(network);
		connections = new Hashtable<Node, Link>();
		routingTable = new Hashtable<Node,PriorityQueue<Tuple<Integer,Link>>>();
	}
	
	public void receiveDistanceVector(Node source, Hashtable<Node,PriorityQueue<Tuple<Integer,Link>>> dVector){
		//update its own.
		boolean updated = false;
		Iterator<Node> endNodes = dVector.keySet().iterator();
		while(endNodes.hasNext()){
			Node n = endNodes.next();
			if(!(n.equals(this))){
				if(!(routingTable.containsKey(n))){
					routingTable.put(n, new PriorityQueue<Tuple<Integer,Link>>());
				}
				boolean found = false;
				Iterator<Tuple<Integer,Link>> tIterator = routingTable.get(n).iterator();
				while(tIterator.hasNext()){
					Tuple<Integer,Link> t = tIterator.next();
					if(t.getY().equals(connections.get(source))){
						found = true;
						if(dVector.get(n).peek().getX()+1 < t.getX()){
							routingTable.get(n).remove(t);	//need to remove then add because need to preserve order
							routingTable.get(n).add(new Tuple<Integer,Link>(dVector.get(n).peek().getX()+1,connections.get(source)));
							updated = true;
						}
						break;
					}
				}
				if(!found){
					routingTable.get(n).add(new Tuple<Integer,Link>(dVector.get(n).peek().getX()+1,connections.get(source)));
					updated = true;
				}
			}
		}
		
		//forward to all except source if something has changed
		if(updated) announcePresenceExcept(source);
	}

	public void addConnection(Link l){
		connections.put(l.getDestination(),l);
		if (!(routingTable.containsKey(l.getDestination()))){
			routingTable.put(l.getDestination(), new PriorityQueue<Tuple<Integer,Link>>());
		}
		//no need to check if l is already there because l is new
		routingTable.get(l.getDestination()).add(new Tuple<Integer,Link>(1,l));
	}
	
	public void announcePresence(){
		Iterator<Node> neighbours = connections.keySet().iterator();
		while(neighbours.hasNext()){
			neighbours.next().receiveDistanceVector(this, routingTable);
		}
	}
	
	public void announcePresenceExcept(Node source){
		Iterator<Node> neighbours = connections.keySet().iterator();
		while(neighbours.hasNext()){
			Node n = neighbours.next();
			if(!(n.equals(source))){
				n.receiveDistanceVector(this, routingTable);
			}
		}
	}

	Hashtable<Node, Link> getAllConnections(){return connections;};

	public Link removeConnection(int i){return connections.remove(i);}
	
	public Link getConnection(Packet p){
		Link result;
		if(p instanceof AckPacket){
			Node a = p.extractNode();
			result = connections.get(a);
		} else{
			result = routingTable.get(p.getRecipient()).peek().getY();
		}
		return result;	//need to check whether result is null.
	}
	
	public void transmitPacket(Packet p){
		NetworkComponent nextHop = getConnection(p);
		p.putNode(((Link)nextHop).getDestination());
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