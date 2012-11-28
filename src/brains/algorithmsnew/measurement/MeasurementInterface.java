package brains.algorithmsnew.measurement;

import java.util.List;

import brains.Particle;

public interface MeasurementInterface {
	// eventueel een iets strictere interface
	public List<Particle> measure(List<Particle> particles, int[] u, int[] z);
	public void reset();
}
