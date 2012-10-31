package roomba;

import java.io.IOException;

import javax.comm.NoSuchPortException;
import javax.comm.PortInUseException;

import emulator.Emulator;
import roomba.interfaces.RoombaInterface;

public class Roomba implements RoombaInterface {

	private boolean DEBUG = false;

	private final Emulator emulator;
	private SerialIO serial = null;

	public static void main(String[] args){
		Roomba r = new Roomba();
		r.turnAtSpot(180, true);
		r.turnAtSpot(180, false);
		r.drive(500);
		r.turn(90, true, RoombaConfig.TURN_MODE_SHARP, RoombaConfig.DRIVE_MODE_MED);
		r.drive(500);
		r.turn(90, true, RoombaConfig.TURN_MODE_SHARP, RoombaConfig.DRIVE_MODE_MED);
		r.drive(1000, RoombaConfig.DRIVE_MODE_FAST);
		r.turn(90, true, RoombaConfig.TURN_MODE_SHARP, RoombaConfig.DRIVE_MODE_MED);
		r.drive(500);
		r.turn(90, true, RoombaConfig.TURN_MODE_SHARP, RoombaConfig.DRIVE_MODE_MED);
		r.drive(500);

	}

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
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		start();
		selectMode(RoombaConfig.ROOMBA_MODE_FULL);
	}

	private Roomba(){
		this.emulator = null;
		DEBUG = true;
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
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		start();
		selectMode(RoombaConfig.ROOMBA_MODE_FULL);
	}

	@Override
	public void drive(int millimeters, int drive_mode) {
		short velocity = 0, radius = Short.MAX_VALUE;
		long delay = 0;

		if(millimeters < 0) velocity = -1;
		else velocity = 1;

		switch(drive_mode){
		case RoombaConfig.DRIVE_MODE_SLOW: velocity *= 100; break;
		case RoombaConfig.DRIVE_MODE_MED:  velocity *= 300; break;
		case RoombaConfig.DRIVE_MODE_FAST: velocity *= 500; break;
		default:
			if(!DEBUG)this.emulator.log("Unknow drive mode");
			else System.err.println("Unknow drive mode");
		}

		delay = (millimeters / velocity) * 1000;
		
		if(DEBUG)System.out.println("[DRIVE] Dist: " + radius);
		if(DEBUG)System.out.println("[DRIVE] Velo: " + velocity);
		if(DEBUG)System.out.println("[DRIVE] Delay: " + delay);

		try {
			// Start roomba
			serial.sendCommand((byte) 137, SerialIO.toByteArray(velocity, radius));
			// wait for roomba to travel
			Thread.sleep(delay);
			// TODO use internal distance sensor to track distance traveled
			// stop roomba
			serial.sendCommand((byte) 137, new byte[] { 0, 0, 0, 0 });
		} catch (NullPointerException e) {
			// Serial doesn't exist
		} catch (InterruptedException e) {
			if(!DEBUG)this.emulator.log("InterruptedException: " + e.getLocalizedMessage());
			else e.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

	}

	@Override
	public void drive(int millimeters) {
		drive(millimeters, RoombaConfig.DRIVE_MODE_MED);

	}

	@Override
	public void turn(int degrees, boolean turnRight, int turn_mode, int drive_mode) {
		short velocity = 0, radius = 0;
		long delay = 0;


		if(degrees < 0) velocity = -1;
		else velocity = 1;

		switch(drive_mode){
		case RoombaConfig.DRIVE_MODE_SLOW: velocity *= 100; break;
		case RoombaConfig.DRIVE_MODE_MED: velocity *= 300; break;
		case RoombaConfig.DRIVE_MODE_FAST: velocity *= 500; break;
		default:
			if(!DEBUG)this.emulator.log("Unknow drive mode");
			else System.err.println("Unknow drive mode");
		}

		if(turnRight) radius = -1;
		else radius = 1;

		switch(turn_mode){
		case RoombaConfig.TURN_MODE_SPOT: break;
		case RoombaConfig.TURN_MODE_SHARP: radius *= 500; break;
		case RoombaConfig.TURN_MODE_WIDE: radius *= 1000; break;
		case RoombaConfig.TURN_MODE_VERYWIDE: radius *= 2000; break;
		default: 
			if(!DEBUG)this.emulator.log("Unknow turning mode");
			else System.err.println("Unknow turning mode");
		}

		if(DEBUG)System.out.println("[TURN] Radius: " + radius);
		if(DEBUG)System.out.println("[TURN] Degrees: " + degrees);

		double distance = 2 * Math.PI * radius * degrees / 360;

		delay = Math.abs((long) ((distance * 1000) / velocity));

		if(DEBUG)System.out.println("[TURN] Dist: " + distance);
		if(DEBUG)System.out.println("[TURN] Velo: " + velocity);
		if(DEBUG)System.out.println("[TURN] Delay: " + delay);
		
		try {
			// Start roomba
			serial.sendCommand((byte) 137, SerialIO.toByteArray(velocity, radius));
			// wait for roomba to travel
			Thread.sleep(delay);
			// TODO use internal distance sensor to track distance traveled
			// stop roomba
			serial.sendCommand((byte) 137, new byte[] { 0, 0, 0, 0 });
		} catch (NullPointerException e) {
			// Roomba doesn exist yet
		} catch (InterruptedException e) {
			if(!DEBUG)this.emulator.log("InterruptedException: " + e.getLocalizedMessage());
			else e.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	@Override
	public void turnAtSpot(int degrees, boolean turnRight) {
		turn(degrees, turnRight, RoombaConfig.TURN_MODE_SPOT, RoombaConfig.DRIVE_MODE_SLOW);
	}

	@Override
	public void start() {
		try {
			serial.sendCommand((byte) 128, new byte[]{});
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NullPointerException e){}
	}

	@Override
	public void selectMode(int roomba_mode) {
		int opcode = 0;
		switch(roomba_mode){
		case RoombaConfig.ROOMBA_MODE_SAFE: opcode = 131; break;
		case RoombaConfig.ROOMBA_MODE_FULL: opcode = 132; break;
		}

		if(opcode != 0)
			try {
				serial.sendCommand((byte) opcode, new byte[]{});
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NullPointerException e){}
	}

}
