package stats;

import javax.swing.*;

import netcomponent.NetworkData;

public abstract class StatsMeter extends JFrame{
	static final int MARGIN_X=30;
	static final int MARGIN_Y=30;
	static final int WINDOW_WIDTH=800;
	static final int WINDOW_HEIGHT=600;
	static final int GRID_SCALE_X=100;
	static final int GRID_SCALE_Y=100;
	static final int CHART_X=MARGIN_X;
	static final int CHART_Y=MARGIN_Y;
	static final int CHART_WIDTH=WINDOW_WIDTH-2*MARGIN_X;
	static final int CHART_HEIGHT=WINDOW_HEIGHT-100;
	
	public abstract void newData(NetworkData data);
}