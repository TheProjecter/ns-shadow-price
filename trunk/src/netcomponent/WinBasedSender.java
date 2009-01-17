package netcomponent;

public class WinBasedSender extends SimplifiedWinBasedSender{
	//variables
	int threshold;
	int accumAck;

	public WinBasedSender(Network network, Node destination, int transferSize, int timeout){
		super(network, destination, transferSize, timeout);
		this.threshold=0;
		this.accumAck=0;
	}

	public void action(){
		//refresh ps to check for expiry
		boolean decreaseSpeed = false;
		for(int i=0; i<ps.length; i++)
			if (ps[i].isExpired()){
				ps[i]=new PacketStatusUnsent();
				decreaseSpeed = true;
				break;
			}
		if (decreaseSpeed){
			threshold=winSize;
			winSize=Math.max(winSize/2, 1);
		}
		else{
			if (threshold==0 || winSize<=threshold){
				//slow start
				winSize = winSize+accumAck;
				accumAck=0;
			}
			else{
				if(accumAck==winSize){
					winSize++;
					accumAck=0;
				}
			}
		}
		
		if(winSizeListenerInstalled){
			getNetwork().getStatsMeter(this, winSizeListenerTix).newData(generateDataEntry(new Packet(this,this,0),winSize));
		}
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
			accumAck++;
			if (cumulPacketsListenerInstalled){
				getNetwork().getStatsMeter(this, cumulPacketsListenerTix).newData(generateDataEntry(p));
			}
			if (markedPacketsListenerInstalled){
				getNetwork().getStatsMeter(this, markedPacketsListenerTix).newData(generateDataEntry(p));
			}
		}
	}
}