package stats;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JPanel;

import java.awt.Color;
import java.util.LinkedList;

import netcomponent.Network;

public class GraphChart extends JFrame{
	//private LinkedList<StatsMeter> stats;
	private int WIN_HEIGHT = 600;
	private int WIN_WIDTH = 1000;
	private JDesktopPane jdp;
	private Network net;
	
	public GraphChart(Network net){
		super();
		this.net=net;
		jdp = new JDesktopPane();
		//stats = new LinkedList<StatsMeter>();
		
		setBounds(0,0,WIN_WIDTH,WIN_HEIGHT);
		setTitle("Network Simulator");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
		setContentPane(jdp);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		jdp.setBackground(Color.DARK_GRAY);
	}
	
	public void genStatsDisplay(StatsMeter s){
		if (!(s instanceof MovementStatsMeter))
			jdp.add(s);
	}
	
	//public void genStatsTable(int statsMeterNum, String filename){stats.get(statsMeterNum).outputStatsTable(filename);}
}
