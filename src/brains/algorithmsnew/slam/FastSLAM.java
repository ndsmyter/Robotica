package brains.algorithmsnew.slam;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
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
    private int iterations;

    public FastSLAM() {
        roulette = new RouletteWheelSelection();
        iterations = 0;
    }

    @Override
    public void reset() {
        iterations = 0;
    }

    /*
     * For quick reference: pg 478
     */
    @Override
    public List<Particle> execute(List<Particle> particles, int[] u, int[] z) {
        for (Particle p : particles) {
            MapStructure map = p.getMap();

            map.logMovement();
            sampleMotionModel(u, map.getPosition());

            double weight = measurementModelMap(z, map);
            updatedOccupancyGrid(z, map);

            p.setWeight(p.getWeight() + weight);
        }

        List<Particle> resampledParticles;
        if (iterations % Config.ITERATIONS_PER_RESAMPLE == 0) {
            resampledParticles = roulette.nextRandomParticles(particles, particles.size());
            for (Particle p : resampledParticles) {
                p.setWeight(p.getWeight() * 0.8);
            }
        } else {
            resampledParticles = particles;
        }

        for (int i = 0; i < 16; i++) {
            System.gc();
        }

        iterations++;

        return resampledParticles;
    }

    /**
     * @param u de bewegingsvector u_t. u[0] → voorwaarts bewegen (afstand in
     * mm), u[1] → draaien (hoek in graden)
     * @param x de state van de robot
     * @return
     */
    public void sampleMotionModel(int[] u, RobotState x) {
        int noisyu0 = u[0] + (int) (u[0] * Utils.gaussSample(Config.ALPHA1));
        int noisyu1 = u[1] + (int) (u[1] * Utils.gaussSample(Config.ALPHA2));

        Utils.driveStateful(x, noisyu0);
        Utils.turnStateful(x, noisyu1);
    }

    public double measurementModelMap(int[] z, MapStructure m) {
        RobotState robotState = m.getPosition();

        double sum = 0.0;

        for (int i = 0; i < RoombaConfig.SENSORS.length; i++) {
            Sensor s = RoombaConfig.SENSORS[i];
            RobotState sensorState = Utils.getSensorState(robotState, s);
            Point measurement = Utils.pointToGrid(Utils.sensorDataToPoint(
                    robotState, z[i], s));
//            ArrayList<Point> path = Utils.getPath(sensorState, s.zMax);
//            for (Point p : path) {
//                double x = m.get(p);
//                double logy = inverseSensorModel_old(p, measurement, sensorState, z[i], s);
//                double y = 1 - (1 / (1 + Math.exp(logy)));
//
//                //sum += Math.abs(x + y);
//                sum += 1 - Math.abs(x - y);
//                // sum += x * y;
//            }
            
          ArrayList<Point> path = Utils.getDetailedPath(sensorState, s.zMax, 1);
            
          HashMap<Point, Double> map = new HashMap<Point, Double> (); 
//          System.out.print("ism: ");
          for (Point p : path) {
          	double ism = inverseSensorModel_new(p, measurement, sensorState, z[i], s);
          	Point p2grid = Utils.pointToGrid(p);
          	if (map.containsKey(p2grid)) {
          		map.put(p2grid, map.get(p2grid) + ism);
          	} else {
          		map.put(p2grid, ism);
          	}
          }
          double max = 0;
          for (Point p : map.keySet()) {
            double x = m.get(p);
            double y = map.get(p);
            double tmp = x * y;
            if(tmp > max) max = tmp;
          }
          sum += max;
        }
        return sum;
    }

    public void updatedOccupancyGrid(int[] z, MapStructure m) {
        RobotState robotState = m.getPosition();
        for (int i = 0; i < RoombaConfig.SENSORS.length; i++) {
            Sensor s = RoombaConfig.SENSORS[i];
            RobotState sensorState = Utils.getSensorState(robotState, s);
            Point measurement = Utils.pointToGrid(Utils.sensorDataToPoint(
                    robotState, z[i], s));
            List<Point> path = Utils.getPath(sensorState, s.zMax);
            
            for (Point p : path) {
            	double ism = inverseSensorModel_old(p, measurement, sensorState, z[i], s);
            	double logOdds = m.getLogOdds(p) + ism;
            	m.putLogOdds(p, logOdds);
            }
        }
    }

    public static double inverseSensorModel_old(Point p, Point measurement,
            RobotState sensorState, int z, Sensor s) {
        double result;
        int r = Utils.euclideanDistance(
                new Point(sensorState.x, sensorState.y), p);
        if (z == -1) {
            result = Config.LOGODD_START;
        } else if (r > Math.min(s.zMax, z) + Config.GRID_CELL_SIZE) {
            result = Config.LOGODD_START;
        } else if (z < s.zMax && p.equals(measurement)) {
            result = Config.LOGODD_OCCUPIED_CORRECT;
        } else if (r < z) {
            result = Config.LOGODD_OCCUPIED_WRONG;
        } else {
            result = Config.LOGODD_START;
        }
        return result;
    }
    
    public static double inverseSensorModel_new(Point p, Point measurement,
            RobotState sensorState, int z, Sensor s) {
        double result;
        int r = Utils.euclideanDistance(new Point(sensorState.x, sensorState.y), p);
        
        if (z == -1) {
        	result = 0;
        } else {
        	result = Utils.gaussian(r, z, s.sigma);
        }
        return result;
    }
}
