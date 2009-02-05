package netcomponent;

import stats.WinSizeStatsMeter;

public class Copy_2_of_SimplifiedWinBasedSender extends Sender{
	//inner classes definitions
	abstract class PacketStatus{
		public boolean isExpired(){return false;}
	}
	class PacketStatusUnsent extends PacketStatus{}
	class PacketStatusPending extends PacketStatus{
		private int creationTime;
		public PacketStatusPending(){creationTime = getNetwork().getTime();}
		public boolean isExpired(){
			return (getNetwork().getTime()>creationTime+timeout);
		}
	}
	class PacketStatusSent extends PacketStatus{}

	//variables
	int winSize;
	int transferSize;
	int timeout;
	PacketStatus[] ps;
	int winSizeListenerTix;
	boolean winSizeListenerInstalled;

	public Copy_2_of_SimplifiedWinBasedSender(Network network, Node destination, int transferSize, int timeout){
		super(network, destination);
		this.winSize=1;
		this.transferSize=transferSize;
		this.timeout=timeout;
		setName("NoName");
		ps = new PacketStatus[transferSize];
		for(int i=0;i<ps.length;i++) ps[i]=new PacketStatusUnsent();
	}

	public void action(){
		//adjust WindowSize
		refreshTimeouts();
		
		//register winsize if needed
		if(winSizeListenerInstalled){getNetwork().getStatsMeter(this, winSizeListenerTix).newData(generateDataEntry(new Packet(this,this,0),winSize));}
		
		// see if win space left to transmit
		int unAck=0;
		for(int i=0;i<ps.length;i++){
			if(ps[i] instanceof PacketStatusPending){
				unAck++;
			}
		}
		
		//if so, find packet to transmit
		if (unAck<winSize){
			for(int i=0;i<ps.length;i++){
				if(ps[i] instanceof PacketStatusUnsent){
					ps[i] = new PacketStatusPending();
					transmitPacket(new Packet(this,getDestination(),i));
					//System.out.println("transmit" + i);
					break;
				}
			}
		}

		//if still packets unsent/pending, put itself in eventqueue for next transmission
		for(PacketStatus pStatus : ps){
			if (!(pStatus instanceof PacketStatusSent)){
				getNetwork().addEvent(this,1);	//put on queue
				break;
			}
		}
	}

	public void receivePacket(Packet p){
		//check if expired, if ok, packetsent
		if(ps[p.getSeqNum()] instanceof PacketStatusPending && !(ps[p.getSeqNum()].isExpired())){
			ps[p.getSeqNum()] = new PacketStatusSent();
			adjustWinSizeAck(p);
			if (cumulPacketsListenerInstalled){
				getNetwork().getStatsMeter(this, cumulPacketsListenerTix).newData(generateDataEntry(p));
			}
			if (markedPacketsListenerInstalled && p.getMark()==1){
				getNetwork().getStatsMeter(this, markedPacketsListenerTix).newData(generateDataEntry(p));
			}
		}
	}
	
	public void addWinSizeListener(){
		if (!winSizeListenerInstalled){
			winSizeListenerTix = getNetwork().addStatsMeter(this, new WinSizeStatsMeter());
			winSizeListenerInstalled = true;
		}
	}
	
	public int getWinSizeListenerTix(){
		if (winSizeListenerInstalled){
			return winSizeListenerTix;
		} else{
			return -1;
		}
	}
	
	void refreshTimeouts(){
		//refresh ps to check for expiry
		for(int i=0; i<ps.length; i++){
			if (ps[i].isExpired()){
				ps[i]=new PacketStatusUnsent();
				if(dropPacketsListenerInstalled){
					getNetwork().getStatsMeter(this, dropPacketsListenerTix).newData(generateDataEntry(new Packet(this,this,0)));
				}
				adjustWinSizeLoss();
			}
		}
	}
	
	void adjustWinSizeLoss(){
		winSize=Math.max(winSize/2, 1);
	}
	void adjustWinSizeAck(Packet p){
		winSize = winSize+1;
	}
}