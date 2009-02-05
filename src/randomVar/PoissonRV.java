package randomVar;

public class PoissonRV extends DiscreteRV {
	double lambda;
	
	public PoissonRV(double lambda){
		this.lambda=Math.abs(lambda);	//ensure all numbers must be +ve
	}
	public int getNextVar() {
		//generate poisson RV
		int n=1;
		double ac = new java.util.Random().nextDouble();
		while(ac>=Math.exp(-lambda)){
			n++;
			ac = ac * (new java.util.Random().nextDouble());
		}
		return n-1;
	}
	
	public double getMean(){return lambda;}
	public double getVariance(){return lambda;}
}
