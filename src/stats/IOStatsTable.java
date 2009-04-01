package stats;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.LinkedList;

import datastruct.Tuple;

public class IOStatsTable {
	public static <E extends Comparable<E>,F> void outputTable(LinkedList<Tuple<E,F>> tSeries, String filename, String xLabel, String yLabel){
		try{
			FileWriter fileStream = new FileWriter(filename,false);
			BufferedWriter bufferStream = new BufferedWriter(fileStream);
			
			bufferStream.write(xLabel + "\t" + yLabel);
			bufferStream.newLine();
			
			for(int i=0; i<tSeries.size();i++){
				bufferStream.write(tSeries.get(i).getX() + "\t" + tSeries.get(i).getY());
				bufferStream.newLine();
			}
			bufferStream.close();
		} catch(Exception e){
			System.out.println(e);
		}
	}
	
	public static void outputScript(LinkedList<String> tableFilenames, String graphType){
		try{
			FileWriter fileStream = new FileWriter("result.r",false);
			BufferedWriter bufferStream = new BufferedWriter(fileStream);
			
			for(int i=0; i<tableFilenames.size();i++){
				bufferStream.write("x" + i + " <- read.table('" + tableFilenames.get(i) + "', header=TRUE);");
				if(i==0){
					bufferStream.write("plot(x" + i + ",type='" + graphType + "',main='Simulation Results',col=" + (i+1) + ");");
				}else{
					bufferStream.write("points(x" + i + ",type='" + graphType + "',col=" + (i+1) + ");");
				}
				bufferStream.newLine();
			}
			bufferStream.close();
		} catch(Exception e){
			System.out.println(e);
		}
	}
}
