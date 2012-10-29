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
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new Main();
	}
}
