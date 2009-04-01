package netcomponent;

import stats.CumulPacketsStatsMeter;
import stats.MarkedPacketsStatsMeter;
import stats.DropPacketsStatsMeter;

public abstract class Sender extends Node{
	private Node destination;

	int cumulPacketsListenerTix;
	boolean cumulPacketsListenerInstalled;
	int markedPacketsListenerTix;
	boolean markedPacketsListenerInstalled;
	int dropPacketsListenerTix;
	boolean dropPacketsListenerInstalled;

	public Sender(Network network, Node destination){
		super(network);
		this.destination = destination;
		this.rateListenerInstalled = false;
		this.cumulPacketsListenerInstalled = false;
		this.markedPacketsListenerInstalled = false;
	}

	Node getDestination(){return destination;}
	
	public void announcePresence(){
		//need to overwrite this to nothing
	}
	public void announcePresenceExcept(Node source){
		//need to overwrite this to nothing
	}
	
	public void addCumulPacketsListener(){
		if (!cumulPacketsListenerInstalled){
			cumulPacketsListenerTix = getNetwork().addStatsMeter(this, new CumulPacketsStatsMeter());
			cumulPacketsListenerInstalled = true;
		}
	}
	
	public int getCumulPacketsListenerTix(){
		if (cumulPacketsListenerInstalled){
			return cumulPacketsListenerTix;
		} else{
			return -1;
		}
	}
	
	public void addMarkedPacketsListener(){
		if (!markedPacketsListenerInstalled){
			markedPacketsListenerTix = getNetwork().addStatsMeter(this, new MarkedPacketsStatsMeter());
			markedPacketsListenerInstalled = true;
		}
	}
	
	public int getMarkedPacketsListenerTix(){
		if (markedPacketsListenerInstalled){
			return markedPacketsListenerTix;
		} else{
			return -1;
		}
	}
	
	public void addDropPacketsListener(){
		if (!dropPacketsListenerInstalled){
			dropPacketsListenerTix = getNetwork().addStatsMeter(this, new DropPacketsStatsMeter());
			dropPacketsListenerInstalled = true;
		}
	}
	
	public int getDropPacketsListenerTix(){
		if (dropPacketsListenerInstalled){
			return dropPacketsListenerTix;
		} else{
			return -1;
		}
	}
}