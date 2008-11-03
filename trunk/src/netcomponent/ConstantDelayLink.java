package netcomponent;
import java.util.LinkedList;
import stats.*;

public class ConstantDelayLink extends Link{
	private int delay;
	private LinkedList<Packet> packetList;
	private int statsMeterTicket;

	public ConstantDelayLink(Network network, Node source, Node destination, int delay){
		super(network,source,destination);
		this.delay=delay;
		packetList = new LinkedList<Packet>();
		statsMeterTicket = getNetwork().addStatsMeter(new RateStatsMeter());
		getNetwork().getStatsMeter(statsMeterTicket).setTitle(this.toString());
	}

	public void action(){
		transmitPacket(packetList.remove());
	}

	public void receivePacket(Packet p){
		packetList.add(p);
		getNetwork().addEvent(this,delay);
		getNetwork().getStatsMeter(statsMeterTicket).newData(new NetworkData(p,this,this,getNetwork().getTime()));
	}

	public void transmitPacket(Packet p){
		getDestination().receivePacket(p);
	}
}