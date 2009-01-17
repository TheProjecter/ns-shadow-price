package stats;

import netcomponent.NetworkData;
import datastruct.Tuple;

public class WinSizeStatsMeter extends OverwriteStatsMeter{

	public WinSizeStatsMeter(){
		super();
		setYLabel("WinSize");
	}
}