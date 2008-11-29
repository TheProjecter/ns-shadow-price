package stats;

import netcomponent.NetworkData;
import datastruct.Tuple;

public class CumulPacketsStatsMeter extends StatsMeter{

	public CumulPacketsStatsMeter(){
		super();
		setYLabel("SentPackets");
	}

	public void newData(NetworkData data){
		if(!(series.isEmpty()) && data.getTime()==series.getLast().getX()){
			series.getLast().setY(series.getLast().getY()+1);
		}else{
			int last=0;
			if(!(series.isEmpty())){
				last = series.getLast().getY();
			}
			series.add(new Tuple<Integer,Integer>(data.getTime(),last+1));
		}
		repaint();
	}
}