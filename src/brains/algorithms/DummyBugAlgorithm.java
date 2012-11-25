package brains.algorithms;

import java.awt.Point;
import java.util.ArrayList;

import roomba.RoombaConfig;
import brains.Brains;
import brains.MapStructure;

import common.Config;
import common.RobotState;
import common.Sensor;
import common.Utils;

public class DummyBugAlgorithm implements AlgorithmInterface {
    // Step length in mm, turn in degrees

    private static final int STEP = 10;
    private static final int TURN = 5;
    private Brains b;
    private Point goal;
    private ArrayList<Point> straightPath;
    private int straightDir;
    private Point lastPosition;
    private boolean followingObstacle;
    private boolean first;

    public DummyBugAlgorithm(Brains b) {
        this.b = b;
    }

    public void run(Brains b) {
        this.b = b;
        while (!b.isStopped()) { //i < MAX_VALUE &&
            doStep(b);
        }
    }

    public void doStep(Brains b) {
        this.b = b;
        // System.out.println(b.getCurrentState());
        processSensorData(b);
        Point currentOnGrid = Utils.pointToGrid(new Point(b.getCurrentState().x, b.getCurrentState().y));
        if (followingObstacle) {
//            if (!first && currentOnGrid.equals(lastPosition)) {
//                b.stop(true);
//                System.out.println("Goal is unreachable");
//            }
            first = false;
            // Following an obstacle
            System.out.println("Current: " + currentOnGrid);
            boolean closer = Utils.euclideanDistance(lastPosition, goal) > Utils.euclideanDistance(currentOnGrid, goal);
            if (straightPath.contains(currentOnGrid) && closer) {
                System.out.println("Found the path again!");
                followingObstacle = false;
                b.turn(straightDir - b.getCurrentState().dir, false);
            } else {
                // Keep following the obstacle
                processSensorData(b);
                // Check if it would be possible to move forward right now
                boolean free = true;
                ArrayList<Point> path;
                int totalTurn = 0;
                // Turn right untill she finds the obstacle, to follow the obstacle
                while (free && totalTurn <= 360) {
                    b.turn(TURN, true);
                    totalTurn += TURN;
                    processSensorData(b);
                    path = Utils.getPath(b.getCurrentState(), STEP
                            + RoombaConfig.ROOMBA_DIAMETER / 2, RoombaConfig.ROOMBA_DIAMETER);
                    for (Point p : path) {
                        free &= (b.getMap().get(p) < 0.60);
                    }
                }
                // Then turn left again untill she can move forward
                if (!free) {
                    totalTurn = 0;
                    while (!free && totalTurn <= 360) {
                        free = true;
                        b.turn(TURN, false);
                        totalTurn += TURN;
                        processSensorData(b);
                        path = Utils.getPath(b.getCurrentState(), STEP
                                + RoombaConfig.ROOMBA_DIAMETER / 2, RoombaConfig.ROOMBA_DIAMETER);
                        for (Point p : path) {
                            free &= (b.getMap().get(p) < 0.60);
                        }
                    }
                }
                b.drive(STEP);
            }
        } else {
            // On the straight line
            boolean free = true;
            ArrayList<Point> path = Utils.getPath(b.getCurrentState(), STEP
                    + RoombaConfig.ROOMBA_DIAMETER / 2, RoombaConfig.ROOMBA_DIAMETER);
            for (Point p : path) {
                free &= (b.getMap().get(Utils.pointToGrid(p)) < 0.60);
            }

            if (free) {
                // Stay on the straight line
                b.drive(STEP);
            } else {
                // Reached an obstacle
                lastPosition = Utils.pointToGrid(new Point(b.getCurrentState().x, b.getCurrentState().y));
                followingObstacle = true;
                first = true;
            }
        }
    }

    public void reset() {
        goal = new Point(1000, 1000);
        straightDir = Utils.angle(new Point(b.getCurrentState().x, b.getCurrentState().y), goal);
        System.out.println("Dir: " + straightDir);
        //b.turn(straightDir, false);
        b.getCurrentState().dir = straightDir;
        straightPath = Utils.getPath(b.getCurrentState(), new RobotState(goal, 0));
        System.out.println("Path: ");
        for (Point p : straightPath) {
            System.out.print(p + ", ");
        }
        System.out.println("");
        followingObstacle = false;;
    }

    public void processSensorData(Brains b) {
        int[] z = b.getSensorData();
        RobotState x = b.getCurrentState();
        MapStructure m = b.getMap();
        MapStructure m2 = updatedOccupancyGrid(z, x, m);
        b.setMap(m2);
    }

    public static MapStructure updatedOccupancyGrid(int[] z, RobotState x,
            MapStructure m) {
        MapStructure mapNew = m.clone();
        for (int i = 0; i < RoombaConfig.SENSORS.length; i++) {
            Sensor s = RoombaConfig.SENSORS[i];
            RobotState sensorState = Utils.getSensorState(x, s);
            Point measurement = Utils.pointToGrid(Utils.sensorDataToPoint(x, z[i], s));
            ArrayList<Point> path = Utils.getPath(sensorState, s.zMax);
            for (Point p : path) {
                double logOdds = m.getLogOdds(p)
                        + inverseSensorModel(m, p, measurement, sensorState, z[i], s);
                mapNew.putLogOdds(p, logOdds);
            }
        }
        return mapNew;
    }

    public static double inverseSensorModel(MapStructure m, Point p, Point measurement,
            RobotState sensorState, int z, Sensor s) {
        double result = 0;
        int r = Utils.euclideanDistance(new Point(sensorState.x, sensorState.y), p);
        if (r > Math.min(s.zMax, z) + Config.GRID_SIZE) {
            result = 0;
        } else if (z < s.zMax && p.equals(measurement)) {
            result = 0.6; // p(occupied | z) = 0.8 => log 0.8/0.2 = log 4 = 0.6
        } else if (r < z) {
            result = -0.6; // p(occupied | z) = 0.2 => 0.2/0.8 = log 0.25 = -0.6
        }
        return result;
    }
}