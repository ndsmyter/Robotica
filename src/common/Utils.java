package common;

import java.awt.Point;
import java.util.ArrayList;

public class Utils {
	public static Point sensorDataToPoint(RobotState currentState,
			int distance, Sensor sensor) {
		double theta = currentState.dir * Math.PI / 180;
		int sensorX = (int) (currentState.x + sensor.xOffset * Math.cos(theta) + sensor.yOffset
				* Math.cos(theta + Math.PI / 2));
		int sensorY = (int) (currentState.y + sensor.xOffset * Math.sin(theta) + sensor.yOffset
				* Math.sin(theta + Math.PI / 2));
		double sensorTheta = (currentState.dir + sensor.dir) * Math.PI / 180;
		int obstacleX = (int) (sensorX + distance * Math.cos(sensorTheta) + 0.5);
		int obstacleY = (int) (sensorY + distance * Math.sin(sensorTheta) + 0.5);
		return new Point(obstacleX, obstacleY);
	}
        
        public static RobotState driveForward(RobotState currentState, int distance){
		double theta = currentState.dir * Math.PI / 180;
		int nextX = (int) (currentState.x + distance * Math.cos(theta) + 0.5);
		int nextY = (int) (currentState.y + distance * Math.sin(theta) + 0.5);
		return new RobotState(nextX, nextY,currentState.dir);            
        }
        
        public static ArrayList<Point> getPath(RobotState currentState, RobotState nextState){
            ArrayList<Point> path = new ArrayList<Point>();
            int length = (int) Math.sqrt((currentState.x - nextState.x)^2 + (currentState.y - nextState.y)^2);
            for(int i = 0; i < length; i+=Config.GRID_SIZE){
                RobotState intermediate = driveForward(currentState, i);
                path.add(new Point(intermediate.x,intermediate.y));
            }
            return path;
        }
        
        public static ArrayList<Point> getPath(RobotState currentState, int distance){
            ArrayList<Point> path = new ArrayList<Point>();
            for(int i = 0; i <= distance; i+=Config.GRID_SIZE){
                RobotState intermediate = driveForward(currentState, i);
                path.add(new Point(intermediate.x,intermediate.y));
            }
            return path;
        }
}
