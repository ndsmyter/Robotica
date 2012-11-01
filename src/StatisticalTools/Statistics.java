package StatisticalTools;

import java.io.IOException;

import javax.comm.NoSuchPortException;
import javax.comm.PortInUseException;

import roomba.SerialIO;

public class Statistics 
{
	double[] data;
	double size;    
	
	public static void main(String[] args){
		try {
			SerialIO serial = new SerialIO("COM9");
			int[] input = serial.getInputData(1000);
			double[] data = new double[input.length];
			
			for(int i=0; i<input.length; i++){
				data[i] = (input[i] * 5)/ 1024.0;  //Omzetten naar voltage
			}
			
			System.out.println("Mean: " + new Statistics(data).getMean());
			System.out.println("Var : " + new Statistics(data).getVariance());
		} catch (NoSuchPortException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (PortInUseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public Statistics(double[] data) 
	{
		this.data = data;
		size = data.length;
	}   

	double getMean()
	{
		double sum = 0.0;
		for(double a : data)
			sum += a;
				return sum/size;
	}

	double getVariance()
	{
		double mean = getMean();
		double temp = 0;
		for(double a :data)
			temp += (mean-a)*(mean-a);
				return temp/size;
	}

	double getStdDev()
	{
		return Math.sqrt(getVariance());
	}
}