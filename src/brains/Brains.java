package brains;

import emulator.Emulator;
import emulator.Event;
import emulator.interfaces.ListenerInterface;

/**
 * This class will start everything up
 * 
 * @author Nicolas
 * 
 */
public class Brains implements ListenerInterface {

	private final Emulator emulator;

	private static final byte DRIVE = 0;
	private static final byte RIGHT = 1;
	private static final byte LEFT = 2;

	// Change this if you want to debug the application
	private byte[] movements = { DRIVE, DRIVE, DRIVE, DRIVE, DRIVE, LEFT,
			DRIVE, DRIVE, DRIVE, LEFT, RIGHT, LEFT, DRIVE, DRIVE };

	public Brains() {
		emulator = new Emulator();

		emulator.log("Initiating application");

		// Just drive around to test the emulator and Roomba
		try {
			Thread.sleep(1000);

			for (byte movement : movements) {
				switch (movement) {
				case DRIVE:
					emulator.drive(1000);
					break;
				case RIGHT:
					emulator.turnRight();
					break;
				case LEFT:
					emulator.turnLeft();
					break;
				}
				Thread.sleep(1000);
			}

		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void stateChanged(Event event) {
		// An event happened to the robot which has to be parsed
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new Brains();
	}
}
