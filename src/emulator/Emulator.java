package emulator;

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
	public void drive(int milliseconds) {
		log("E: DRIVE (" + milliseconds + ")");
		fireStateChanged(true, new Event(EventType.DRIVE, milliseconds));
		roomba.drive(milliseconds);
	}

	@Override
	public void turn(int degrees, boolean turnRight, int turnMode, int driveMode) {
		log("E: " + (turnRight ? "RIGHT" : "LEFT") + " (" + degrees + "°)");
		fireStateChanged(true, new Event(EventType.TURN, degrees, turnRight));
		roomba.turn(degrees, turnRight, turnMode, driveMode);
	}

	@Override
	public void turnRight() {
		log("E: RIGHT");
		fireStateChanged(true, new Event(EventType.TURN_RIGHT));
		roomba.turn(90, true, Roomba.TURN_MODE_SPOT, Roomba.DRIVE_MODE_MED);

	}

	@Override
	public void turnLeft() {
		log("E: LEFT");
		fireStateChanged(true, new Event(EventType.TURN_LEFT));
		roomba.turn(90, false, Roomba.TURN_MODE_SPOT, Roomba.DRIVE_MODE_MED);
	}

	@Override
	public void log(String message) {
		fireStateChanged(true, new Event(EventType.LOG, message));
		System.out.println(message);
	}
}
