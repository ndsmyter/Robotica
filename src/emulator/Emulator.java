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
	private final static int QUARTER_TURN_TIME = 1000;

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
	public void turn(int milliseconds, boolean right) {
		// TODO Add logic to draw this action on screen
		log("E: " + (right ? "RIGHT" : "LEFT") + " (" + milliseconds + ")");
		fireStateChanged(new Event(EventType.TURN, milliseconds, right));
		roomba.turn(milliseconds, right);
	}

	@Override
	public void turnRight() {
		// TODO Add logic to draw this action on screen
		log("E: RIGHT");
		fireStateChanged(new Event(EventType.TURN_RIGHT));
		roomba.turn(QUARTER_TURN_TIME, true);

	}

	@Override
	public void turnLeft() {
		// TODO Auto-generated method stub
		log("E: LEFT");
		fireStateChanged(new Event(EventType.TURN_LEFT));
		roomba.turn(QUARTER_TURN_TIME, false);
	}

	@Override
	public void log(String message) {
		// TODO Add logic to draw this action on screen
		fireStateChanged(new Event(EventType.LOG, message));
		System.out.println(message);
	}
}
