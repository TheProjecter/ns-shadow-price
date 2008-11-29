package netcomponent;
import java.util.LinkedList;
import stats.*;

public class Resource extends Node{
	LinkedList<Packet> buffer;
	private int bufferSize;
	private int capacity;
	private int lastScheduledActionTime;
	private int dropPacketsListenerTix;
	private boolean dropPacketsListenerInstalled;
	private int loadListenerTix;
	private boolean loadListenerInstalled;

	public Resource(Network network, int bufferSize, int capacity){
		super(network);
		buffer=new LinkedList<Packet>();
		this.bufferSize=bufferSize;
		this.capacity=capacity;
		lastScheduledActionTime=0;
		dropPacketsListenerInstalled=false;
		loadListenerInstalled=false;
	}
	
	public void action(){
		Packet p = buffer.remove();
		transmitPacket(new AckPacket(this,p.getSender(),p.getSeqNum()));
	}

	public void receivePacket(Packet p){
		if(buffer.size()<bufferSize){	//check if full yet
			buffer.add(p);
			lastScheduledActionTime = Math.max(lastScheduledActionTime+1,capacity*(getNetwork().getTime()));
			getNetwork().addEvent(this,lastScheduledActionTime/capacity-getNetwork().getTime());
			if(loadListenerInstalled){
				getNetwork().getStatsMeter(this, loadListenerTix).newData(generateDataEntry(p,buffer.size()));
			}
		}
		else{
			//detect dropped packets
			if (dropPacketsListenerInstalled){
				getNetwork().getStatsMeter(this, dropPacketsListenerTix).newData(generateDataEntry(p));
			}
		}
	}
	
	public int getBufferSize(){return bufferSize;}
	
	public void addDropPacketsListener(){
		if (!dropPacketsListenerInstalled){
			dropPacketsListenerTix = getNetwork().addStatsMeter(this, new DropPacketsStatsMeter());
			dropPacketsListenerInstalled = true;
		}
	}
	public void addLoadListener(){
		//load listener needs to report in two places: at receive and transmit
		if (!loadListenerInstalled){
			loadListenerTix = getNetwork().addStatsMeter(this, new LoadStatsMeter());
			loadListenerInstalled = true;
		}	
	}
}