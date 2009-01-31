package netcomponent;

public class WinBasedSender extends SimplifiedWinBasedSender{
	//variables
	int threshold;
	int accumAck;

	public WinBasedSender(Network network, Node destination, int transferSize, int timeout){
		super(network, destination, transferSize, timeout);
		this.threshold=0;
		this.accumAck=0;
	}

	public void receivePacket(Packet p){
		if(ps[p.getSeqNum()] instanceof PacketStatusPending && !(ps[p.getSeqNum()].isExpired())){ accumAck++;}
		super.receivePacket(p);
	}
	
	void reduceWinSize(){
		threshold=winSize;
		winSize=Math.max(winSize/2, 1);
	}
	
	void increaseWinSize(){
		if (threshold==0 || winSize<=threshold){
			//slow start
			winSize = winSize+accumAck;
			accumAck=0;
		}
		else{
			//congestion avoidance
			if(accumAck==winSize){
				winSize++;
				accumAck=0;
			}
		}
	}
}