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
        int length = euclideanDistance(new Point(currentState.x,currentState.y), new Point(nextState.x, nextState.y));
        return getPath(currentState, length, 0);
    }

    public static ArrayList<Point> getPath(RobotState currentState, int distance) {
        return getPath(currentState, distance, 0);
    }
    
    public static ArrayList<Point> getPath(RobotState currentState, int distance, int width){
        ArrayList<Point> path = new ArrayList<Point>();
        RobotState current = new RobotState(currentState.x, currentState.y, currentState.dir - 90);
        current = driveForward(current,width/2);      
        current.dir = currentState.dir;
        RobotState intermediate;
        for (int w = 0; w <= width ; w+= Config.GRID_SIZE) {
            for (int d = 0; d < distance; d += Config.GRID_SIZE) {
                intermediate = driveForward(current, d);
                path.add(pointToGrid(new Point(intermediate.x, intermediate.y)));
            }
            current.dir += 90;
            current = driveForward(current,Config.GRID_SIZE);
            current.dir -= 90;
        }
        return path;
    }

    public static Point pointToGrid(Point p) {
        p.x = p.x - p.x % Config.GRID_SIZE;
        p.y = p.y - p.y % Config.GRID_SIZE;
        return p;
    }
}
