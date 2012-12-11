package brains.algorithmsnew;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import brains.Particle;

public class RouletteWheelSelection {
	private Random random;

	public RouletteWheelSelection() {
		random = new Random();
	}

	private Particle nextRandomParticle(List<Particle> particles,
			double sumWeight) {
		double x = random.nextDouble();
		int i = 0;

		double normProb = particles.get(i).getWeight() / sumWeight;
		int particleCount = particles.size();
		while (x > normProb && i - 2 < particleCount) {
			x -= normProb;
			i++;
			normProb = particles.get(i).getWeight() / sumWeight;
		}
		return particles.get(i).clone();
	}

	private double calculateSumWeight(List<Particle> particles) {
		double sumWeight = 0;
		for (Particle p : particles) {
			sumWeight += p.getWeight();
		}
		return sumWeight;
	}

	public Particle nextRandomParticle(List<Particle> particles) {
		double sumWeight = calculateSumWeight(particles);

		return nextRandomParticle(particles, sumWeight);
	}

	public ArrayList<Particle> nextRandomParticles(List<Particle> particles,
			int n) {
		double sumWeight = calculateSumWeight(particles);

		ArrayList<Particle> newParticles = new ArrayList<Particle>();
		for (int i = 0; i < n; i++)
			newParticles.add(nextRandomParticle(particles, sumWeight));
		return newParticles;
	}
}