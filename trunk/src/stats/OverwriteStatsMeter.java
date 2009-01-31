package stats;

import netcomponent.NetworkData;

import datastruct.Tuple;

public abstract class OverwriteStatsMeter extends StatsMeter{
	public OverwriteStatsMeter(){
		super();
		setYLabel("Dropped_Packets");
	}
	public void newData(NetworkData data){
		if(!(series.isEmpty()) && data.getTime()==series.getLast().getX()){
			series.getLast().setY(Math.max(data.getAux(),series.getLast().getY()));
		}else{
			series.add(new Tuple<Integer,Integer>(data.getTime(),data.getAux()));
		}
		repaint();
	}
}