package stats;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.LinkedList;

import javax.swing.*;

import datastruct.Tuple;

import netcomponent.NetworkData;

public abstract class StatsMeter extends JInternalFrame{
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
	
	LinkedList<Tuple<Integer,Integer>> series;
	
	private String xLabel;
	private String yLabel;
	
	private int zoomX;
	private int zoomY;
	
	private String rGraphType;
	
	StatsMeter(){
		super("Rate",true,false,true,true);
		zoomX = 1;
		zoomY = 1;
		xLabel = "Time";
		yLabel = "Y";
		rGraphType = "l";

		series = new LinkedList<Tuple<Integer,Integer>>();

		JPanel chart = new JPanel(){
			public void paintComponent(Graphics g){
				//drawgrid
				super.paintComponent(g);
				g.setColor(Color.WHITE);
				g.drawRect(CHART_X,CHART_Y,CHART_WIDTH,CHART_HEIGHT);
				g.fillRect(CHART_X,CHART_Y+CHART_HEIGHT+1,CHART_WIDTH+3,3);
				g.fillRect(CHART_X-3,CHART_Y-3,3,CHART_HEIGHT+7);

				g.setColor(Color.GRAY);
				for(int i=0; i*100<CHART_WIDTH;i++)
					g.drawLine(CHART_X+100*i,CHART_Y,CHART_X+100*i,CHART_Y+CHART_HEIGHT);
				for(int i=0; i*100<CHART_HEIGHT;i++)
					g.drawLine(CHART_X,CHART_Y+CHART_HEIGHT-100*i,CHART_X+CHART_WIDTH,CHART_Y+CHART_HEIGHT-100*i);

				//label axis
				g.setColor(Color.WHITE);
				g.setFont(new Font("Courier New",Font.PLAIN,14));
				g.drawString("Time",CHART_X+CHART_WIDTH-50,CHART_Y+CHART_HEIGHT+20);
				g.drawString("Cumulative Packets Transmitted",5,20);

				//draw lines
				for(int i=0;i<series.size();i++) g.drawLine(xCoord(series.get(i).getX()), yCoord(0), xCoord(series.get(i).getX()), yCoord(series.get(i).getY()));
			}
		};

		chart.setBackground(Color.BLACK);
		add(chart);
		setBounds(0,0,WINDOW_WIDTH,WINDOW_HEIGHT);
		setVisible(true);
	}

	public abstract void newData(NetworkData data);
	
	public void outputStatsTable(String filename){
		try{
			FileWriter fileStream = new FileWriter(filename,false);
			BufferedWriter bufferStream = new BufferedWriter(fileStream);
			
			bufferStream.write(xLabel + " " + yLabel);
			bufferStream.newLine();
			
			for(int i=0; i<series.size();i++){
				bufferStream.write(series.get(i).getX() + " " + series.get(i).getY());
				bufferStream.newLine();
			}
			bufferStream.close();
		} catch(Exception e){
			System.out.println(e);
		}
	}
	
	public int xCoord(int x){return CHART_X+x*zoomX;}
	public int yCoord(int y){return CHART_Y+CHART_HEIGHT-y*zoomY;}
	
	public void setZoomX(int scale){zoomX=scale;}
	public void setZoomY(int scale){zoomY=scale;}
	
	public String getXLabel(){return xLabel;}
	public String getYLabel(){return yLabel;}
	public void setXLabel(String x){xLabel = x;}
	public void setYLabel(String y){yLabel = y;}
	public String getRGraphType(){return rGraphType;}
	public void setRGraphType(String t){rGraphType=t;}
}