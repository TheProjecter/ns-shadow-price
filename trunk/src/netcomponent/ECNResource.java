package netcomponent;

public class ECNResource extends Resource{
	private int alphaPercentage;	//sensitivity to dropped packets between 0..1
	private int alphaBufferSize;

	public ECNResource(Network network, int bufferSize, int capacity){
		super(network, bufferSize, capacity);
		alphaPercentage = 50;
		alphaBufferSize = 0;
	}
	
	public void setAlpha(int alphaPercentage){this.alphaPercentage=alphaPercentage;}
	public int getAlpha(){return alphaPercentage;}

	public void action(){
		super.action();
		alphaBufferSize = alphaBufferSize - alphaPercentage;
	}
	
	public void receivePacket(Packet p){
		if(alphaBufferSize <alphaPercentage*getBufferSize()){alphaBufferSize = alphaBufferSize + 100;}
		else{p.setMark(1);}
		super.receivePacket(p);
	}
}