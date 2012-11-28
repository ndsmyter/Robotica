package brains.algorithmsnew;

import java.util.List;

import brains.Brains;
import brains.MapStructure;
import brains.Particle;
import brains.algorithmsnew.explore.BugMovement;
import brains.algorithmsnew.explore.ExploreAlgorithmInterface;
import brains.algorithmsnew.explore.RandomMovement;
import brains.algorithmsnew.slam.FastSlamMeasurement;
import brains.algorithmsnew.slam.SLAMAlgorithmInterface;

public class Algorithm {
	private ExploreAlgorithmInterface explorer;
	private SLAMAlgorithmInterface slam;

	public Algorithm(ExploreAlgorithmInterface explore, SLAMAlgorithmInterface slam) {
		this.explorer = explore;
		this.slam = slam;
	}

	public void reset() {
		explorer.reset();
		slam.reset();
	}

	public void run(Brains b) {
		reset();
		while (!b.isStopped()) {
			doStep(b);
		}
	}

	public void doStep(Brains b) {
		MapStructure map = b.getMap();
		int[] u = explorer.explore(map);
		b.moveEmulator(u);
		
		List<Particle> particles = b.getParticles();
		int[] z = b.getSensorData();
		List<Particle> newParticles = slam.execute(particles, u, z);
		b.setParticles(newParticles);
		
		b.setMap(newParticles.get(0).getMap());
	}

	public static Algorithm getFastSlamRandom() {
		return new Algorithm(new RandomMovement(), new FastSlamMeasurement());
	}
	
	public static Algorithm getFastSlamBug(Brains b) {
		Stopper stopper = new Stopper(b);
		return new Algorithm(new BugMovement(stopper), new FastSlamMeasurement());
	}

}
