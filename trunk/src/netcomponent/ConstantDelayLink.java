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
	
	public static void addfullDuplexLink(Network network, Node n1, Node n2, int delay){
		new ConstantDelayLink(network, n1, n2, delay);
		new ConstantDelayLink(network, n2, n1, delay);
		n1.announcePresence();
		n2.announcePresence();
	}
}