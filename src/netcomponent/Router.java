package netcomponent;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;

public class Router extends Node{
	LinkedList<Packet> buffer;
	
	public Router(Network network){
		super(network);
		buffer = new LinkedList<Packet>();
	}
	
	public void action(){
		
	}

	public void receivePacket(Packet p){
		
	}

	public void transmitPacket(Packet p){
		
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
}
