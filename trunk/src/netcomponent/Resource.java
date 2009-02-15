package netcomponent;
import java.util.LinkedList;
import stats.*;

public class Resource extends Node{
	LinkedList<Packet> buffer;
	private int bufferSize;
	int capacity;
	private int lastScheduledActionTime;
	private int bufferDropListenerTix;
	private boolean bufferDropListenerInstalled;
	private int loadListenerTix;
	private boolean loadListenerInstalled;
	int congestionListenerTix;
	boolean congestionListenerInstalled;

	public Resource(Network network, int bufferSize, int capacity){
		super(network);
		buffer=new LinkedList<Packet>();
		this.bufferSize=bufferSize;
		this.capacity=capacity;
		lastScheduledActionTime=capacity;
		bufferDropListenerInstalled=false;
		loadListenerInstalled=false;
		congestionListenerInstalled=false;
	}
	
	public void action(){
		Packet p = buffer.remove();
		if(p.getRecipient().equals(this)){
			//if for me, reply
			AckPacket ackP = new AckPacket(this,p.getSender(),p.getSeqNum());
			if (p.getMark()==1){ackP.setMark(1);}
			transmitPacket(ackP);
		} else{
			//otherwise, forward
			transmitPacket(p);
		}
	}

	public void receivePacket(Packet p){
		if(!bufferFull()){	//check if full yet, but assume it is yours
			buffer.add(p);
			lastScheduledActionTime = Math.max(lastScheduledActionTime+1,capacity*(1+getNetwork().getTime()));
			getNetwork().addEvent(this,lastScheduledActionTime/capacity-getNetwork().getTime());
			if(loadListenerInstalled){
				getNetwork().getStatsMeter(this, loadListenerTix).newData(generateDataEntry(p,buffer.size()));
			}
		}
		else{
			//detect dropped packets
			if (bufferDropListenerInstalled){
				getNetwork().getStatsMeter(this, bufferDropListenerTix).newData(generateDataEntry(p));
			}
		}
	}
	
	public void transmitPacket(Packet p){
		if(loadListenerInstalled){
			getNetwork().getStatsMeter(this, loadListenerTix).newData(generateDataEntry(p,buffer.size()));
		}
		super.transmitPacket(p);
	}
	
	public int getBufferSize(){return bufferSize;}
	
	public boolean bufferFull(){return (buffer.size()>=bufferSize);}
	
	public void addBufferDropListener(){
		if (!bufferDropListenerInstalled){
			bufferDropListenerTix = getNetwork().addStatsMeter(this, new BufferDropStatsMeter());
			bufferDropListenerInstalled = true;
		}
	}
	public int getBufferDropListenerTix(){
		if (bufferDropListenerInstalled){
			return bufferDropListenerTix;
		} else{
			return -1;
		}
	}
	
	public void addLoadListener(){
		//load listener needs to report in two places: at receive and transmit
		if (!loadListenerInstalled){
			loadListenerTix = getNetwork().addStatsMeter(this, new LoadStatsMeter());
			loadListenerInstalled = true;
		}	
	}
	public int getLoadListenerTix(){
		if (loadListenerInstalled){
			return loadListenerTix;
		} else{
			return -1;
		}
	}
	public void addCongestionListener(){
		//load listener needs to report in two places: at receive and transmit
		if (!congestionListenerInstalled){
			congestionListenerTix = getNetwork().addStatsMeter(this, new CongestionStatsMeter());
			congestionListenerInstalled = true;
		}	
	}
	public int getCongestionListenerTix(){
		if (congestionListenerInstalled){
			return congestionListenerTix;
		} else{
			return -1;
		}
	}
}