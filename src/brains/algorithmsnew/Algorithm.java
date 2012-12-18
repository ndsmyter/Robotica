package brains.algorithmsnew;

import java.util.List;

import brains.Brains;
import brains.MapStructure;
import brains.Particle;
import brains.algorithmsnew.explore.BugExplore;
import brains.algorithmsnew.explore.ExploreAlgorithmInterface;
import brains.algorithmsnew.explore.RandomExplore;
import brains.algorithmsnew.slam.FastSLAM;
import brains.algorithmsnew.slam.SLAMAlgorithmInterface;

public class Algorithm {
	private ExploreAlgorithmInterface explorer;
	private SLAMAlgorithmInterface slam;

	public Algorithm(ExploreAlgorithmInterface explore,
			SLAMAlgorithmInterface slam) {
		this.explorer = explore;
		this.slam = slam;
	}

	public void reset() {
		explorer.reset();
		slam.reset();
	}

	public void run(Brains b) {
		// reset();
		while (!b.isStopped()) {
			doStep(b);
		}
	}

	public ExploreAlgorithmInterface getExplorer() {
		return explorer;
	}

	public void doStep(Brains b) {
		List<Particle> particles = b.getParticles();

		MapStructure bestMap = b.getBestParticleMap();

		int[] u = explorer.explore(bestMap);
		b.move(u);

		int[] z = b.getSensorData();
		List<Particle> newParticles = slam.execute(particles, u, z);
		b.setParticles(newParticles);
	}

	public static Algorithm getFastSlamRandom() {
		return new Algorithm(new RandomExplore(), new FastSLAM());
	}

	public static Algorithm getFastSlamBug(Brains b) {
		Stopper stopper = new Stopper(b);
		return new Algorithm(new BugExplore(stopper), new FastSLAM());
	}

}
