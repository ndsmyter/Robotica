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

	public Main() {
		emulator = new Emulator();

		emulator.log("Initiating application");

		try {
			Thread.sleep(1000);
			emulator.drive(1000);
			Thread.sleep(1000);
			emulator.drive(1000);
			Thread.sleep(1000);
			emulator.drive(1000);
			Thread.sleep(1000);
			emulator.drive(1000);
			Thread.sleep(1000);
			emulator.turnRight();
			Thread.sleep(1000);
			emulator.turnRight();
			Thread.sleep(1000);
			emulator.turnRight();
			Thread.sleep(1000);
			emulator.turnLeft();
			Thread.sleep(1000);
			emulator.drive(1000);
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
