package netcomponent;

public class ECNResourceRED extends Resource{
	private int startMarkRandom;	//sensitivity to dropped packets between 0..1
	private int startMarkAll;

	public ECNResourceRED(Network network, int bufferSize, int capacity){
		super(network, bufferSize, capacity);
		startMarkRandom = bufferSize/3;
		startMarkAll = 2*bufferSize/3;
	}
	
	public void receivePacket(Packet p){
		if(buffer.size()>=startMarkAll || (buffer.size()>=startMarkRandom && buffer.size()<startMarkAll && new java.util.Random().nextInt(100)<markProb(buffer.size()))){
			p.setMark(1);
		}
		super.receivePacket(p);
	}
	
	public void setMarkRandom(double percentFull){
		startMarkRandom = (int)(this.getBufferSize()*percentFull);
	}
	public void setMarkAll(double percentFull){
		startMarkAll = (int)(this.getBufferSize()*percentFull);
	}
	
	private int markProb(int n){
		return (n-startMarkRandom)*100/(startMarkAll-startMarkRandom); 
	}
}