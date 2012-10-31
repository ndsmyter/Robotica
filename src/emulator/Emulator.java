package emulator;

import java.awt.Point;
import java.util.ArrayList;

import roomba.Roomba;
import emulator.interfaces.EmulatorInterface;
import emulator.interfaces.ModelInterface;

/**
 * This class will be used as an interface for the brains and the Roomba robot
 * 
 * @author Nicolas
 * 
 */
public class Emulator extends ModelInterface implements EmulatorInterface {

	private Roomba roomba;

	public Emulator() {
		roomba = new Roomba(this);
		new EmulatorWindow(this);
	}

	@Override
	public void drive(int millimeters, int driveMode) {
		log("E: DRIVE (" + millimeters + ")");
		fireStateChanged(true, new Event(EventType.DRIVE, millimeters,
				driveMode));
		roomba.drive(millimeters, driveMode);
	}

	@Override
	public void turn(int degrees, boolean turnRight, int turnMode, int driveMode) {
		log("E: " + (turnRight ? "RIGHT" : "LEFT") + " (" + degrees + "°)");
		fireStateChanged(true, new Event(EventType.TURN, -1, degrees,
				turnRight, driveMode));
		roomba.turn(degrees, turnRight, turnMode, driveMode);
	}

	public void addObstacle(ArrayList<Point> obstacle) {
		log("E: Obstacle");
		fireStateChanged(true, new Event(EventType.OBSTACLE, obstacle));
	}

	public int[] getSensorData() {
		// Stub
		int[] sensordata = { 500, 500, 500, 500, 500 };
		return sensordata;
	}

	@Override
	public void log(String message) {
		fireStateChanged(true, new Event(EventType.LOG, message));
		System.out.println(message);
	}
}
