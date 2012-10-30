package brains;

import roomba.RoombaConfig;
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

	private static final byte DRIVE = 0;
	private static final byte RIGHT = 1;
	private static final byte LEFT = 2;

	// Change this if you want to debug the application
	private byte[] movements = { DRIVE, DRIVE, DRIVE, DRIVE, DRIVE, DRIVE,
			DRIVE, DRIVE, DRIVE, DRIVE, DRIVE, DRIVE, DRIVE,
			LEFT, DRIVE, DRIVE, DRIVE, LEFT, DRIVE, RIGHT, DRIVE, LEFT, DRIVE, DRIVE };

	private final static int SLEEP_TIME = 100;

	public Brains() {
		emulator = new Emulator();

		emulator.log("Initiating application");

		// Just drive around to test the emulator and Roomba
		try {
			Thread.sleep(SLEEP_TIME);

			for (byte movement : movements) {
				switch (movement) {
				case DRIVE:
					emulator.drive(100, RoombaConfig.DRIVE_MODE_MED);
					break;
				case RIGHT:
                                        emulator.turn(20, true, RoombaConfig.TURN_RADIUS_SPOT, RoombaConfig.DRIVE_MODE_MED);
					//emulator.turnRight();
					break;
				case LEFT:
                                        emulator.turn(45, false, RoombaConfig.TURN_RADIUS_SPOT, RoombaConfig.DRIVE_MODE_MED);
					//emulator.turnLeft();
					break;
				}
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

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new Brains();
	}
}
