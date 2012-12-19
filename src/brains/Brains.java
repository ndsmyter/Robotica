package brains;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import roomba.RoombaConfig;
import brains.algorithmsnew.Algorithm;
import brains.algorithmsnew.explore.BugExplore;
import brains.algorithmsnew.explore.ExploreAlgorithmInterface;

import common.Config;
import common.RobotState;

import emulator.Emulator;
import emulator.Event;
import emulator.interfaces.ListenerInterface;

/**
 * This class will start everything up, and will eventually control everything
 * that happens to the robot. So that is the reason why this class is called
 * Brains
 * 
 * @author Nicolas
 * 
 */
public class Brains implements ListenerInterface {

	private final Emulator emulator;
	private List<Particle> particles;
	private static final byte DRIVE = 0;
	private static final byte RIGHT = 1;
	private static final byte LEFT = 2;
	private final static int SLEEP_TIME = 100;
	private Algorithm algorithm;
	private boolean stopped;

	public Brains() {
		particles = new ArrayList<Particle>();
		// algorithm = Algorithm.getFastSlamRandom();
		algorithm = Algorithm.getFastSlamBug(this);
		reset();
		emulator = new Emulator(this);
		emulator.log("===============================================================================");
		emulator.log("Initiating application");

		// Make the roomba notify us it is ready to begin playing
		emulator.setSongs();
		emulator.singSong(0);
	}

	public void log(String message) {
		emulator.log(message);
	}

	public Point getGoal() {
		ExploreAlgorithmInterface explorer = algorithm.getExplorer();
		if (explorer instanceof BugExplore)
			return ((BugExplore) explorer).getGoal();
		else
			return null;
	}

	public void setGoal(Point p) {
		ExploreAlgorithmInterface explorer = algorithm.getExplorer();
		if (explorer instanceof BugExplore)
			((BugExplore) explorer).setGoal(p);
	}

	public ArrayList<Point> getGoalPath() {
		ExploreAlgorithmInterface explorer = algorithm.getExplorer();
		if (explorer instanceof BugExplore)
			return ((BugExplore) explorer).getGoalPath();
		else
			return null;
	}

	public List<Particle> getParticles() {
		return particles;
	}

	public void setParticles(List<Particle> particles) {
		this.particles = particles;
		emulator.updateParticlesOfViewers();
	}

	public boolean isStopped() {
		return stopped;
	}

	public void stop(boolean stop) {
		this.stopped = stop;
	}

	public void reset() {
		particles.clear();
		for (int i = 0; i < Config.NUMBER_OF_PARTICLES; i++) {
			particles.add(new Particle(new MapStructure(), 1.0));
		}
		algorithm.reset();
		stop(false);
	}

	public void restart() {
		if (isStopped()) // Continue previous execution
		{
			stop(false);
		}

		// testDriving();
		// testSensors();
		algorithm.run(this);
	}

	public void doStep() {
		algorithm.doStep(this);
	}

	private void testDriving() {
		// Change this if you want to debug the application
		byte[] movements = { LEFT, LEFT, DRIVE, DRIVE, DRIVE, DRIVE, DRIVE,
				DRIVE, LEFT, DRIVE, LEFT, DRIVE, LEFT, DRIVE, LEFT, DRIVE,
				LEFT, DRIVE, LEFT, DRIVE, LEFT, DRIVE, LEFT, DRIVE, RIGHT,
				DRIVE, LEFT, DRIVE, LEFT, DRIVE, LEFT, DRIVE, LEFT, DRIVE,
				LEFT, DRIVE, LEFT, DRIVE, LEFT, DRIVE, LEFT, DRIVE, DRIVE,
				DRIVE, DRIVE, DRIVE, DRIVE };

		// Just drive around to test the emulator and Roomba
		try {
			Thread.sleep(SLEEP_TIME);

			for (byte movement : movements) {
				switch (movement) {
				case DRIVE:
					drive(150);
					break;
				case RIGHT:
					turn(-140);
					break;
				case LEFT:
					turn(25);
					break;
				}
				Thread.sleep(SLEEP_TIME);
			}

		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void testSensors() {
		// Turn around axis to test the sensors
		try {
			Thread.sleep(SLEEP_TIME);
			for (int i = 0; i < 36; i++) {
				turn(10);
				// DummyAlgorithm.processSensorData(this);
				Thread.sleep(SLEEP_TIME);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void stateChanged(Event event) {
		// An event happened to the robot which has to be parsed
	}

	public void move(int[] u) {
		if (u[0] != 0) {
			drive(u[0]);
		}
		if (u[1] != 0) {
			turn(u[1]);
		}
	}

	public void moveEmulator(int[] u) {
		// if (u[0] != 0)
		// emulator.drive(u[0], RoombaConfig.DRIVE_MODE_MED);
		// if (u[1] != 0)
		// emulator.turn(u[1], false, RoombaConfig.TURN_RADIUS_SPOT,
		// RoombaConfig.DRIVE_MODE_MED);
		move(u);
	}

	public void turn(int degrees) {
		emulator.turn(degrees, RoombaConfig.TURN_RADIUS_SPOT,
				RoombaConfig.DRIVE_MODE_MED);
	}

	public void drive(int distance) {
		emulator.drive(distance, RoombaConfig.DRIVE_MODE_MED);
	}

	public int[] getSensorData() {
		return emulator.getSensorData();
	}

	public MapStructure getParticleMap(int i) {
		return particles.get(i).getMap();
	}

	public MapStructure getBestParticleMap() {
		return getMedian();
	}

	public MapStructure getMedian() {
		ArrayList<Integer> xs = new ArrayList<Integer>();
		ArrayList<Integer> ys = new ArrayList<Integer>();
		ArrayList<Integer> dirs = new ArrayList<Integer>();
		for (int i = 0; i < particles.size(); i++) {
			Particle p = particles.get(i);
			RobotState state = p.getMap().getPosition();
			xs.add(state.x);
			ys.add(state.y);
			dirs.add(state.dir);
			dirs.add(state.dir + 360);
		}
		Collections.sort(xs);
		Integer medianx = xs.get(xs.size() / 2);
		Collections.sort(ys);
		Integer mediany = ys.get(ys.size() / 2);
		Collections.sort(dirs);
		Integer mediandir = dirs.get(dirs.size() / 2);

		double dist = Integer.MAX_VALUE;
		Particle selected = particles.get(0);
		for (int i = 0; i < particles.size(); i++) {
			Particle p = particles.get(i);
			RobotState state = p.getMap().getPosition();
			int xPart = (medianx - state.x) * (medianx - state.x);
			int yPart = (mediany - state.y) * (mediany - state.y);
			int dirPart = (mediandir - state.dir) * (mediandir - state.dir);
			double tmpdist = Math.sqrt(xPart + yPart + dirPart);
			if (tmpdist < dist) {
				dist = tmpdist;
				selected = p;
			}
		}

		return selected.getMap();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new Brains();
	}
}
