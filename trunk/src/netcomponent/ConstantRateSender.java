package netcomponent;

import stats.*;

public class ConstantRateSender extends Sender{
	//inner classes definitions
	private abstract class PacketStatus{
		public boolean isExpired(){return false;}
	}
	private class PacketStatusUnsent extends PacketStatus{}
	private class PacketStatusPending extends PacketStatus{
		private int creationTime;
		public PacketStatusPending(){creationTime = getNetwork().getTime();}
		public boolean isExpired(){
			return (getNetwork().getTime()>creationTime+timeout);
		}
	}
	private class PacketStatusSent extends PacketStatus{}

	//variables
	private int rate;
	private int transferSize;
	private int timeout;
	private PacketStatus[] ps;

	public ConstantRateSender(Network network, Node destination, int rate, int transferSize, int timeout){
		super(network, destination);
		this.rate=rate;
		this.transferSize=transferSize;
		this.timeout=timeout;
		setName("NoName");
		ps = new PacketStatus[transferSize];
		for(int i=0;i<ps.length;i++) ps[i]=new PacketStatusUnsent();
		statsMeterTicket = getNetwork().addStatsMeter(new RateStatsMeter());
		getNetwork().getStatsMeter(statsMeterTicket).setTitle(this.toString());
	}

	public void action(){
		//refresh ps to check for expiry
		for(int i=0; i<ps.length; i++)
			if (ps[i].isExpired())
				ps[i]=new PacketStatusUnsent();

		// find packet to transmit
		for(int i=0;i<ps.length;i++){
			if(ps[i] instanceof PacketStatusUnsent){
				ps[i] = new PacketStatusPending();
				transmitPacket(new Packet(this,getDestination(),i));
				break;
			}
		}

		//if still packets unsent/pending, put itself in eventqueue for next transmission
		for(PacketStatus pStatus : ps){
			if (!(pStatus instanceof PacketStatusSent)){
					getNetwork().addEvent(this,1);	//put on queue
				break;
			}
		}
		//stats work...
	}

	public void receivePacket(Packet p){
		//check if expired, if ok, packetsent
		if(ps[p.getSeqNum()] instanceof PacketStatusPending && !(ps[p.getSeqNum()].isExpired())){
			ps[p.getSeqNum()] = new PacketStatusSent();
			getNetwork().getStatsMeter(statsMeterTicket).newData(new NetworkData(p,this,this,getNetwork().getTime()));
		}
	}

	public void transmitPacket(Packet p){
		// no routing table, everything delivered to the same channel...
		getConnection().receivePacket(p);
	}

	public Link getConnection(){return getAllConnections().getFirst();}
}