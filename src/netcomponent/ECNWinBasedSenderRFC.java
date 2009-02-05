package netcomponent;

public class ECNWinBasedSenderRFC extends WinBasedSender{
	//variables

	public ECNWinBasedSenderRFC(Network network, Node destination, int transferSize, int timeout){
		super(network, destination, transferSize, timeout);
	}
	
	void adjustWinSizeAck(Packet p){
		if (p.getMark()>0){
			adjustWinSizeLoss();
		} else{
			super.adjustWinSizeAck(p);
		}
	}
}