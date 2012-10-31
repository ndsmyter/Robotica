package brains;

import java.awt.Point;
import java.util.ArrayList;

import roomba.RoombaConfig;

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
	private ArrayList<Point> obstacles = new ArrayList<Point>();

	private static final byte DRIVE = 0;
	private static final byte RIGHT = 1;
	private static final byte LEFT = 2;

	// Change this if you want to debug the application
	private byte[] movements = { LEFT, LEFT, DRIVE, DRIVE, DRIVE, DRIVE, DRIVE,
			DRIVE, LEFT, DRIVE, LEFT, DRIVE, LEFT, DRIVE, LEFT, DRIVE, LEFT,
			DRIVE, LEFT, DRIVE, LEFT, DRIVE, LEFT, DRIVE, RIGHT, DRIVE, LEFT,
			DRIVE, LEFT, DRIVE, LEFT, DRIVE, LEFT, DRIVE, LEFT, DRIVE, LEFT,
			DRIVE, LEFT, DRIVE, LEFT, DRIVE, DRIVE, DRIVE, DRIVE, DRIVE, DRIVE };
	private final static int SLEEP_TIME = 100;

	public Brains() {
		emulator = new Emulator();

		emulator.log("Initiating application");
		// testDriving(movements);
		testSensors();
	}

	private void testDriving(byte[] movements) {
		ArrayList<Point> obstacle = new ArrayList<Point>();
		obstacle.add(new Point(-10, -10));
		obstacle.add(new Point(-10, -11));
		obstacle.add(new Point(-10, -12));
		obstacle.add(new Point(-10, -13));
		emulator.addObstacle(obstacle);

		// Just drive around to test the emulator and Roomba
		try {
			Thread.sleep(SLEEP_TIME);

			for (byte movement : movements) {
				switch (movement) {
				case DRIVE:
					emulator.drive(200, RoombaConfig.DRIVE_MODE_MED);
					break;
				case RIGHT:
					emulator.turn(140, true, RoombaConfig.TURN_RADIUS_SPOT,
							RoombaConfig.DRIVE_MODE_MED);
					break;
				case LEFT:
					emulator.turn(25, false, RoombaConfig.TURN_RADIUS_SPOT,
							RoombaConfig.DRIVE_MODE_MED);
					break;
				}
				Thread.sleep(SLEEP_TIME);
			}

		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void testSensors() {
		currentState = new RobotState(0, 0, 0);
		// Just drive around to test the emulator and Roomba
		try {
			drive(100);
			Thread.sleep(SLEEP_TIME);
			for (int i = 0; i < 36; i++) {
				turn(10, false);
				// drive(100);
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

	private void turn(int degrees, boolean isTurnRight) {
		int angle = (isTurnRight ? -degrees : degrees);
		move(currentState.x, currentState.y,
				(currentState.dir + angle + 360) % 360);
		emulator.turn(degrees, isTurnRight, RoombaConfig.TURN_RADIUS_SPOT,
				RoombaConfig.DRIVE_MODE_MED);
	}

	private void drive(int distance) {
		double theta = Math.PI * currentState.dir / 180;
		int newx = (int) (currentState.x + distance * Math.cos(theta) + 0.5);
		int newy = (int) (currentState.y + distance * Math.sin(theta) + 0.5);
		move(newx, newy, currentState.dir);
		emulator.drive(distance, RoombaConfig.DRIVE_MODE_MED);
	}

	private void move(int x, int y, int dir) {
		currentState.x = x;
		currentState.y = y;
		currentState.dir = dir;
	}

	private void processSensorData() {
		int[] data = emulator.getSensorData();
		ArrayList<Point> obstacle = new ArrayList<Point>();
		for (int i = 0; i < 5; i++) {
			obstacle.add(Utils.sensorDataToPoint(currentState, data[i],
					RoombaConfig.SENSORS[i]));
		}
		obstacles.addAll(obstacle);
		emulator.addObstacle(obstacle);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new Brains();
	}
}
