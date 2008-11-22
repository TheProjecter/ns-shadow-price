package stats;

import netcomponent.NetworkData;
import java.util.LinkedList;
import datastruct.Tuple;

public class MovementStatsMeter extends StatsMeter{
	private LinkedList<Tuple<Integer,Integer>> series;

	public MovementStatsMeter(){
		super();
	}

	public void newData(NetworkData data){
		System.out.println("Time " + data.getTime() + ": Packet(Sender: " + data.getEndSource() + ", Recipient: " + data.getEndSource() + ", Seq: " + data.getSeqNum() + ", mark: " + data.getMark() + ") moves to " + data.getInterDestination());
	}
	
	public void outputStatsTable(String filename){
		//needs to override with empty method.
		//no IO for these
	}
}