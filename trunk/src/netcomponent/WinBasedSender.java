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
	
	void adjustWinSizeLoss(int lostSeqNum){
		threshold=winSize;
		winSize=Math.max(winSize/2, 1);
	}
	
	void adjustWinSizeAck(Packet p){
		accumAck++;
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