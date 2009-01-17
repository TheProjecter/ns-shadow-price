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
	private boolean txtOutputEnabled;
	
	public Network(){
		evtQueue = new ActionQueue();
		time = 0;
		//chart = new GraphChart(this);
		netObjs = new Hashtable<NetworkComponent,LinkedList<StatsMeter>>();
		txtOutputEnabled = false;
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
	
	public void enableTxtOutput(){
		txtOutput = new MovementStatsMeter();
		txtOutputEnabled = true;
	}
	
	public void registerNetObj(NetworkComponent c){netObjs.put(c,new LinkedList<StatsMeter>());}
	
	public void outputTxt(NetworkData d){
		if(txtOutputEnabled){
			txtOutput.newData(d);
		}
	}
	
	public StatsMeter getStatsMeter(NetworkComponent c, int tix){
		return netObjs.get(c).get(tix);
	}
	
	public int addStatsMeter(NetworkComponent c, StatsMeter s){
		netObjs.get(c).add(s);
		//chart.genStatsDisplay(s);	//no graphical display
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

		int sourceBufferSize = 10;
		int sourceCapacity = 3;
		int senderRate = 1;
		int senderTransferSize = 500;
		int senderTimeout = 100;
		int linkDelay = 2;
		int senderNum = 1;

		System.out.println("Network Simulator starts...");

		Network net = new Network();
		ECNRouterRED router = new ECNRouterRED(net,sourceBufferSize,sourceCapacity);
		
		//generate TCP senders
		for(int i=1;i<=senderNum;i++){
			Resource destination = new ECNResourceRED(net,sourceBufferSize,sourceCapacity);
			SimplifiedWinBasedSender sender = new SimplifiedWinBasedSender(net,destination,senderTransferSize,senderTimeout);
			ConstantDelayLink.addfullDuplexLink(net, sender, router, linkDelay);
			ConstantDelayLink.addfullDuplexLink(net, router, destination, linkDelay);
			//sender.addCumulPacketsListener();
			sender.addWinSizeListener();
			for(int j=1; j<=senderRate; j++){
				int interTime = new java.util.Random().nextInt(100);
				net.addEvent(sender,interTime);
			}
		}

		long timeStart = System.currentTimeMillis();
		net.run();
		long timeEnd = System.currentTimeMillis();
		long timeTaken = timeEnd-timeStart;
		System.out.println("Network Simulator finishes in " + timeTaken + "ms!");
		net.statsIO();
	}
}