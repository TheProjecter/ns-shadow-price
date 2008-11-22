package netcomponent;
public abstract class Link extends NetworkComponent{
	private Node destination;
	private Node source;

	public Link(Network network, Node source, Node destination){
		super(network);
		this.source=source;
		this.destination=destination;
		this.source.addConnection(this);
	}

	public Node getDestination(){return destination;}
	public Node getSource(){return source;}
	public void transmitPacket(Packet p){
		NetworkComponent nextHop = getDestination();
		nextHop.receivePacket(p);
		getNetwork().outputTxt(generateDataEntry(p,nextHop));
	}
}