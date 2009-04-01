package netcomponent;

import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Set;
import java.util.Iterator;

import datastruct.ActionQueue;
import datastruct.RandomList;

import stats.StatsMeter;
import stats.MovementStatsMeter;
import stats.IOStatsTable;

public class Network{
	private ActionQueue evtQueue;
	int time;
	private Hashtable<NetworkComponent,LinkedList<StatsMeter>> netObjs;
	private MovementStatsMeter txtOutput;
	private boolean txtOutputEnabled;
	
	public Network(){
		evtQueue = new ActionQueue();
		time = 0;
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
		return netObjs.get(c).size()-1;
	}
	
	public void statsIO(String graphType){
		Set<NetworkComponent> keys = netObjs.keySet();
		Iterator<NetworkComponent> keysIterator = keys.iterator();
		LinkedList<String> files = new LinkedList<String>();
		while(keysIterator.hasNext()){
			NetworkComponent k = keysIterator.next();
			int index = 0;
			while(!(netObjs.get(k).isEmpty())){
				StatsMeter meter = netObjs.get(k).remove();
				String filename = k.toString() + "-" + meter.getClass().getName() + (index++) +  ".dat";
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
}