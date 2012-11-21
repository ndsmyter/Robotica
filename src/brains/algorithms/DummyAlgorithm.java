package brains.algorithms;

import java.awt.Point;
import java.util.ArrayList;

import brains.Brains;
import brains.MapStructure;
import common.Config;
import common.RobotState;
import common.Sensor;

import common.Utils;
import roomba.RoombaConfig;

public class DummyAlgorithm implements AlgorithmInterface {
	// Step length in mm

	private static final int STEP = 50;
	private static final int MAX_VALUE = 100;
	private int i = 0;

	public DummyAlgorithm() {
		reset();
	}

	public void run(Brains b) {
		while (i < MAX_VALUE && !b.isStopped()) {
			doStep(b);
		}
	}

	public void doStep(Brains b) {
		// System.out.println(b.getCurrentState());
		processSensorData2(b);
		boolean free = true;
		ArrayList<Point> path = Utils.getPath(b.getCurrentState(), STEP
				+ RoombaConfig.ROOMBA_DIAMETER / 2);
		for (Point p : path) {
			free &= (b.getMap().get(Utils.pointToGrid(p)) < 0.60);
		}
		if (free) {
			b.drive(STEP);
		} else {
			b.turn(90, true);
		}
		i++;
	}

	public void reset() {
		i = 0;
	}

	public static void processSensorData(Brains b) {
		int[] data = b.getSensorData();
		for (int i = 0; i < 5; i++) {
			RobotState sensorState = Utils.getSensorState(b.getCurrentState(),
					RoombaConfig.SENSORS[i]);
			Point measurement = Utils.sensorDataToPoint(b.getCurrentState(),
					data[i], RoombaConfig.SENSORS[i]);
			ArrayList<Point> path = Utils.getPath(sensorState, new RobotState(
					measurement.x, measurement.y, sensorState.dir));
			for (Point p : path) {
				// double newValue = mapStructure.get(Utils.pointToGrid(p)) -
				// 0.10;
				// if (newValue > 1)
				// newValue = 1;
				// if (newValue < 0)
				// newValue = 0;
				// mapStructure.put(Utils.pointToGrid(p), newValue);
				b.getMap().put(Utils.pointToGrid(p), 0);
			}
			if (data[i] < 800) {
				b.getMap().put(Utils.pointToGrid(measurement), 1);
			}
		}
	}

	public void processSensorData2(Brains b) {
		int[] z = b.getSensorData();
		RobotState x = b.getCurrentState();
		MapStructure m = b.getMap();
		MapStructure m2 = updatedOccupancyGrid(z, x, m);
		b.setMap(m2);
	}

	public MapStructure updatedOccupancyGrid(int[] z, RobotState x,
			MapStructure m) {
		MapStructure mapNew = m.clone();
		for (int i = 0; i < RoombaConfig.SENSORS.length; i++) {
			Sensor s = RoombaConfig.SENSORS[i];
			RobotState sensorState = Utils.getSensorState(x, s);
			ArrayList<Point> path = Utils.getPath(sensorState, s.zMax);
			for (Point p : path) {
				double logOdds = m.getLogOdds(p)
						+ inverseSensorModel(m, p, x, sensorState, z[i], s);
				mapNew.putLogOdds(Utils.pointToGrid(p), logOdds);
			}
		}
		return mapNew;
	}

	public double inverseSensorModel(MapStructure m, Point p, RobotState x,
			RobotState sensorState, int z, Sensor s) {
		double result = 0;
		int r = Utils.euclideanDistance(new Point(sensorState.x, sensorState.y), p);
		p = Utils.pointToGrid(p);
		Point measurement = Utils.pointToGrid(Utils.sensorDataToPoint(x, z, s));
		if(r > Math.min(s.zMax,z)+Config.GRID_SIZE){
            result = 0;
        } else if (z < s.zMax && p.equals(measurement)){
            result = 0.6; // p(occupied | z) = 0.8 => log 0.8/0.2 = log 4 = 0.6
        } else if (r < z){
            result = -0.6; // p(occupied | z) = 0.2 => 0.2/0.8 = log 0.25 = -0.6
        }
		return result;
	}
}
