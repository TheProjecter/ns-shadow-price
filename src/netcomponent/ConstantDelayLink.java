package netcomponent;
import java.util.LinkedList;

public class ConstantDelayLink extends Link{
	private int delay;

	public ConstantDelayLink(Network network, Node source, Node destination, int delay){
		super(network,source,destination);
		this.delay=delay;
	}
	
	public int nextDelay(){return delay;}
	
	public double getMeanDelay(){return (double)delay;}
	public double getDelayVariance(){return 0.0;}
	
	public static void addfullDuplexLink(Network network, Node n1, Node n2, int delay){
		new ConstantDelayLink(network, n1, n2, delay);
		new ConstantDelayLink(network, n2, n1, delay);
		n1.announcePresence();
		n2.announcePresence();
	}
}