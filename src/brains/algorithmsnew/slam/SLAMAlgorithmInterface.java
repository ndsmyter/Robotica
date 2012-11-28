package brains.algorithmsnew.slam;

import java.util.List;

import brains.Particle;

public interface SLAMAlgorithmInterface {
	// eventueel een iets strictere interface
	public List<Particle> execute(List<Particle> particles, int[] u, int[] z);
	public void reset();
}
