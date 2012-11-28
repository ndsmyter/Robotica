package brains.algorithmsnew;

import java.util.List;

import brains.Brains;
import brains.MapStructure;
import brains.Particle;
import brains.algorithmsnew.measurement.FastSlamMeasurement;
import brains.algorithmsnew.measurement.MeasurementInterface;
import brains.algorithmsnew.movement.BugMovement;
import brains.algorithmsnew.movement.MovementInterface;
import brains.algorithmsnew.movement.RandomMovement;

public class Algorithm {
	private MovementInterface moi;
	private MeasurementInterface mei;

	public Algorithm(MovementInterface moi, MeasurementInterface mei) {
		this.moi = moi;
		this.mei = mei;
	}

	public void reset() {
		moi.reset();
		mei.reset();
	}

	public void run(Brains b) {
		reset();
		while (!b.isStopped()) {
			doStep(b);
		}
	}

	public void doStep(Brains b) {
		MapStructure map = b.getMap();
		int[] u = moi.move(map);
		b.moveEmulator(u);
		
		List<Particle> particles = b.getParticles();
		int[] z = b.getSensorData();
		List<Particle> newParticles = mei.measure(particles, u, z);
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
