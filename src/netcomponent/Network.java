package netcomponent;

import javax.swing.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Set;
import java.util.Iterator;

import datastruct.ActionQueue;
import datastruct.RandomList;

import stats.StatsMeter;
import stats.GraphChart;
import stats.MovementStatsMeter;

public class Network{
	private ActionQueue evtQueue;
	private GraphChart chart;
	int time;
	private Hashtable<NetworkComponent,LinkedList<StatsMeter>> netObjs;
	private MovementStatsMeter txtOutput;
	
	public Network(){
		evtQueue = new ActionQueue();
		time = 0;
		chart = new GraphChart(this);
		netObjs = new Hashtable<NetworkComponent,LinkedList<StatsMeter>>();
		txtOutput = new MovementStatsMeter();
	}

	public void run(){
		while(!terminate() && !(evtQueue.size()==1 && evtQueue.getHead().size()==0)){
			RandomList<NetworkComponent> rl = evtQueue.getHead();
			while(rl.size()!=0){
				rl.pick().action();
			}
			evtQueue.expungeHead();	//passage of time
			time++;
		}
	}
	
	public void registerNetObj(NetworkComponent c){netObjs.put(c,new LinkedList<StatsMeter>());}
	
	public void outputTxt(NetworkData d){
		txtOutput.newData(d);
	}
	
	public StatsMeter getStatsMeter(NetworkComponent c, int tix){
		return netObjs.get(c).get(tix);
	}
	
	public int addStatsMeter(NetworkComponent c, StatsMeter s){
		netObjs.get(c).add(s);
		chart.genStatsDisplay(s);
		return netObjs.get(c).size()-1;
	}
	
	public void statsIO(){
		Set<NetworkComponent> keys = netObjs.keySet();
		Iterator<NetworkComponent> keysIterator = keys.iterator();
		LinkedList<String> files = new LinkedList<String>();
		while(keysIterator.hasNext()){
			NetworkComponent k = keysIterator.next();
			while(!(netObjs.get(k).isEmpty())){
				StatsMeter meter = netObjs.get(k).remove();
				String filename = k.toString() + "-" + meter.getClass().getName() + ".dat";
				meter.setYLabel(meter.getYLabel() + "_" + k.getClass().getSimpleName());
				meter.outputStatsTable(filename);
				files.add(filename);
			}
		}
		try{
			//output R script file
			FileWriter fileStream = new FileWriter("result.r",false);
			BufferedWriter bufferStream = new BufferedWriter(fileStream);
			
			for(int i=0; i<files.size();i++){
				bufferStream.write("x" + i + " <- read.table('" + files.get(i) + "', header=TRUE)");
				bufferStream.newLine();
			}
			bufferStream.write("plot(x0, type='l',main='Simulation Results');");
			
			for(int i=1; i<files.size();i++){
				bufferStream.write("points(x" + i + ",type='l');");
			}
			
			bufferStream.close();
			System.out.println("Finished IO!");
		} catch(Exception e){
			System.out.println(e);
		}
	}

	public boolean terminate(){
		return false;
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

		int sourceBufferSize = 100;
		int sourceCapacity = 10;
		int senderRate = 1;
		int senderTransferSize = 500;
		int senderTimeout = 10;
		int linkDelay = 1;
		int tcpSenderNum = 1;
		int constantSenderNum = 0;

		System.out.println("Network Simulator starts...");

		Network net = new Network();
		Resource destination1 = new ECNResource(net,sourceBufferSize,sourceCapacity);
		Resource destination2 = new ECNResource(net,sourceBufferSize,sourceCapacity);
		Router router = new Router(net,sourceBufferSize,sourceCapacity);
		TCPSender sender1 = new ECN_TCPSender(net,destination1,senderRate,senderTransferSize,senderTimeout);
		TCPSender sender2 = new TCPSender(net,destination2,senderRate,senderTransferSize,senderTimeout);
		ConstantDelayLink.addfullDuplexLink(net, sender1, router, linkDelay);
		ConstantDelayLink.addfullDuplexLink(net, sender2, router, linkDelay);
		ConstantDelayLink.addfullDuplexLink(net, router, destination1, linkDelay);
		ConstantDelayLink.addfullDuplexLink(net, router, destination2, linkDelay);
		for(int j=1; j<=senderRate; j++){
			net.addEvent(sender1);
			net.addEvent(sender2);
		}
		sender1.addCumulPacketsListener();
		sender2.addCumulPacketsListener();
		
	/*	
		//generate TCP senders
		for(int i=1;i<=tcpSenderNum;i++){
			TCPSender sender = new TCPSender(net,destination,senderRate,senderTransferSize,senderTimeout);
			sender.addMarkedPacketsListener();
			sender.addRateListener();
			sender.addCumulPacketsListener();
			ConstantDelayLink.addfullDuplexLink(net, sender, destination, linkDelay);
			for(int j=1; j<=senderRate; j++){
				net.addEvent(sender);
			}
		}
		
		//generate constant rate senders
		for (int i=1;i<=constantSenderNum;i++){
			ConstantRateSender sender = new ConstantRateSender(net,destination,senderRate,senderTransferSize,senderTimeout);
			sender.addMarkedPacketsListener();
			new ConstantDelayLink(net,sender,destination,linkDelay);
			new ConstantDelayLink(net,destination,sender,linkDelay);
			for(int j=1; j<=senderRate; j++){
				net.addEvent(sender);
			}
		}
	*/
		net.run();
		System.out.println("Network Simulator finishes!");
		net.statsIO();
	}
}