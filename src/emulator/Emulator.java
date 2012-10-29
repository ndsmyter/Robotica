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
		roomba = new Roomba();
	}

	@Override
	public void drive(int milliseconds) {
		// TODO Add logic to draw this action on screen
		roomba.drive(milliseconds);
	}

	@Override
	public void turn(int milliseconds, boolean right) {
		// TODO Add logic to draw this action on screen
		roomba.turn(milliseconds, right);
	}

	@Override
	public void log(String message) {
		// TODO Add logic to draw this action on screen
		System.out.println(message);
	}

}
