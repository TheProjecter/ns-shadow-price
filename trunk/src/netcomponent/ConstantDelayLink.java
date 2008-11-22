package netcomponent;
import java.util.LinkedList;

public class ConstantDelayLink extends Link{
	private int delay;
	private LinkedList<Packet> packetList;

	public ConstantDelayLink(Network network, Node source, Node destination, int delay){
		super(network,source,destination);
		this.delay=delay;
		packetList = new LinkedList<Packet>();
	}

	public void action(){
		transmitPacket(packetList.remove());
	}

	public void receivePacket(Packet p){
		packetList.add(p);
		getNetwork().addEvent(this,delay);
	}
}