package roomba;

import java.io.IOException;

import javax.comm.NoSuchPortException;
import javax.comm.PortInUseException;

import emulator.Emulator;
import roomba.interfaces.RoombaInterface;

public class Roomba implements RoombaInterface {

	private boolean DEBUG = RoombaConfig.ROOMBA_DEBUG;

	private final Emulator emulator;
	private SerialIO serial = null;

	public static void main(String[] args) {
		Roomba r = new Roomba(null);
		//r.getBumberSensors();
		//r.waitFor(1000);

		//r.setSongs();
		//r.waitFor(1000);
		//r.singSong(1);
		for(int i=0; i<7; i++){
			r.getSensorData(new byte[]{0});
			//r.turn(10, true, RoombaConfig.TURN_MODE_SPOT, RoombaConfig.DRIVE_MODE_SLOW);
			r.drive(100, RoombaConfig.DRIVE_MODE_SLOW);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}

		/*
		 * for(int i=0; i<5; i++){ r.turnAtSpot(180, RoombaConfig.TURN_RIGHT);
		 * r.turnAtSpot(180, RoombaConfig.TURN_LEFT); }
		 */
		/*
		 * for(int i=0; i<5; i++){ r.drive(500); r.waitFor(5000); r.drive(-500);
		 * r.waitFor(10000); }
		 */
	}

	public Roomba(Emulator emulator) {
		this.emulator = emulator;
		try {
			this.serial = new SerialIO(RoombaConfig.IO_PORT);
			if(DEBUG)System.out.println("Roomba startup");
		} catch (NoSuchPortException e) {
			System.out.println("Port not found");
		} catch (PortInUseException e) {
			System.out.println("Port in use");
		} catch (IOException e) {
		}

		waitFor(5000);
		start();
		waitFor(1000);
		selectMode(RoombaConfig.ROOMBA_MODE_SAFE);
		waitFor(1000);
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
			velocity *= RoombaConfig.DRIVE_DISTANCE_SLOW;
			break;
		case RoombaConfig.DRIVE_MODE_MED:
			velocity *= RoombaConfig.DRIVE_DISTANCE_MED;
			break;
		case RoombaConfig.DRIVE_MODE_FAST:
			velocity *= RoombaConfig.DRIVE_DISTANCE_FAST;
			break;
		default:
			if (!DEBUG)
				this.emulator.log("Unknow drive mode");
			else
				System.err.println("Unknow drive mode");
		}

		delay = (long) (millimeters * 1000.0 / velocity);

		if (DEBUG)
			System.out.println("[DRIVE] Dist: " + millimeters);
		if (DEBUG)
			System.out.println("[DRIVE] Radius: " + radius);
		if (DEBUG)
			System.out.println("[DRIVE] Velo: " + velocity);
		if (DEBUG)
			System.out.println("[DRIVE] Delay: " + delay);

		try {
			// Start roomba
			serial.sendCommand(RoombaConfig.ROOMBA_COMMAND_DRIVE,
					SerialIO.toByteArray(velocity, radius));
			// wait for roomba to travel
			waitFor(delay);
			// TODO use internal distance sensor to track distance traveled
			stop();
		} catch (NullPointerException e) {
			// Serial doesn't exist
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
			if (!DEBUG)
				this.emulator.log("Unknow drive mode");
			else
				System.err.println("Unknow drive mode");
		}

		radius = 1;

		switch (turn_mode) {
		case RoombaConfig.TURN_MODE_SPOT:
			// radius *= RoombaConfig.TURN_RADIUS_SPOT;
			radius = RoombaConfig.ROOMBA_WHEEL_DISTANCE / 2;
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
			if (!DEBUG)
				this.emulator.log("Unknow turning mode");
			else
				System.err.println("Unknow turning mode");
		}

		double distance = Math.PI * radius * (degrees / 180.0); // [mm]

		delay = Math.abs((long) ((distance * 1000) / velocity)); // [ms]

		if (turn_mode == RoombaConfig.TURN_MODE_SPOT)
			radius = 1;

		if (turnRight)
			radius *= -1;

		if (DEBUG)
			System.out.println("[TURN] Radius: " + radius);
		if (DEBUG)
			System.out.println("[TURN] Degrees: " + degrees);
		if (DEBUG)
			System.out.println("[TURN] Dist: " + distance);
		if (DEBUG)
			System.out.println("[TURN] Velo: " + velocity);
		if (DEBUG)
			System.out.println("[TURN] Delay: " + delay);

		try {
			// Start roomba
			serial.sendCommand(RoombaConfig.ROOMBA_COMMAND_DRIVE,
					SerialIO.toByteArray(velocity, radius));
			// wait for roomba to travel
			waitFor(delay);
			// TODO use internal distance sensor to track distance traveled
			stop();
		} catch (NullPointerException e) {
			// Roomba doesn exist yet
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	@Override
	public void turnAtSpot(int degrees, boolean turnRight) {
		turn(degrees, turnRight, RoombaConfig.TURN_MODE_SPOT,
				RoombaConfig.DRIVE_MODE_SLOW);
	}

	@Override
	public void start() {
		try {
			serial.sendCommand(RoombaConfig.ROOMBA_COMMAND_START, new byte[] {});
		} catch (IOException e) {
		} catch (NullPointerException e) {
			// Serial doesn't exist
		}
		waitFor(1000);
	}

	@Override
	public void stop() {
		try {
			serial.sendCommand(RoombaConfig.ROOMBA_COMMAND_DRIVE, new byte[] {0, 0, 0, 0 });
		} catch (IOException e) {
		} catch (NullPointerException e) {
			// Serial doesn't exist
		}
		waitFor(1000);
	}

	@Override
	public void selectMode(int roombaMode) {
		int opcode = 0;
		switch (roombaMode) {
		case RoombaConfig.ROOMBA_MODE_SAFE:
			opcode = RoombaConfig.ROOMBA_COMMAND_SAFE;
			break;
		case RoombaConfig.ROOMBA_MODE_FULL:
			opcode = RoombaConfig.ROOMBA_COMMAND_FULL;
			break;
		}

		if (opcode != 0) {
			try {
				serial.sendCommand((byte) opcode, new byte[] {});
			} catch (IOException e) {
			} catch (NullPointerException e) {
				// Serial doesn't exist
			}
		}
		waitFor(1000);
	}

	private void waitFor(long milliseconds) {
		try {
			Thread.sleep(milliseconds);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void setSongs() {
		try {
			serial.sendCommand((byte) 140, new byte[] { (byte) 0, 5, 69, 8, 69,
					16, 30, 8, 69, 8, 69, 8 });
			serial.sendCommand((byte) 140, new byte[] { (byte) 1, 11, 69, 8,
					69, 8, 69, 8, 30, 8, 69, 16, 69, 16, 69, 16, 30, 8, 69, 8,
					69, 8, 69, 8 });
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			// Serial doesn't exist
		}
	}

	public void singSong(int select) {
		try {
			serial.sendCommand((byte) 141, new byte[] { (byte) select });
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			// Serial doesn't exist
		}
	}

	private void getBumberSensors() {
		try {
			serial.sendCommand((byte) 149, new byte[] { 1, 7 });
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			// Serial doesn't exist
		}
	}

	@Override
	public int[] getSensorData(byte[] ids){
		byte[] get = new byte[ids.length + 1];
		get[0] = (byte) (ids.length);
		for(int i=0; i<ids.length; i++){
			get[i + 1] = ids[i];
		}
		try {
			serial.sendCommand((byte)120, get);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		byte[] inputBytes = serial.getResponds();
		short[] input = new short[inputBytes.length / 2];
		for(int i=0; i<input.length; i++){
			input[i] = (short) ((inputBytes[2*i] << 8) + (inputBytes[2*i + 1] & 0xFF));
			if(DEBUG)System.out.println(inputBytes[2*i] + "|" + inputBytes[2*i+1] + " -> " + input[i]);
		}
		int[] output = new int[input.length];
		for(int i=0; i<input.length; i++){
			System.out.println(input[i] + " -> " + convertSensorOutputToDistance(input[i]));
			output[i] = convertSensorOutputToDistance(input[i]);
		}
		waitFor(1000);
		return output;
	}
	
	public short[] getRawSensorData(byte[] ids){
		byte[] get = new byte[ids.length + 1];
		get[0] = (byte) (ids.length);
		for(int i=0; i<ids.length; i++){
			get[i + 1] = ids[i];
		}
		try {
			serial.sendCommand((byte)120, get);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		byte[] inputBytes = serial.getResponds();
		short[] input = new short[inputBytes.length / 2];
		for(int i=0; i<input.length; i++){
			input[i] = (short) ((inputBytes[2*i] << 8) + (inputBytes[2*i + 1] & 0xFF));
			if(DEBUG)System.out.println(inputBytes[2*i] + "|" + inputBytes[2*i+1] + " -> " + input[i]);
		}
		
		return input;
	}
	
	int[] grenzen = new int[]{433,249,175,137,113,97,88,80,70};
	private int convertSensorOutputToDistance(short in){
		double b = in & 0xFF;
		if(b <= grenzen[0] && b > grenzen[1])
			return linearize(b, 100, 200, grenzen[0], grenzen[1]);
		if(b <= grenzen[1] && b > grenzen[2])
			return linearize(b, 200, 300, grenzen[1], grenzen[2]);
		if(b <= grenzen[2] && b > grenzen[3])
			return linearize(b, 300, 400, grenzen[2], grenzen[3]);
		if(b <= grenzen[3] && b > grenzen[4])
			return linearize(b, 400, 500, grenzen[3], grenzen[4]);
		if(b <= grenzen[4] && b > grenzen[5])
			return linearize(b, 500, 600, grenzen[4], grenzen[5]);
		if(b <= grenzen[5] && b > grenzen[6])
			return linearize(b, 600, 700, grenzen[5], grenzen[6]);
		if(b <= grenzen[6] && b > grenzen[7])
			return linearize(b, 700, 800, grenzen[6], grenzen[7]);
		if(b <= grenzen[7] && b > grenzen[8])
			return linearize(b, 700, 800, grenzen[7], grenzen[8]);
		return -1;

	}

	private int linearize(double b, double Dub, double Dlb, double Ub, double Lb) {
		return (int)Math.round(Dlb - (((double)b - Lb)*(Dlb - Dub)/(Ub - Lb)));
	}
}
