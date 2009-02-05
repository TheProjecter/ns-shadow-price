package stats;

public class BufferDropStatsMeter extends IncrementStatsMeter {
	public BufferDropStatsMeter(){
		super();
		setYLabel("Buffer_Dropped_Packets");
	}
}
