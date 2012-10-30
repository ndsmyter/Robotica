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
		// TODO Add logic to draw this action on screen
		log("E: DRIVE (" + milliseconds + ")");
		fireStateChanged(new Event(EventType.DRIVE, milliseconds));
		roomba.drive(milliseconds);
	}

	@Override
	public void turn(int degrees, boolean turnRight, int turnMode, int driveMode) {
		// TODO Add logic to draw this action on screen
		log("E: " + (turnRight ? "RIGHT" : "LEFT") + " (" + degrees + "°)");
		fireStateChanged(new Event(EventType.TURN, degrees, turnRight));
		roomba.turn(degrees, turnRight, turnMode, driveMode);
	}

	@Override
	public void turnRight() {
		// TODO Add logic to draw this action on screen
		log("E: RIGHT");
		fireStateChanged(new Event(EventType.TURN_RIGHT));
		roomba.turn(90, true, Roomba.TURN_MODE_SPOT, Roomba.DRIVE_MODE_MED);

	}

	@Override
	public void turnLeft() {
		// TODO Auto-generated method stub
		log("E: LEFT");
		fireStateChanged(new Event(EventType.TURN_LEFT));
		roomba.turn(90, false, Roomba.TURN_MODE_SPOT, Roomba.DRIVE_MODE_MED);
	}

	@Override
	public void log(String message) {
		// TODO Add logic to draw this action on screen
		fireStateChanged(new Event(EventType.LOG, message));
		System.out.println(message);
	}
}
