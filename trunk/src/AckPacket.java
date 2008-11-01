public class AckPacket extends Packet{
	AckPacket(Node sender, Node recipient, int seqNum){
		super(sender,recipient,seqNum);
	}
}