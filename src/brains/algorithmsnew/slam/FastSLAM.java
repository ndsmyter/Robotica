package brains.algorithmsnew.slam;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import roomba.RoombaConfig;
import brains.MapStructure;
import brains.Particle;
import brains.algorithmsnew.RouletteWheelSelection;

import common.Config;
import common.RobotState;
import common.Sensor;
import common.Utils;

public class FastSLAM implements SLAMAlgorithmInterface {
	private RouletteWheelSelection roulette;
	
	public FastSLAM() {
		roulette = new RouletteWheelSelection();
	}
	
	@Override
	public void reset() {}
	
	/*
	 * For quick reference: pg 478
	 */
	@Override
	public List<Particle> execute(List<Particle> particles, int[] u, int[] z) {
		List<Particle> updatedParticles = new ArrayList<Particle>();

		for (Particle p : particles) {
			MapStructure map = p.getMap();
			
			RobotState newPosition = sampleMotionModel(u, map.getPosition());
			map.setPosition(newPosition);
			double weight = measurementModelMap(z, map);
			MapStructure newMap = updatedOccupancyGrid(z, map);
			
			updatedParticles.add(new Particle(newMap, weight));
		}

		List<Particle> resampledParticles = roulette.nextRandomParticles(
				updatedParticles, particles.size());

		return resampledParticles;
	}

	/**
	 * TODO Stef
	 * @param u de bewegingsvector u_t. u[0] → voorwaarts bewegen, u[1] → draaien
	 * @param x de state van de robot
	 * @return
	 */
	public RobotState sampleMotionModel(int[] u, RobotState x) {
		// Note van Robrecht: Ik heb hier tijdelijk al iets ingestoken 
		// zodanig dat onze simulatie al werkt. Het geen ik hier doe gaat 
		// geen rekening houden met de variantie op onze metingen, wat 
		// wel de bedoelig is. Dit stukje code veronderstelt dus dat
		// onze meting 100% juist is.
		RobotState tmp = new RobotState(x.x, x.y, x.dir);
		
		if (u[0] != 0)
			tmp = Utils.driveForward(tmp, u[0]);
		
		if (u[1] != 0)
			tmp.dir = (tmp.dir + u[1] + 360) % 360;
		
		return tmp;
	}

	/**
	 * TODO Robrecht
	 * @param z
	 * @param x
	 * @param m
	 * @return
	 */
	public double measurementModelMap(int[] z, MapStructure m) {
		RobotState robotState = m.getPosition();
		
		double sum = 0.0; 
		
        for (int i = 0; i < RoombaConfig.SENSORS.length; i++) {
            Sensor s = RoombaConfig.SENSORS[i];
            RobotState sensorState = Utils.getSensorState(robotState, s);
            Point measurement = Utils.pointToGrid(Utils.sensorDataToPoint(robotState, z[i], s));
            ArrayList<Point> path = Utils.getPath(sensorState, s.zMax);
            for (Point p : path) {
            	double x = m.get(p);
            	// need to find a better value for y
            	double y;
            	if (measurement.equals(p)) {
            		y = 1.0;
            	} else {
            		y = 0.0;
            	}
            	sum += x * y;
            }
        }
		return sum;
	}
	
	public MapStructure updatedOccupancyGrid(int[] z, MapStructure m) {
        MapStructure mapNew = m.clone();
        RobotState robotState = m.getPosition();
        for (int i = 0; i < RoombaConfig.SENSORS.length; i++) {
            Sensor s = RoombaConfig.SENSORS[i];
            RobotState sensorState = Utils.getSensorState(robotState, s);
            Point measurement = Utils.pointToGrid(Utils.sensorDataToPoint(
                    robotState, z[i], s));
            ArrayList<Point> path = Utils.getPath(sensorState, s.zMax);
            for (Point p : path) {
                double logOdds = m.getLogOdds(p)
                        + inverseSensorModel(m, p, measurement, sensorState,
                        z[i], s);
                mapNew.putLogOdds(p, logOdds);
            }
        }
        return mapNew;
    }

    public static double inverseSensorModel(MapStructure m, Point p,
            Point measurement, RobotState sensorState, int z, Sensor s) {
        double result = 0;
        int r = Utils.euclideanDistance(
                new Point(sensorState.x, sensorState.y), p);
        if (r > Math.min(s.zMax, z) + Config.GRID_CELL_SIZE) {
            result = 0;
        } else if (z < s.zMax && p.equals(measurement)) {
            result = 0.6; // p(occupied | z) = 0.8 => log 0.8/0.2 = log 4 = 0.6
        } else if (r < z) {
            result = -0.6; // p(occupied | z) = 0.2 => 0.2/0.8 = log 0.25 = -0.6
        }
        return result;
    }
}
