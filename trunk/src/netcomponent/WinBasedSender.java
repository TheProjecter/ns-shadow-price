package netcomponent;

public class WinBasedSender extends SimplifiedWinBasedSender{
	//variables
	double threshold;

	public WinBasedSender(Network network, Node destination, int transferSize, int timeout){
		super(network, destination, transferSize, timeout);
		this.threshold=0.0;
	}
	
	void adjustWinSizeLoss(int lostSeqNum){
		threshold=winSize;
		winSize=Math.max(winSize/2, 1);
	}
	
	void adjustWinSizeAck(Packet p){
		if (threshold<1.0 || winSize<=threshold){
			//slow start
			winSize = winSize+1.0;
		}
		else{
			//congestion avoidance
			winSize=winSize + 1/winSize;
		}
	}
}