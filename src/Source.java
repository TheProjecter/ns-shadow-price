import java.util.LinkedList;

public class Source extends Node{
	private LinkedList<Packet> buffer;
	private int bufferSize;
	private int capacity;
	private int lastScheduledActionTime;

	public Source(Network network, int bufferSize, int capacity){
		super(network);
		buffer=new LinkedList<Packet>();
		this.bufferSize=bufferSize;
		this.capacity=capacity;
		lastScheduledActionTime=0;
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
			getNetwork().addStat(new NetworkData(p,this,this,getNetwork().getTime()));
		}
	}

	public void transmitPacket(Packet p){
		getConnection().receivePacket(p);
	}

	public Link getConnection(){return getAllConnections().getFirst();}
}