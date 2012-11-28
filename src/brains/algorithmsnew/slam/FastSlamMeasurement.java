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

public class FastSlamMeasurement implements SLAMAlgorithmInterface {
	private RouletteWheelSelection roulette;
	
	public FastSlamMeasurement() {
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
	 * @param u
	 * @param x
	 * @return
	 */
	public RobotState sampleMotionModel(int[] u, RobotState x) {
		// poging tot implementatie
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
		return 1.0;
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