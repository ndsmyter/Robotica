package brains;

import java.util.List;

import roomba.RoombaConfig;
import brains.algorithms.AlgorithmInterface;
import brains.algorithms.DummyAlgorithm;
import brains.algorithms.DummyBugAlgorithm;

import common.Utils;

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
	private MapStructure mapStructure;

	private List<Particle> particles;

	private static final byte DRIVE = 0;
	private static final byte RIGHT = 1;
	private static final byte LEFT = 2;
	private final static int SLEEP_TIME = 100;
	private AlgorithmInterface algorithm;
	private boolean stopped;

	public Brains() {
		algorithm = new DummyBugAlgorithm(this);
		reset();
		emulator = new Emulator(this);
		emulator.log("Initiating application");
	}

	public List<Particle> getParticles() {
		return particles;
	}

	public void setParticles(List<Particle> particles) {
		this.particles = particles;
	}

	public boolean isStopped() {
		return stopped;
	}

	public void stop(boolean stop) {
		this.stopped = stop;
	}

	public void reset() {
		mapStructure = new MapStructure();
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
					turn(140, true);
					break;
				case LEFT:
					turn(25, false);
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
				turn(10, false);
				DummyAlgorithm.processSensorData(this);
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

	public void turn(int degrees, boolean isTurnRight) {
		int angle = (isTurnRight ? -degrees : degrees);
		mapStructure.getPosition().dir = (mapStructure.getPosition().dir
				+ angle + 360) % 360;
		emulator.turn(degrees, isTurnRight, RoombaConfig.TURN_RADIUS_SPOT,
				RoombaConfig.DRIVE_MODE_MED);
	}

	public void drive(int distance) {
		mapStructure.setPosition(Utils.driveForward(mapStructure.getPosition(),
				distance));
		emulator.drive(distance, RoombaConfig.DRIVE_MODE_MED);
	}

	public int[] getSensorData() {
		return emulator.getSensorData();
	}

	public MapStructure getMap() {
		return mapStructure;
	}

	public void setMap(MapStructure m) {
		mapStructure.useNewMap(m);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new Brains();
	}
}
