package roomba;

import common.Sensor;

public final class RoombaConfig {
	public static final int DRIVE_DISTANCE_SLOW = 100;
	public static final int DRIVE_DISTANCE_MED = 300;
	public static final int DRIVE_DISTANCE_FAST = 500;

	public static final int DRIVE_MODE_SLOW = 0;
	public static final int DRIVE_MODE_MED = 1;
	public static final int DRIVE_MODE_FAST = 2;

	public static final int TURN_RADIUS_SPOT = 0;
	public static final int TURN_RADIUS_SHARP = 500;
	public static final int TURN_RADIUS_WIDE = 1000;
	public static final int TURN_RADIUS_VERYWIDE = 2000;

	public static final int TURN_MODE_SPOT = 0;
	public static final int TURN_MODE_SHARP = 1;
	public static final int TURN_MODE_WIDE = 2;
	public static final int TURN_MODE_VERYWIDE = 3;

	public static final boolean TURN_RIGHT = true;
	public static final boolean TURN_LEFT = false;

	public static final int ROOMBA_MODE_SAFE = 0;
	public static final int ROOMBA_MODE_FULL = 1;

	public static final String IO_PORT = "COM9";
	public static final byte ROOMBA_COMMAND_DRIVE = (byte) 137;
	public static final byte ROOMBA_COMMAND_START = (byte) 128;
	public static final byte ROOMBA_COMMAND_SAFE = (byte) 131;
	public static final byte ROOMBA_COMMAND_FULL = (byte) 132;

	// Roomba 5200: height=8cm, diameter=33cm, afstand tussen de wielen = 24cm
	public static final int ROOMBA_HEIGHT = 80;
	public static final int ROOMBA_DIAMETER = 330;
	public static final int ROOMBA_WHEEL_DISTANCE = 240;

	// Sensor placement
	public static final Sensor[] SENSORS = { new Sensor(100, -100, -45, 1000),
			new Sensor(120, -50, 0, 1000), new Sensor(120, 0, 0, 1000),
			new Sensor(120, 50, 0, 1000), new Sensor(100, 100, 45, 1000) };

}
