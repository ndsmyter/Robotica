package emulator;

import roomba.Roomba;
import roomba.RoombaConfig;
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

	@Override
	public void turnRight() {
		log("E: RIGHT");
		fireStateChanged(true, new Event(EventType.TURN_RIGHT, 0, 90, true,
				RoombaConfig.DRIVE_MODE_MED));
		roomba.turn(90, true, RoombaConfig.TURN_MODE_SPOT,
				RoombaConfig.DRIVE_MODE_MED);

	}

	@Override
	public void turnLeft() {
		log("E: LEFT");
		fireStateChanged(true, new Event(EventType.TURN_LEFT, 0, 90, false,
				RoombaConfig.DRIVE_MODE_MED));
		roomba.turn(90, false, RoombaConfig.TURN_MODE_SPOT,
				RoombaConfig.DRIVE_MODE_MED);
	}

	@Override
	public void log(String message) {
		fireStateChanged(true, new Event(EventType.LOG, message));
		System.out.println(message);
	}
}
