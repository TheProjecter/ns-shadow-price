public class Packet{
	private int mark;
	private Node sender;
	private Node recipient;
	private int seqNum;

	public Packet(Node sender, Node recipient, int seqNum){
		mark = 0;
		this.sender=sender;
		this.recipient=recipient;
		this.seqNum = seqNum;
	}

	public void setMark(int v){mark = v;}

	public int getMark(){return mark;}

	public Node getSender(){return sender;}

	public Node getRecipient(){return recipient;}

	public int getSeqNum(){return seqNum;}
}