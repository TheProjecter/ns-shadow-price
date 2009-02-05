package netcomponent;

public class ECNWinBasedSenderWTP extends WinBasedSender{
	//variables
	private int utility;
	private int gainFactor;
	private double gainLeftover;

	public ECNWinBasedSenderWTP(Network network, Node destination, int transferSize, int timeout, int utility){
		super(network, destination, transferSize, timeout);
		this.utility=utility;
		this.gainFactor=1;
		this.gainLeftover=0.0;
	}
	
	public void setUtility(int w){utility = w;}
	public void setGainFactor(int k){gainFactor = k;}
	
	void adjustWinSizeLoss(){}	//dont do anything
	
	void adjustWinSizeAck(Packet p){
		double incAmt = gainLeftover + (double)(((double)gainFactor)*(double)((double)((double)utility/(double)winSize)-(double)(p.getMark())));
		int incSize = (int)incAmt;
		gainLeftover = incAmt - (double)incSize;
		winSize = Math.max(1, winSize+incSize);
	}
}