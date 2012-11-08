package common;

import java.awt.Point;
import java.util.ArrayList;

public class Utils {

    public static Point sensorDataToPoint(RobotState currentState,
            int distance, Sensor sensor) {
        RobotState sensorState = getSensorState(currentState, sensor);
        RobotState obstacle = driveForward(sensorState, distance);
        return new Point(obstacle.x, obstacle.y);
    }

    public static RobotState getSensorState(RobotState currentState,
            Sensor sensor) {        
        RobotState step1 = driveForward(currentState, sensor.xOffset);
        step1.dir = step1.dir + 90;
        RobotState step2 = driveForward(step1, sensor.yOffset);
        step2.dir = (currentState.dir + sensor.dir);
        return step2;
    }

    public static int euclideanDistance(Point point1, Point point2) {
        return (int) (Math.sqrt((point1.x - point2.x) * (point1.x - point2.x)
                + (point1.y - point2.y) * (point1.y - point2.y)) + 0.5);
    }

    public static RobotState driveForward(RobotState currentState, int distance) {
        double theta = Math.toRadians(currentState.dir);
        int x = currentState.x + (int) (distance * Math.cos(theta));
        int y = currentState.y + (int) (distance * Math.sin(theta));
        return new RobotState(x,y,currentState.dir);
        
    }

    public static ArrayList<Point> getPath(RobotState currentState,
            RobotState nextState) {
        ArrayList<Point> path = new ArrayList<Point>();
        Point currentG = pointToGrid(new Point(currentState.x, currentState.y));
        Point nextG = pointToGrid(new Point(nextState.x, nextState.y));
        int length = euclideanDistance(currentG, nextG);
        for (int i = 0; i < length; i += Config.GRID_SIZE) {
            RobotState intermediate = driveForward(currentState, i);
            path.add(new Point(intermediate.x, intermediate.y));
        }
        return path;
    }

    public static ArrayList<Point> getPath(RobotState currentState, int distance) {
        ArrayList<Point> path = new ArrayList<Point>();
        Point g = pointToGrid(new Point(currentState.x, currentState.y));
        currentState.x = g.x;
        currentState.y = g.y;
        for (int i = 0; i < distance; i += Config.GRID_SIZE) {
            RobotState intermediate = driveForward(currentState, i);
            path.add(new Point(intermediate.x, intermediate.y));
        }
        return path;
    }

    public static Point pointToGrid(Point p) {
        p.x = p.x - p.x % Config.GRID_SIZE;
        p.y = p.y - p.y % Config.GRID_SIZE;
        return p;
    }
}
