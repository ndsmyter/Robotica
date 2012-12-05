package emulator;

import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Scanner;

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

	private String currentMap = ""; // No map by default
	private ArrayList<Point> background = new ArrayList<Point>();
	private ArrayList<String> backgroundFiles = new ArrayList<String>();
	private static final String MAPS_DIRECTORY = "maps";
	private static final String DEFAULT_MAP_FILE = "default.txt";

	private ParticleViewer particleViewer = null;
	private boolean mapShowing = true;

	// The colors which you can change to the color you like
	public final static Color ZERO_COLOR = Color.BLACK;
	public final static Color ROBOT_COLOR = new Color(210, 250, 255);
	public final static Color SENSOR_COLOR = new Color(5, 80, 90);
	public final static Color TEXT_COLOR = Color.BLACK;
	public final static Color GRID_COLOR = Color.DARK_GRAY;
	public final static Color PATH_COLOR = Color.ORANGE;
	public final static Color MAP_COLOR = Color.YELLOW;
	public final static Color BACKGROUND_COLOR = Color.GRAY;

	// Scaling parameters
	public final static int LINE_LENGTH = 100;
	public final static int ROBOT_SIZE = RoombaConfig.ROOMBA_DIAMETER;
	public final static double ZOOM_FACTOR = 0.05;
	public final static double ORIGINAL_ZOOM = 0.2;
	public final static int ARROW_MOVEMENT = 5;

	public final static int CELLS_IN_GRID = 10;

	// Logs
	private ArrayList<String> logs = new ArrayList<String>();

	private static final String LOG_FILENAME = "log.txt";

	public Emulator(Brains brains) {
		this.brains = brains;
		roomba = new Roomba(this);
		loadBackgroundFiles();
		particleViewer = new ParticleViewer(this);
		new EmulatorWindow(this);
	}

	public void updateParticleViewer() {
		if (particleViewer != null)
			particleViewer.setParticles(brains.getParticles());
	}

	private void loadBackgroundFiles() {
		File dir = new File(MAPS_DIRECTORY);
		if (dir.isDirectory()) {
			for (File file : dir.listFiles()) {
				if (file.getName().equals(DEFAULT_MAP_FILE)) {
					try {
						Scanner sc = new Scanner(file);
						if (sc.hasNextLine())
							setMap(sc.nextLine());
						sc.close();
					} catch (FileNotFoundException e) {
					}
				} else if (file.getName().endsWith(".bmp") && file.isFile())
					backgroundFiles.add(file.getName());
			}
		}
	}

	public ArrayList<String> getBackgroundMaps() {
		return backgroundFiles;
	}

	public String getMap() {
		return currentMap;
	}

	public void setMapShowing(boolean showing) {
		if (this.mapShowing != showing) {
			this.mapShowing = showing;
			particleViewer.viewUpdated();
		}
	}

	public boolean isMapShowing() {
		return this.mapShowing;
	}

	public void setMap(String map) {
		if (!map.equals(currentMap)) {
			currentMap = map;
			if (map.isEmpty()) {
				background.clear();
			} else {
				map = MAPS_DIRECTORY + "/" + map;
				System.out.println("Loaded map: " + map);
				loadBackgroundMap(new File(map));
			}
			try {
				PrintWriter out = new PrintWriter(
						new BufferedWriter(new FileWriter(MAPS_DIRECTORY + "/"
								+ DEFAULT_MAP_FILE)));
				out.write(currentMap);
				out.close();
			} catch (IOException e) {
			}
		}
	}

	public void loadBackgroundMap(File file) {
		if (!file.isFile())
			return;
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
		} catch (InterruptedException e) {
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

	public void doStep() {
		brains.doStep();
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
		RobotState sensorState = Utils.getSensorState(brains.getMap()
				.getPosition(), sensor);
		ArrayList<Point> points = Utils.getPath(sensorState, sensor.zMax);
		boolean stop = false;
		int dist = sensor.zMax;
		// Loop over all sensor points
		for (int i = 0; i < points.size() && !stop; i++) {
			Point sensorP = Utils.pointToGrid(points.get(i));
			if (background.contains(sensorP)) {
				int dist2 = Utils.euclideanDistance(sensorP, new Point(
						sensorState.x, sensorState.y));
				if (dist2 < dist) {
					dist = dist2;
					stop = true;
				}
			}
		}
		return dist;
	}

	@Override
	public void log(String message) {
		SimpleDateFormat sdf = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
		logs.add(sdf.format(Calendar.getInstance().getTime()) + "  " + message
				+ "\n");
		fireStateChanged(true, new Event(EventType.LOG, message));
	}

	public void saveLogToFile() {
		try {
			// Create file
			FileWriter fstream = new FileWriter(LOG_FILENAME, true);
			BufferedWriter out = new BufferedWriter(fstream);
			for (String log : logs) {
				out.append(log);
			}
			// Close the output stream
			out.close();
		} catch (Exception e) {
		}
	}

	public void setBackground(ArrayList<Point> background) {
		this.background = new ArrayList<Point>(background);
	}

	public ArrayList<Point> getBackground() {
		return background;
	}

	public void setSongs() {
		roomba.setSongs();
	}

	public void singSong(int song) {
		roomba.singSong(song);
	}
}
