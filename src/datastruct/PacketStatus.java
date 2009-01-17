package datastruct;

import java.util.PriorityQueue;
import java.util.LinkedList;

public class PacketStatus {
	
	private class PendingPacket{
		private int creationTime;
		private int serialNum;
		public PendingPacket(int time, int serialNum){
			this.creationTime = time;
			this.serialNum = serialNum;
		}
		public boolean isExpired(int time){
			return (time>creationTime+timeout);
		}
		public int getSerialNum(){return serialNum;}
	}
	
	private PriorityQueue<Integer> unsentPackets;
	private LinkedList<PendingPacket> pendingPackets;
	private int transferSize;
	private int timeout;
	
	public PacketStatus(int transferSize){
		this.unsentPackets = new PriorityQueue<Integer>();
		this.transferSize = transferSize;
		this.pendingPackets = new LinkedList<PendingPacket>();
	}
	
	public boolean isCompleted(){
		return (unsentPackets.isEmpty() && pendingPackets.isEmpty());
	}
	
	public boolean moreUnsentPackets(){return unsentPackets.isEmpty();}
	
	public int nextSerialNum(int time){
		int nextInt = unsentPackets.remove();
		if (unsentPackets.isEmpty()){
			if(nextInt != transferSize-1){
				unsentPackets.add(nextInt+1);
			}
		}
		pendingPackets.add(new PendingPacket(time,nextInt));
		return nextInt;
	}
	
	public void refreshExpiry(int time){
		for(int i=0;i<pendingPackets.size();i++){
			if (pendingPackets.get(i).isExpired(time)){
				int serialNum = pendingPackets.remove(i).getSerialNum();
				unsentPackets.add(serialNum);
				i--;
			}
		}
	}
	public void packetAcknowledged(int serialNum){
		for(int i=0;i<pendingPackets.size();i++){
			if(pendingPackets.get(i).getSerialNum() == serialNum){
				pendingPackets.remove(i);
				break;
			}
		}
	}
}
