package netcomponent;
public abstract class Sender extends Node{
	private Node destination;

	public Sender(Network network, Node destination){
		super(network);
		this.destination = destination;
	}

	Node getDestination(){return destination;}
}