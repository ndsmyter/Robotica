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
import java.util.List;
import java.util.Scanner;

import javax.imageio.ImageIO;

import roomba.Roomba;
import roomba.RoombaConfig;
import brains.Brains;
import brains.MapStructure;

import common.Config;
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

	private RobotState simulatedRobotState;
	// private Random simRandom = new Random();

	private String currentMap = ""; // No map by default
	private ArrayList<Point> background = new ArrayList<Point>();
	private ArrayList<String> backgroundFiles = new ArrayList<String>();
	private static final String MAPS_DIRECTORY = "maps";
	private static final String DEFAULT_MAP_FILE = "default.txt";

	private List<ParticleViewer> particleViewers = new ArrayList<ParticleViewer>();
	private boolean mapShowing = true;
	private boolean roombaShowing = true;

	// The colors which you can change to the color you like
	public final static Color ZERO_COLOR = Color.BLACK;
	public final static Color ROBOT_COLOR = new Color(210, 250, 255);
	public final static Color SENSOR_COLOR = new Color(5, 80, 90);
	public final static Color TEXT_COLOR = Color.BLACK;
	public final static Color GRID_COLOR = Color.DARK_GRAY;
	public final static Color PATH_COLOR = Color.ORANGE;
	public final static Color MAP_COLOR = Color.YELLOW;
	public final static Color BACKGROUND_COLOR = Color.GRAY;
	public final static Color CURRENT_STATE_COLOR = Color.RED;

	// Scaling parameters
	public final static int LINE_LENGTH = 100;
	public final static int ROBOT_SIZE = RoombaConfig.ROOMBA_DIAMETER;
	public final static double ZOOM_FACTOR = 0.05;
	public final static double ZOOM_FACTOR2 = 0.005;
	public final static double ORIGINAL_ZOOM = 0.2;
	public final static int ARROW_MOVEMENT = 5;

	public final static int CELLS_IN_GRID = 10;

	private int iteration = 0;

	// Logs
	private ArrayList<String> logs = new ArrayList<String>();

	private static final String LOG_FILENAME = "log.txt";

	public Emulator(Brains brains) {
		simulatedRobotState = new RobotState(0, 0, 0);
		this.brains = brains;
		roomba = new Roomba(this);
		loadBackgroundFiles();
		new EmulatorWindow(this);
	}

	public RobotState getSimulatedRobotState() {
		return simulatedRobotState;
	}

	public void addParticleViewer(ParticleViewer particleViewer) {
		particleViewers.add(particleViewer);
	}

	public void removeParticleViewer(ParticleViewer particleViewer) {
		particleViewers.remove(particleViewer);
	}

	public void makeParticleViewer() {
		new ParticleViewer(this);
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
			updateViewOfParticleViewers();
		}
	}

	public void updateParticlesOfViewers() {
		for (ParticleViewer particleViewer : particleViewers)
			particleViewer.setParticles(brains.getParticles());
	}

	private void updateViewOfParticleViewers() {
		for (ParticleViewer particleViewer : particleViewers) {
			particleViewer.viewUpdated();
		}
	}

	public boolean isMapShowing() {
		return this.mapShowing;
	}

	public void setRoombaShowing(boolean showing) {
		if (this.roombaShowing != showing) {
			this.roombaShowing = showing;
			updateViewOfParticleViewers();
		}
	}

	public boolean isRoombaShowing() {
		return this.roombaShowing;
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
						Point p = new Point((int) (5 * (i - w2)),
								(int) (-5 * (j - h2)));
						Utils.pointToGrid(p);
						if (!backgroundMap.contains(p))
							backgroundMap.add(p);
					}
				}
			}
			setBackground(backgroundMap);
		} catch (IOException e) {
		} catch (InterruptedException e) {
		}
	}

	public void reset() {
		iteration = 0;
		simulatedRobotState = new RobotState(0, 0, 0);
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
		iteration++;
		log("E(" + iteration + "): DRIVE (" + millimeters + ")");

		// // Simulate noise start
		// int x = (int) (Math.abs(Config.SIMULATED_MOVEMENT_NOISE_PCT *
		// millimeters) + 0.5);
		// if(x > 0)
		// millimeters = millimeters + simRandom.nextInt(x * 2) - x;

		if (Config.SIMULATED_ROTATION_NOISE_PCT > 0) {
			double b = Config.SIMULATED_ROTATION_NOISE_PCT * millimeters;
			millimeters = (int) Utils.gaussSample(b, millimeters);
		}

		// driving with steps SIMULATED_STEP_SIZEs
		while (millimeters > 0) {
			int toDrive = millimeters < Config.SIMULATED_STEP_SIZE ? millimeters
					: Config.SIMULATED_STEP_SIZE;
			
			boolean isFree = isPathFree(simulatedRobotState, toDrive, background);
			
			if (isFree) {
				millimeters -= toDrive;
				simulatedRobotState = Utils.driveForward(simulatedRobotState,
						toDrive);
			} else {
				// PANIEK! Kate rijdt tegen een muur :/
				log("PANIEK! Kate rijdt tegen een muur :/");
				millimeters = 0;
			}
		}

		fireStateChanged(true, new Event(EventType.DRIVE, millimeters,
				driveMode));
		roomba.drive(millimeters, driveMode);
	}

	@Override
	public void turn(int degrees, int turnMode, int driveMode) {
		iteration++;
		boolean turnRight = degrees < 0;
		log("E(" + iteration + "): " + (turnRight ? "RIGHT" : "LEFT") + " ("
				+ degrees + ")");

		// Simulate noise
		// int x = (int) (Math.abs(Config.SIMULATED_ROTATION_NOISE_PCT *
		// degrees) + 0.5);
		// if(x > 0)
		// degrees = degrees + simRandom.nextInt(x * 2) - x;

		if (Config.SIMULATED_ROTATION_NOISE_PCT > 0) {
			double b = Config.SIMULATED_ROTATION_NOISE_PCT * degrees;
			degrees = (int) Utils.gaussSample(b, degrees);
		}

		simulatedRobotState.dir = (simulatedRobotState.dir + degrees + 360) % 360;
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
		return sensordata;
	}

	public int emulateSensor(Sensor sensor) {
		RobotState sensorState = Utils.getSensorState(simulatedRobotState,
				sensor);
		ArrayList<Point> points = Utils.getPath(sensorState, sensor.zMax);
		boolean stop = false;
		int dist = sensor.zMax;
		// Loop over all sensor points
		for (int i = 0; i < points.size() && !stop; i++) {
			Point sensorP = Utils.pointToGrid(points.get(i));
			if (background.contains(sensorP)) {
				int dist2 = Utils.euclideanDistance(sensorP, new Point(
						sensorState.x, sensorState.y));

				// Ruis simuleren
				// int x = (int) (Math.abs(Config.SIMULATED_SENSOR_NOISE_PCT *
				// dist2) + 0.5);
				// if(x > 0)
				// dist2 = dist2 + simRandom.nextInt(x * 2) - x;

				if (Config.SIMULATED_SENSOR_NOISE_PCT > 0) {
					double b = Config.SIMULATED_SENSOR_NOISE_PCT * dist2;
					dist2 = (int) Utils.gaussSample(b, dist2);
				}

				if (dist2 < dist) {
					dist = dist2;
					stop = true;
				}
			}
		}
		return dist;
	}
	
	private boolean isPathFree(RobotState robotState, int step, ArrayList<Point> background) {
		ArrayList<Point> path = Utils.getPath(
				robotState,
				step + RoombaConfig.ROOMBA_DIAMETER / 2,
				RoombaConfig.ROOMBA_DIAMETER
			);
		boolean freeTmp = true;
		int points = path.size();
		for (int i = 0; i < points && freeTmp; i++)
			freeTmp &= !background.contains(path.get(i));
		return freeTmp;
	}

	@Override
	public void log(String message) {
		SimpleDateFormat sdf = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
		logs.add(sdf.format(Calendar.getInstance().getTime()) + "  " + message
				+ "\n");
		if (iteration % 20 == 0) {
			saveLogToFile();
			logs.clear();
		}
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
