package netcomponent;
public class NetworkData{
	private final NetworkComponent interSource;
	private final NetworkComponent interDestination;
	private final NetworkComponent endSource;
	private final NetworkComponent endDestination;
	private final int time;
	private final int seqNum;
	private final int mark;
	private final int aux;

	public NetworkData(Packet p, NetworkComponent interSource, NetworkComponent interDestination, int time){
		this.interSource=interSource;
		this.interDestination=interDestination;
		this.endSource=p.getSender();
		this.endDestination=p.getRecipient();
		this.time=time;
		this.seqNum=p.getSeqNum();
		this.mark=p.getMark();
		this.aux=0;
	}
	public NetworkData(Packet p, NetworkComponent interSource, NetworkComponent interDestination, int time, int aux){
		this.interSource=interSource;
		this.interDestination=interDestination;
		this.endSource=p.getSender();
		this.endDestination=p.getRecipient();
		this.time=time;
		this.seqNum=p.getSeqNum();
		this.mark=p.getMark();
		this.aux=aux;
	}

	public NetworkComponent getInterSource(){return interSource;}
	public NetworkComponent getInterDestination(){return interDestination;}
	public NetworkComponent getEndSource(){return endSource;}
	public NetworkComponent getEndDestination(){return endDestination;}
	public int getTime(){return time;}
	public int getSeqNum(){return seqNum;}
	public int getMark(){return mark;}
	public int getAux(){return aux;}
	public String toString(){
		return "Time " + time + " - Packet(sender:" + endSource + ",recipient:" + endDestination + ",seqnum:" + seqNum + ",mark:" + mark + ") moved to " + interDestination + ".";
	}
}