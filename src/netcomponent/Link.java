package netcomponent;
public abstract class Link extends NetworkComponent{
	private Node destination;
	private Node source;

	public Link(Network network, Node source, Node destination){
		super(network);
		this.source=source;
		this.source.addConnection(this);
		this.destination=destination;
	}

	public Node getDestination(){return destination;}
	public Node getSource(){return source;}
}