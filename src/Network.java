import javax.swing.*;

public class Network{
	private ActionQueue evtQueue;
	private StatsMeter stats;
	int time;

	public Network(StatsMeter stats){
		evtQueue = new ActionQueue();
		time = 0;
		this.stats = stats;
	}

	public void initialize(){}

	public void run(){
		while(!terminate() || !(evtQueue.size()==1 && evtQueue.getHead().size()==0)){
			RandomList<NetworkComponent> rl = evtQueue.getHead();
			while(rl.size()!=0){
				rl.pick().action();
			}
			evtQueue.expungeHead();	//passage of time
			time++;
		}
	}

	public void addStat(NetworkData data){stats.newData(data);}

	public boolean terminate(){
		return time>=20;
	}

	public int getTime(){return time;}

	public void addEvent(NetworkComponent actor){evtQueue.addActor(actor);}
	public void addEvent(NetworkComponent actor, int delay){evtQueue.addActor(actor,delay);}

	public static void main(String args[]){
		try {
			// Set System L&F
			UIManager.setLookAndFeel(
			UIManager.getSystemLookAndFeelClassName());
		}catch(Exception e) {}

		int sourceBufferSize = 20;
		int sourceCapacity = 3;
		int senderRate = 1;
		int senderTransferSize = 500;
		int senderTimeout = 30;
		int linkDelay = 1;

		System.out.println("Network Simulator...");

		Network net = new Network(new RateStatsMeter());
		Source destination = new Source(net,sourceBufferSize,sourceCapacity);
		TCPSender sender = new TCPSender(net,destination,senderRate,senderTransferSize,senderTimeout);
		//ConstantRateSender sender = new ConstantRateSender(net,destination,senderRate,senderTransferSize,senderTimeout);
		ConstantDelayLink link1 = new ConstantDelayLink(net,sender,destination,linkDelay);
		ConstantDelayLink link2 = new ConstantDelayLink(net,destination,sender,linkDelay);

		for(int i=1;i<=senderRate;i++)
			net.addEvent(sender);

		net.run();
	}
}