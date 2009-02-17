package netcomponent;

import java.util.LinkedList;

public class Packet{
	private int mark;
	private Node sender;
	private Node recipient;
	private int seqNum;
	LinkedList<Node> path;

	public Packet(Node sender, Node recipient, int seqNum){
		mark = 0;
		this.sender=sender;
		this.recipient=recipient;
		this.seqNum = seqNum;
		this.path = new LinkedList<Node>();
		this.path.add(sender);
	}

	public void setMark(int v){mark = v;}

	public int getMark(){return mark;}

	public Node getSender(){return sender;}

	public Node getRecipient(){return recipient;}

	public int getSeqNum(){return seqNum;}
	
	public void putNode(Node n){path.add(n);}
	
	public Node extractNode(){return path.removeLast();}
}