package stats;

public class DropPacketsStatsMeter extends CumulPacketsStatsMeter{
	public DropPacketsStatsMeter(){
		super();
		setYLabel("Dropped_Packets");
	}
}