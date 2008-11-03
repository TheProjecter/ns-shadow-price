package netcomponent;
public class NetworkData{
	private final NetworkComponent interSource;
	private final NetworkComponent interDestination;
	private final NetworkComponent endSource;
	private final NetworkComponent endDestination;
	private final int time;
	private final int seqNum;
	private final int mark;

	public NetworkData(Packet p, NetworkComponent interSource, NetworkComponent interDestination, int time){
		this.interSource=interSource;
		this.interDestination=interDestination;
		endSource=p.getSender();
		endDestination=p.getRecipient();
		this.time=time;
		seqNum=p.getSeqNum();
		mark=p.getMark();
	}

	public NetworkComponent getInterSource(){return interSource;}
	public NetworkComponent getInterDestination(){return interDestination;}
	public NetworkComponent getEndSource(){return endSource;}
	public NetworkComponent getEndDestination(){return endDestination;}
	public int getTime(){return time;}
	public int getSeqNum(){return seqNum;}
	public int getMark(){return mark;}
	public String toString(){
		return "Time " + time + " - Packet(sender:" + endSource + ",recipient:" + endDestination + ",seqnum:" + seqNum + ",mark:" + mark + ") moved to " + interDestination + ".";
	}
}