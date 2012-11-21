package brains.algorithms;

import brains.Brains;

public interface AlgorithmInterface {
	public void run(Brains b);

	public void doStep(Brains b);

	public void reset();
}
