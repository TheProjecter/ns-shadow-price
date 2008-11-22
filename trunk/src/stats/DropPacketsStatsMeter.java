package stats;

public class DropPacketsStatsMeter extends IncrementStatsMeter{
	public DropPacketsStatsMeter(){
		super();
		setYLabel("Dropped_Packets");
	}
}