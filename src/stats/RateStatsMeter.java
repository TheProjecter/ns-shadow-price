package stats;

import javax.swing.*;

import netcomponent.NetworkData;
import netcomponent.Sender;


import java.awt.Graphics;
import java.awt.Color;
import java.awt.Font;
import java.util.LinkedList;

public class RateStatsMeter extends StatsMeter{
	private LinkedList<NetworkData> series;

	public RateStatsMeter(){
		super();
		series = new LinkedList<NetworkData>();

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
				if(series.size()>1)
					for(int i=1;i<series.size();i++) g.drawLine(xCoord(series.get(i-1).getTime()),yCoord(i-1),xCoord(series.get(i).getTime()),yCoord(i));
						
			}
		};

		chart.setBackground(Color.BLACK);
		add(chart);
		setBounds(0,0,WINDOW_WIDTH,WINDOW_HEIGHT);
		setVisible(true);
	}

	public void newData(NetworkData data){
		System.out.println(data);
		series.add(data);
		repaint();
	}

	public int xCoord(int x){return CHART_X+x;}
	public int yCoord(int y){return CHART_Y+CHART_HEIGHT-y;}
}