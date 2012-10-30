package roomba;

import java.io.IOException;

import javax.comm.NoSuchPortException;
import javax.comm.PortInUseException;

import emulator.Emulator;
import roomba.interfaces.RoombaInterface;

public class Roomba implements RoombaInterface {

	private final Emulator emulator;
	private SerialIO serial = null;

	public Roomba(Emulator emulator) {
		this.emulator = emulator;
		try {
			this.serial = new SerialIO("COM9");
		} catch (NoSuchPortException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (PortInUseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void drive(int millimeters, int drive_mode) {
		short velocity = 0, radius = Short.MAX_VALUE;
		long delay = 0;

		if (millimeters < 0)
			velocity = -1;
		else
			velocity = 1;

		switch (drive_mode) {
		case RoombaConfig.DRIVE_MODE_SLOW:
			velocity *= 100;
			break;
		case RoombaConfig.DRIVE_MODE_MED:
			velocity *= 300;
			break;
		case RoombaConfig.DRIVE_MODE_FAST:
			velocity *= 500;
			break;
		default:
			this.emulator.log("Unknow drive mode");
		}

		delay = (millimeters / velocity) * 1000;

		try {
			// Start roomba
			serial.sendCommand((byte) 137,
					SerialIO.toByteArray(velocity, radius));
			// wait for roomba to travel
			this.wait(delay);
			// TODO use internal distance sensor to track distance traveled
			// stop roomba
			serial.sendCommand((byte) 137, new byte[] { 0, 0, 0, 0 });
		} catch (NullPointerException e) {
			// Serial doesn't exist
		} catch (InterruptedException e) {
			this.emulator.log("InterruptedException: "
					+ e.getLocalizedMessage());
		} catch (IOException e1) {
			e1.printStackTrace();
		}

	}

	@Override
	public void drive(int millimeters) {
		drive(millimeters, RoombaConfig.DRIVE_MODE_MED);

	}

	@Override
	public void turn(int degrees, boolean turnRight, int turn_mode,
			int drive_mode) {
		short velocity = 0, radius = 0;
		long delay = 0;

		if (degrees < 0)
			velocity = -1;
		else
			velocity = 1;

		switch (drive_mode) {
		case RoombaConfig.DRIVE_MODE_SLOW:
			velocity *= RoombaConfig.DRIVE_DISTANCE_SLOW;
			break;
		case RoombaConfig.DRIVE_MODE_MED:
			velocity *= RoombaConfig.DRIVE_DISTANCE_MED;
			break;
		case RoombaConfig.DRIVE_MODE_FAST:
			velocity *= RoombaConfig.DRIVE_DISTANCE_FAST;
			break;
		default:
			this.emulator.log("Unknow drive mode");
		}

		if (turnRight)
			radius = -1;
		else
			radius = 1;

		switch (turn_mode) {
		case RoombaConfig.TURN_MODE_SPOT:
			break;
		case RoombaConfig.TURN_MODE_SHARP:
			radius *= RoombaConfig.TURN_RADIUS_SHARP;
			break;
		case RoombaConfig.TURN_MODE_WIDE:
			radius *= RoombaConfig.TURN_RADIUS_WIDE;
			break;
		case RoombaConfig.TURN_MODE_VERYWIDE:
			radius *= RoombaConfig.TURN_RADIUS_VERYWIDE;
			break;
		default:
			this.emulator.log("Unknow turning mode");
		}

		double distance = 2 * Math.PI * radius * degrees / 360;

		delay = (long) ((distance * 1000) / velocity);

		try {
			// Start roomba
			serial.sendCommand((byte) 137,
					SerialIO.toByteArray(velocity, radius));
			// wait for roomba to travel
			this.wait(delay);
			// TODO use internal distance sensor to track distance traveled
			// stop roomba
			serial.sendCommand((byte) 137, new byte[] { 0, 0, 0, 0 });
		} catch (NullPointerException e) {
			// Roomba doesn exist yet
		} catch (InterruptedException e) {
			this.emulator.log("InterruptedException: "
					+ e.getLocalizedMessage());
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	@Override
	public void turnAtSpot(int degrees, boolean turnRight) {
		turn(degrees, turnRight, RoombaConfig.TURN_MODE_SPOT,
				RoombaConfig.DRIVE_MODE_MED);

	}

}
