package stats;

import netcomponent.NetworkData;
import datastruct.Tuple;

public class LoadStatsMeter extends OverwriteStatsMeter{

	public LoadStatsMeter(){
		super();
		setYLabel("Load");
	}
}