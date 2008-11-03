package netcomponent;

import javax.swing.*;
import java.util.LinkedList;

import datastruct.ActionQueue;
import datastruct.RandomList;

import stats.RateStatsMeter;
import stats.StatsMeter;
import stats.GraphChart;

public class Network{
	private ActionQueue evtQueue;
	private GraphChart chart;
	int time;

	public Network(){
		evtQueue = new ActionQueue();
		time = 0;
		chart = new GraphChart();
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
	
	public StatsMeter getStatsMeter(int i){return chart.getStatsMeter(i);}
	
	public int addStatsMeter(StatsMeter s){return chart.addStatsMeter(s);}

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

		int sourceBufferSize = 10;
		int sourceCapacity = 2;
		int senderRate = 2;
		int senderTransferSize = 500;
		int senderTimeout = 10;
		int linkDelay = 1;

		System.out.println("Network Simulator...");

		Network net = new Network();
		Source destination = new Source(net,sourceBufferSize,sourceCapacity);
		//TCPSender sender = new TCPSender(net,destination,senderRate,senderTransferSize,senderTimeout);
		ConstantRateSender sender = new ConstantRateSender(net,destination,senderRate,senderTransferSize,senderTimeout);
		ConstantDelayLink link1 = new ConstantDelayLink(net,sender,destination,linkDelay);
		ConstantDelayLink link2 = new ConstantDelayLink(net,destination,sender,linkDelay);

		for(int i=1;i<=senderRate;i++)
			net.addEvent(sender);

		net.run();
	}
}