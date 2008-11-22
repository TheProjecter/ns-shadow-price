package netcomponent;

import stats.CumulPacketsStatsMeter;
import stats.MarkedPacketsStatsMeter;

public abstract class Sender extends Node{
	private Node destination;

	int cumulPacketsListenerTix;
	boolean cumulPacketsListenerInstalled;
	int markedPacketsListenerTix;
	boolean markedPacketsListenerInstalled;

	public Sender(Network network, Node destination){
		super(network);
		this.destination = destination;
		this.rateListenerInstalled = false;
		this.cumulPacketsListenerInstalled = false;
		this.markedPacketsListenerInstalled = false;
	}

	Node getDestination(){return destination;}
	
	public void addCumulPacketsListener(){
		if (!cumulPacketsListenerInstalled){
			cumulPacketsListenerTix = getNetwork().addStatsMeter(this, new CumulPacketsStatsMeter());
			cumulPacketsListenerInstalled = true;
		}
	}
	
	public void addMarkedPacketsListener(){
		if (!markedPacketsListenerInstalled){
			markedPacketsListenerTix = getNetwork().addStatsMeter(this, new MarkedPacketsStatsMeter());
			markedPacketsListenerInstalled = true;
		}
	}
}