package netcomponent;

import netcomponent.SimplifiedTCPSender.PacketStatusPending;
import netcomponent.SimplifiedTCPSender.PacketStatusSent;

public class ECN_TCPSender extends TCPSender{
	//variables
	private boolean markedPack;

	public ECN_TCPSender(Network network, Node destination, int rate, int transferSize, int timeout){
		super(network, destination,rate,transferSize,timeout);
		this.markedPack=false;
	}

	public void action(){
		//refresh ps to check for expiry
		if(getNetwork().getTime()+1>lastSecuredActionTime){
			//NO NEED to adjust lastSecuredActionTime yet! see bottom of this method
			boolean decreaseSpeed = markedPack;
			for(int i=0; i<ps.length; i++){
				if (ps[i].isExpired()){
					ps[i]=new PacketStatusUnsent();
					thresholdWin=rate;
					decreaseSpeed = true;
				}
			}
			if (decreaseSpeed){
				rate=Math.max(rate/2, 1);
			} else{
				if(thresholdWin==0 || rate<thresholdWin){
					//slow start phase
					rate = rate+accumAck;
					accumAck=0;
				} else if (rate>=thresholdWin && accumAck>=rate) {
					//congestion avoidance
					rate = rate+1;
					accumAck=0;
				}
			}
			markedPack=false;
		}
		
		// find packet to transmit
		for(int i=0;i<ps.length;i++){
			if(ps[i] instanceof PacketStatusUnsent){
				ps[i] = new PacketStatusPending();
				transmitPacket(new Packet(this,getDestination(),i));
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
			markedPack = true;
		}
		super.receivePacket(p);
	}
}