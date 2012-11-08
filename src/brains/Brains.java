package brains;

import java.awt.Point;
import java.util.ArrayList;

import roomba.RoombaConfig;
import brains.algorithms.AlgorithmInterface;
import brains.algorithms.DummyAlgorithm;
import brains.interfaces.ObstacleListener;

import common.RobotState;
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
	private RobotState currentState;
	private MapStructure mapStructure;
	private static final byte DRIVE = 0;
	private static final byte RIGHT = 1;
	private static final byte LEFT = 2;
	private final static int SLEEP_TIME = 100;

	private AlgorithmInterface algorithm;

	private boolean stopped;

	public Brains() {
		algorithm = new DummyAlgorithm();
		reset();
		emulator = new Emulator(this);
		emulator.log("Initiating application");
	}

	public boolean isStopped() {
		return stopped;
	}

	public void stop(boolean stop) {
		this.stopped = stop;
	}

	public void reset() {
		currentState = new RobotState(0, 0, 0);
		mapStructure = new MapStructure();
		algorithm.reset();
		stop(false);
	}

	public void restart() {
		if (isStopped())
			// Continue previous execution
			stop(false);

		// testDriving();
		// testSensors();
		algorithm.run(this);
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
				processSensorData();
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
		currentState.dir = (currentState.dir + angle + 360) % 360;
		emulator.turn(degrees, isTurnRight, RoombaConfig.TURN_RADIUS_SPOT,
				RoombaConfig.DRIVE_MODE_MED);
	}

	public void drive(int distance) {
		currentState = Utils.driveForward(currentState, distance);
		emulator.drive(distance, RoombaConfig.DRIVE_MODE_MED);
	}

	public void processSensorData() {
		int[] data = emulator.getSensorData();
		for (int i = 0; i < 5; i++) {
			RobotState sensorState = Utils.getSensorState(currentState,
					RoombaConfig.SENSORS[i]);
			Point measurement = Utils.sensorDataToPoint(currentState, data[i],
					RoombaConfig.SENSORS[i]);
			ArrayList<Point> path = Utils.getPath(sensorState, new RobotState(
					measurement.x, measurement.y, sensorState.dir));
			for (Point p : path) {
//				double newValue = mapStructure.get(Utils.pointToGrid(p)) - 0.10;
//				if (newValue > 1)
//					newValue = 1;
//				if (newValue < 0)
//					newValue = 0;
//				mapStructure.put(Utils.pointToGrid(p), newValue);
				mapStructure.put(Utils.pointToGrid(p), 0);
			}
			if (data[i] < 800)
				mapStructure.put(Utils.pointToGrid(measurement), 1);
		}
	}

	public void addObstacleListener(ObstacleListener listener) {
		mapStructure.addObstacleListener(listener);
	}

	public MapStructure getMap() {
		return mapStructure;
	}

	public RobotState getCurrentState() {
		return currentState;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new Brains();
	}
}
