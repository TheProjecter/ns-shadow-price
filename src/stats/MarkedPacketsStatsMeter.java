package stats;

public class MarkedPacketsStatsMeter extends CumulPacketsStatsMeter{
	public MarkedPacketsStatsMeter(){
		super();
		setYLabel("Marked_Packets");
	}
}