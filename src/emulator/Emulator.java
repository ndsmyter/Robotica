package emulator;

import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import roomba.Roomba;
import roomba.RoombaConfig;
import brains.Brains;

import common.RobotState;
import common.Sensor;
import common.Utils;

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
	private Brains brains;
	private ArrayList<Point> background = new ArrayList<Point>();

	public Emulator(Brains brains) {
		this.brains = brains;
		loadBackgroundMap(new File("spiral.bmp"));
		roomba = new Roomba(this);
		new EmulatorWindow(this);
	}

	public void loadBackgroundMap(File file) {
		try {
			ArrayList<Point> backgroundMap = new ArrayList<Point>();
			BufferedImage img = ImageIO.read(file);
			PixelGrabber grabber = new PixelGrabber(img, 0, 0, -1, -1, true);
			grabber.grabPixels();
			int[] pixels = (int[]) grabber.getPixels();
			int w = img.getWidth(), h = img.getHeight();
			int w2 = (int) (1.0 * w / 2 + 0.5), h2 = (int) (1.0 * h / 2 + 0.5);
			for (int i = 0; i < w; i++) {
				for (int j = 0; j < h; j++) {
					Color c = new Color(pixels[w * j + i]);
					if (c.getBlue() == 0 && c.getRed() == 0
							&& c.getGreen() == 0) {
						backgroundMap
								.add(new Point(5 * (i - w2), -5 * (j - h2)));
					}
				}
			}
			setBackground(backgroundMap);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void reset() {
		brains.reset();
	}

	public void restart() {
		brains.restart();
	}

	public void stop() {
		brains.stop(true);
	}

	public Brains getBrains() {
		return brains;
	}

	@Override
	public void drive(int millimeters, int driveMode) {
		log("E: DRIVE (" + millimeters + ")");
		fireStateChanged(true, new Event(EventType.DRIVE, millimeters,
				driveMode));
		roomba.drive(millimeters, driveMode);
	}

	@Override
	public void turn(int degrees, boolean turnRight, int turnMode, int driveMode) {
		log("E: " + (turnRight ? "RIGHT" : "LEFT") + " (" + degrees + ")");
		fireStateChanged(true, new Event(EventType.TURN, -1, degrees,
				turnRight, driveMode));
		roomba.turn(degrees, turnRight, turnMode, driveMode);
	}

	public int[] getSensorData() {
		// Stub
		int[] sensordata = new int[5];
		for (int i = 0; i < 5; i++) {
			sensordata[i] = emulateSensor(RoombaConfig.SENSORS[i]);
		}
		// System.out.println(sensordata[0]+" "+sensordata[1]+" "+sensordata[2]+" "+sensordata[3]+" "+sensordata[4]);
		return sensordata;
	}

	public int emulateSensor(Sensor sensor) {
		RobotState sensorState = Utils.getSensorState(brains.getCurrentState(),
				sensor);
		ArrayList<Point> points = Utils.getPath(sensorState, 800);
		boolean stop = false;
		int dist = 800;
		// Loop over all sensor points
		for (int i = 0; i < points.size() && !stop; i++) {
			Point sensorP = Utils.pointToGrid(points.get(i));
			if (background.contains(sensorP)) {
				int dist2 = Utils.euclideanDistance(sensorP, new Point(
						sensorState.x, sensorState.y));
				if (dist2 < dist) {
					dist = dist2;
				}
			}
		}
		return dist;
	}

	@Override
	public void log(String message) {
		fireStateChanged(true, new Event(EventType.LOG, message));
		System.out.println(message);
	}

	public void setBackground(ArrayList<Point> background) {
		ArrayList<Point> backgroundGrid = new ArrayList<Point>();
		for (Point p : background) {
			backgroundGrid.add(Utils.pointToGrid(p));
		}
		this.background = background;
	}

	public ArrayList<Point> getBackground() {
		return background;
	}
}
