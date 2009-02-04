package netcomponent;

import stats.AlphaStatsMeter;

public class ECNResourceVQ extends Resource{
	private double alphaPercentage;	//sensitivity to dropped packets between 0..1
	private double alphaBufferLoad;
	private int lastAlphaBufferClearTime;
	int alphaListenerTix;
	boolean alphaListenerInstalled;

	public ECNResourceVQ(Network network, int bufferSize, int capacity){
		super(network, bufferSize, capacity);
		alphaPercentage = 0.9;
		alphaBufferLoad = 0.0;
		alphaListenerInstalled = false;
		lastAlphaBufferClearTime = 0;
	}
	
	public void setAlpha(double alphaPercentage){this.alphaPercentage = alphaPercentage;}
	public double getAlpha(){return alphaPercentage;}
	
	public void receivePacket(Packet p){
		if(!bufferFull()){
			if(alphaBufferLoad > alphaPercentage*(double)(getBufferSize())){
				p.setMark(1);
				if(congestionListenerInstalled){
					getNetwork().getStatsMeter(this, congestionListenerTix).newData(generateDataEntry(p));
				}
			}else{
				alphaBufferLoad = alphaBufferLoad + 1.0;
			}
			if(alphaListenerInstalled){
				getNetwork().getStatsMeter(this, alphaListenerTix).newData(generateDataEntry(new Packet(this,this,0),(int)alphaBufferLoad));
			}
		}
		super.receivePacket(p);
	}
	public void transmitPacket(Packet p){
		if(getNetwork().getTime() > lastAlphaBufferClearTime){
			alphaBufferLoad=Math.max(alphaBufferLoad-((double)(getNetwork().getTime()-lastAlphaBufferClearTime))*alphaPercentage*(double)capacity,0);
			lastAlphaBufferClearTime = this.getNetwork().getTime();
		}
		if(alphaListenerInstalled){
			getNetwork().getStatsMeter(this, alphaListenerTix).newData(generateDataEntry(new Packet(this,this,0),(int)alphaBufferLoad));
		}
		super.transmitPacket(p);
	}
	
	public void addAlphaListener(){
		//load listener needs to report in two places: at receive and transmit
		if (!alphaListenerInstalled){
			alphaListenerTix = getNetwork().addStatsMeter(this, new AlphaStatsMeter());
			alphaListenerInstalled = true;
		}	
	}
	public int getAlphaListenerTix(){
		if (alphaListenerInstalled){
			return alphaListenerTix;
		} else{
			return -1;
		}
	}
}