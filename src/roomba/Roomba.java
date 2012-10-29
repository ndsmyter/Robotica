package roomba;

import emulator.Emulator;
import roomba.interfaces.RoombaInterface;

public class Roomba implements RoombaInterface {

	private final Emulator emulator;

	public Roomba(Emulator emulator) {
		this.emulator = emulator;
	}

	@Override
	public void drive(int milliseconds) {
		emulator.log("R: DRIVE (" + milliseconds + ")");
	}

	@Override
	public void turn(int milliseconds, boolean right) {
		emulator.log("R: " + (right ? "RIGHT" : "LEFT") + " (" + milliseconds
				+ ")");
	}

}
