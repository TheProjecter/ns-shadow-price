package netcomponent;

public class SimplifiedTCPSender extends Sender{
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
	int rate;
	int transferSize;
	int timeout;
	int lastSecuredActionTime;
	PacketStatus[] ps;

	public SimplifiedTCPSender(Network network, Node destination, int rate, int transferSize, int timeout){
		super(network, destination);
		this.rate=rate;
		this.transferSize=transferSize;
		this.timeout=timeout;
		setName("NoName");
		ps = new PacketStatus[transferSize];
		for(int i=0;i<ps.length;i++) ps[i]=new PacketStatusUnsent();
		lastSecuredActionTime=0;
	}

	public void action(){
		//refresh ps to check for expiry
		if(getNetwork().getTime()+1>lastSecuredActionTime){
			//NO NEED to adjust lastSecuredActionTime yet! see bottom of this method
			boolean decreaseSpeed = false;
			for(int i=0; i<ps.length; i++)
				if (ps[i].isExpired()){
					ps[i]=new PacketStatusUnsent();
					decreaseSpeed = true;
				}
			if (decreaseSpeed){rate=Math.max(rate/2, 1);}
			else{
				rate = rate+1;
			}
		}
		// find packet to transmit
		for(int i=0;i<ps.length;i++){
			if(ps[i] instanceof PacketStatusUnsent){
				ps[i] = new PacketStatusPending();
				transmitPacket(new Packet(this,getDestination(),i));
				System.out.println("rate:"+rate);
				break;
			}
		}

		//if still packets unsent/pending, put itself in eventqueue for next transmission
		if(getNetwork().getTime()+1>lastSecuredActionTime){
			lastSecuredActionTime = getNetwork().getTime()+1;
			int count=0;
			for(PacketStatus pStatus : ps){
				if (!(pStatus instanceof PacketStatusSent)){
					getNetwork().addEvent(this,1);	//put on queue
					count++;
					if (count>=rate) break;
				}
			}
		}
	}

	public void receivePacket(Packet p){
		//check if expired, if ok, packetsent
		if(ps[p.getSeqNum()] instanceof PacketStatusPending && !(ps[p.getSeqNum()].isExpired())){
			ps[p.getSeqNum()] = new PacketStatusSent();
			if (cumulPacketsListenerInstalled){
				getNetwork().getStatsMeter(this, cumulPacketsListenerTix).newData(generateDataEntry(p));
			}
			if (markedPacketsListenerInstalled){
				getNetwork().getStatsMeter(this, markedPacketsListenerTix).newData(generateDataEntry(p));
			}
		}
	}
}