package StatisticalTools;

import roomba.Roomba;

public class Statistics 
{
	double[] data;
	double size;    

	public static void main(String[] args){

		Roomba r = new Roomba(null);
		double[] data = new double[10 * 3];
		byte[] in = new byte[3];
		long before = System.currentTimeMillis();
		for(int i=0; i<data.length; i++){
			data[i] = (r.getRawSensorData(new byte[]{0})[0]);
		}
		System.out.println(System.currentTimeMillis() - before);

		System.out.println("Mean: " + new Statistics(data).getMean());
		System.out.println("Var : " + new Statistics(data).getVariance());
		
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