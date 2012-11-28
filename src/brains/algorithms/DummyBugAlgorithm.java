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
    private ArrayList<Point> goals;
    private int goalIndex;
    private ArrayList<Point> straightPath;
    private int straightDir;
    private Point lastPosition;
    private ArrayList<Point> obstaclePositions;
    private boolean followingObstacle;

    public DummyBugAlgorithm(Brains b) {
        this.b = b;
    }

    public void run(Brains b) {
        this.b = b;
        while (!b.isStopped()) { // i < MAX_VALUE &&
            doStep(b);
        }
    }

    public void doStep(Brains b) {
        this.b = b;
        // System.out.println(b.getCurrentState());
        processSensorData(b);
        MapStructure map = b.getMap();
        RobotState robotState = map.getPosition();
        Point currentOnGrid = Utils.pointToGrid(new Point(robotState.x,
                robotState.y));
        System.out.println("Current : "+ robotState +" (=> "+currentOnGrid+")");
        if (currentOnGrid.equals(goals.get(goalIndex))) {
            System.out.println("Goal "+goalIndex+" reached! :D");
            goalIndex++;
            if (goalIndex >= goals.size()) {
                b.stop(true);
            } else {
                setGoal(robotState, goals.get(goalIndex));
            }
        } else if (followingObstacle) {
            int c = obstaclePositions.indexOf(currentOnGrid);
            if (c > 0 && c < obstaclePositions.size() - 5) {
                System.out.println("Goal "+goalIndex+" is unreachable! D:");
                goalIndex++;
                if (goalIndex >= goals.size()) {
                    b.stop(true);
                } else {
                    setGoal(robotState, goals.get(goalIndex));
                }
            } else {
                obstaclePositions.add(currentOnGrid);
                // Following an obstacle
                if (straightPath.contains(currentOnGrid) && Utils.euclideanDistance(lastPosition, goals.get(goalIndex)) > Utils
                        .euclideanDistance(currentOnGrid, goals.get(goalIndex))) {
                    System.out.println("Found the path again! ^^");
                    setGoal(robotState, goals.get(goalIndex));
                } else {
                    // Keep following the obstacle
                    processSensorData(b);
                    // Check if it would be possible to move forward right now
                    boolean free = true;
                    ArrayList<Point> path;
                    int totalTurn = 0;
                    // Turn right untill she finds the obstacle, to follow the
                    // obstacle
                    while (free && totalTurn <= 360) {
                        b.turn(TURN, true);
                        totalTurn += TURN;
                        processSensorData(b);
                        path = Utils.getPath(robotState, STEP
                                + RoombaConfig.ROOMBA_DIAMETER / 2,
                                RoombaConfig.ROOMBA_DIAMETER);
                        for (Point p : path) {
                            free &= (map.get(p) < 0.60);
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
                            path = Utils.getPath(robotState, STEP
                                    + RoombaConfig.ROOMBA_DIAMETER / 2,
                                    RoombaConfig.ROOMBA_DIAMETER);
                            for (Point p : path) {
                                free &= (map.get(p) < 0.60);
                            }
                        }
                    }
                    b.drive(STEP);
                }
            }
        } else {
            // On the straight line
            boolean free = true;
            ArrayList<Point> path = Utils.getPath(robotState, STEP
                    + RoombaConfig.ROOMBA_DIAMETER / 2,
                    RoombaConfig.ROOMBA_DIAMETER);
            for (Point p : path) {
                free &= (map.get(p) < 0.60);
            }

            if (free) {
                // Stay on the straight line
                b.drive(STEP);
            } else {
                // Reached an obstacle
                System.out.println("Encountered an obstacle! :(");
                lastPosition = Utils.pointToGrid(new Point(robotState.x,
                        robotState.y));
                followingObstacle = true;
            }
        }
    }

    public void reset() {
        goalIndex = 0;
        goals = new ArrayList<Point>();
        goals.add(new Point(300, 300));
        goals.add(new Point(-300, 300));
        goals.add(new Point(-300, -300));
        goals.add(new Point(-200, -200));
        goals.add(new Point(0, 0));
        MapStructure map = b.getMap();
        RobotState robotState = map.getPosition();
        straightDir = Utils.angle(new Point(robotState.x, robotState.y), goals.get(goalIndex));
        System.out.println("Dir: " + straightDir);
        // b.turn(straightDir, false);
        robotState.dir = straightDir;
        straightPath = Utils.getPath(robotState, new RobotState(goals.get(goalIndex), 0));
        System.out.println("Path: ");
        for (Point p : straightPath) {
            System.out.print(p + ", ");
        }
        System.out.println("");
        followingObstacle = false;
        obstaclePositions = new ArrayList<Point>();
    }

    public void setGoal(RobotState robotState, Point goal) {
        followingObstacle = false;
        obstaclePositions.clear();
        straightDir = Utils.angle(new Point(robotState.x, robotState.y), goals.get(goalIndex));
        System.out.println("Dir: "+straightDir);
        b.turn(straightDir - robotState.dir, false);
        straightPath = Utils.getPath(robotState, new RobotState(goals.get(goalIndex), 0));
    }

    public void processSensorData(Brains b) {
        int[] z = b.getSensorData();
        MapStructure m2 = updatedOccupancyGrid(z, b.getMap());
        b.setMap(m2);
    }

    public static MapStructure updatedOccupancyGrid(int[] z, MapStructure m) {
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
