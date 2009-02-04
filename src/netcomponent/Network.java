package netcomponent;

import javax.swing.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Set;
import java.util.Iterator;
import java.util.Random;

import datastruct.ActionQueue;
import datastruct.RandomList;
import datastruct.Tuple;

import stats.StatsMeter;
import stats.GraphChart;
import stats.MovementStatsMeter;
import stats.IOStatsTable;

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
	
	public void statsIO(String graphType){
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
		IOStatsTable.outputScript(files,graphType);
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
		
		int sourceBufferSize = 40;
		int sourceCapacity = 3;
		int senderRate = 1;
		int senderTransferSize = 500;
		int senderTimeout = 320;
		double linkDelay = 50.0;
		int senderNum = 40;
		double vqAlpha = 0.5;
		int utility = 10;

		int trials=1;
		
		LinkedList<LinkedList<Tuple<Double,Double>>> results = new LinkedList<LinkedList<Tuple<Double,Double>>>();
		results.add(new LinkedList<Tuple<Double,Double>>());
		
		for(int i=1; i<=trials;i++){
			LinkedList<Integer> randomStarts = new LinkedList<Integer>();
			for(int j=0;j<senderNum;j++){
				randomStarts.add(new Random().nextInt(10));
			}
			for(double j=0.0;j<=1.0;j+=0.1){
				results.get(0).add(runScript(sourceBufferSize,sourceCapacity,senderRate,senderTransferSize,senderTimeout,linkDelay,senderNum,j,utility,randomStarts));
			}
		}

		LinkedList<String> files = new LinkedList<String>();
		for(int i=0; i<results.size();i++){
			String filename="serviceTime";
			IOStatsTable.outputTable(results.get(i), filename + i + ".dat", "x", "y");
			files.add(filename + i + ".dat");
		}
		IOStatsTable.outputScript(files, "l");

	}
	
	private static Tuple<Double,Double> runScript(int sourceBufferSize,int sourceCapacity,int senderRate,int senderTransferSize,int senderTimeout,double linkDelay,int senderNum,double vqAlpha,int utility, LinkedList<Integer> randomStarts){
		System.out.println("Network Simulator starts...");
		
		Network net = new Network();
		ECNRouterVQ router = new ECNRouterVQ(net,sourceBufferSize,sourceCapacity);
		router.setAlpha(vqAlpha);
		
		LinkedList<Sender> senders = new LinkedList<Sender>();
		
		for(int count=1; count<=senderNum; count++){
			ECNResourceVQ destination = new ECNResourceVQ(net,sourceBufferSize,100);
			destination.setAlpha(vqAlpha);
			senders.add(new ECNWinBasedSenderWTP(net,destination,senderTransferSize,senderTimeout,utility));
			PoissonDelayLink.addfullDuplexLink(net, senders.getLast(), router, linkDelay);
			PoissonDelayLink.addfullDuplexLink(net, router, destination, linkDelay);
			if(count<0){
				net.addEvent(senders.getLast(),0);
			}else{
				net.addEvent(senders.getLast(),randomStarts.get(count-1));
			}
			senders.getLast().addCumulPacketsListener();
		}
		//senders.get(2).addMarkedPacketsListener();
		//senders.get(1).addMarkedPacketsListener();
		//senders.get(0).addMarkedPacketsListener();
		//((WinBasedSender)(senders.get(0))).addWinSizeListener();
		//((WinBasedSender)(senders.get(1))).addWinSizeListener();
		//((WinBasedSender)(senders.get(2))).addWinSizeListener();

		long timeStart = System.currentTimeMillis();
		net.run();
		long timeEnd = System.currentTimeMillis();
		long timeTaken = timeEnd-timeStart;
		System.out.println("Network Simulator finishes in " + timeTaken + "ms!");
		
		double output=0.0;
		if(senders.getFirst().dropPacketsListenerInstalled){
			for(Sender s: senders){
				if (s.dropPacketsListenerInstalled && !(net.getStatsMeter(s, s.getDropPacketsListenerTix()).getSeries().isEmpty()))
					output+=net.getStatsMeter(s, s.getDropPacketsListenerTix()).getLatestTuple().getY();
			}
		} else if(senders.getFirst().markedPacketsListenerInstalled){
			for(Sender s: senders){
				if (s.markedPacketsListenerInstalled && !(net.getStatsMeter(s, s.getMarkedPacketsListenerTix()).getSeries().isEmpty()))
					output+=net.getStatsMeter(s, s.getMarkedPacketsListenerTix()).getLatestTuple().getY();
			}
		} else if(senders.getFirst().cumulPacketsListenerInstalled){
			for(Sender s: senders){
				if (s.cumulPacketsListenerInstalled && !(net.getStatsMeter(s, s.getCumulPacketsListenerTix()).getSeries().isEmpty()))
					output+=net.getStatsMeter(s, s.getCumulPacketsListenerTix()).getLatestTuple().getX();
			}
		}
		//net.statsIO("l");
		output = output/senderNum;
		return new Tuple<Double,Double>(vqAlpha,output);
	}
}