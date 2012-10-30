package brains;

import emulator.Emulator;

/**
 * This class will start everything up
 * 
 * @author Nicolas
 * 
 */
public class Main {

	private final Emulator emulator;

	private static final byte DRIVE = 0;
	private static final byte RIGHT = 1;
	private static final byte LEFT = 2;

	// Change this if you want to debug the application
	private byte[] movements = { DRIVE, DRIVE, DRIVE, DRIVE, DRIVE, LEFT,
			DRIVE, DRIVE, DRIVE, LEFT, RIGHT, LEFT, DRIVE, DRIVE };

	public Main() {
		emulator = new Emulator();

		emulator.log("Initiating application");

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

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new Main();
	}
}
