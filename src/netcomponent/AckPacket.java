package netcomponent;

public class AckPacket extends Packet{
	public AckPacket(Packet p){
		super(p.getRecipient(),p.getSender(),p.getSeqNum());
		path = p.path;
	}
	
	public void putNode(Node n){
		//nothing
	}
}