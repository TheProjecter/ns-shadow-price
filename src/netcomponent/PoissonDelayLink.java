package netcomponent;

public class PoissonDelayLink extends Link {
	private randomVar.PoissonRV delayRV; 
	
	public PoissonDelayLink(Network network, Node source, Node destination, double meanDelay){
		super(network,source,destination);
		this.delayRV = new randomVar.PoissonRV(meanDelay);
	}
	
	public int nextDelay(){return delayRV.getNextVar();}
	
	public double getMeanDelay(){return delayRV.getMean();}
	public double getDelayVariance(){return delayRV.getVariance();}
	
	public static void addfullDuplexLink(Network network, Node n1, Node n2, double meanDelay){
		new PoissonDelayLink(network, n1, n2, meanDelay);
		new PoissonDelayLink(network, n2, n1, meanDelay);
		n1.announcePresence();
		n2.announcePresence();
	}
}
