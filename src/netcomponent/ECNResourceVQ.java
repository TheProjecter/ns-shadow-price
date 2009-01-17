package netcomponent;

public class ECNResourceVQ extends Resource{
	private double alphaPercentage;	//sensitivity to dropped packets between 0..1
	private int alphaBufferLoad;

	public ECNResourceVQ(Network network, int bufferSize, int capacity){
		super(network, bufferSize, capacity);
		alphaPercentage = 0.9;
		alphaBufferLoad = 0;
	}
	
	public void setAlpha(double alphaPercentage){this.alphaPercentage=alphaPercentage;}
	public double getAlpha(){return alphaPercentage;}
	
	public void receivePacket(Packet p){
		if(alphaBufferLoad <alphaPercentage*getBufferSize()){alphaBufferLoad = alphaBufferLoad + 1;}
		else{p.setMark(1);}
		super.receivePacket(p);
	}
	public void transmitPacket(Packet p){
		super.transmitPacket(p);
		alphaBufferLoad--;
	}
}