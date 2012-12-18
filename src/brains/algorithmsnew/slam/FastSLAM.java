package brains.algorithmsnew.slam;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
    	System.out.print("[SLAM]");
    	for(int i: z){
    		System.out.print(i +", ");
    	}System.out.println();
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
                p.setWeight(0);
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
            ArrayList<Point> path = Utils.getPath(sensorState, s.zMax);
            for (Point p : path) {
                double x = m.get(p);
                double logy = inverseSensorModel(p, measurement, sensorState, z[i], s);
                double y = 1 - (1 / (1 + Math.exp(logy)));

                //sum += Math.abs(x + y);
                sum += 1 - Math.abs(x - y);
                // sum += x * y;
            }
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
            ArrayList<Point> path = Utils.getPath(sensorState, s.zMax);
            for (Point p : path) {
                double logOdds = m.getLogOdds(p)
                        + inverseSensorModel(p, measurement, sensorState, z[i],
                        s);
                m.putLogOdds(p, logOdds);
            }
        }
    }

    public static double inverseSensorModel(Point p, Point measurement,
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
}
