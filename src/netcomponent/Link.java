package netcomponent;

import java.util.LinkedList;

public abstract class Link extends NetworkComponent{
	private Node destination;
	private Node source;
	private LinkedList<Packet> packetList;

	public Link(Network network, Node source, Node destination){
		super(network);
		this.source=source;
		this.destination=destination;
		this.source.addConnection(this);
		packetList = new LinkedList<Packet>();
	}

	public Node getDestination(){return destination;}
	public Node getSource(){return source;}
	public void transmitPacket(Packet p){
		NetworkComponent nextHop = getDestination();
		nextHop.receivePacket(p);
		getNetwork().outputTxt(generateDataEntry(p,nextHop));
	}
	
	public void action(){
		transmitPacket(packetList.remove());
	}
	
	public void receivePacket(Packet p){
		packetList.add(p);
		int n = nextDelay();
		getNetwork().addEvent(this,n);
		//System.out.println(n);
	}
	
	public abstract int nextDelay();
	public abstract double getMeanDelay();
	public abstract double getDelayVariance();
}