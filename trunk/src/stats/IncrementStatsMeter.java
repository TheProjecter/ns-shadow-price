package stats;

import netcomponent.NetworkData;

import datastruct.Tuple;

public abstract class IncrementStatsMeter extends StatsMeter{
	public IncrementStatsMeter(){
		super();
	}
	public void newData(NetworkData data){
		//just add data by one
		if(!(series.isEmpty()) && data.getTime()==series.getLast().getX()){
			series.getLast().setY(series.getLast().getY()+1);
		}
		else{
			series.add(new Tuple<Integer,Integer>(data.getTime(),1));
		}
		repaint();
	}
}