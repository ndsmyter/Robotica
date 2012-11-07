package common;

import java.awt.Point;
import java.util.ArrayList;

public class Utils {
	public static Point sensorDataToPoint(RobotState currentState,
			int distance, Sensor sensor) {
		RobotState sensorState = getSensorState(currentState, sensor);
		double sensorTheta = (sensorState.dir) * Math.PI / 180;
		int obstacleX = (int) (sensorState.x + distance * Math.cos(sensorTheta) + 0.5);
		int obstacleY = (int) (sensorState.y + distance * Math.sin(sensorTheta) + 0.5);
		return new Point(obstacleX, obstacleY);
	}

	public static RobotState getSensorState(RobotState currentState,
			Sensor sensor) {
		double theta = currentState.dir * Math.PI / 180;
		int sensorX = (int) (currentState.x + sensor.xOffset * Math.cos(theta) + sensor.yOffset
				* Math.cos(theta + Math.PI / 2));
		int sensorY = (int) (currentState.y + sensor.xOffset * Math.sin(theta) + sensor.yOffset
				* Math.sin(theta + Math.PI / 2));
		int sensorDir = (currentState.dir + sensor.dir);
		return new RobotState(sensorX, sensorY, sensorDir);
	}

	public static int euclideanDistance(Point point1, Point point2) {
		return (int) (Math.sqrt((point1.x - point2.x) * (point1.x - point2.x)
				+ (point1.y - point2.y) * (point1.y - point2.y)) + 0.5);
	}

	public static RobotState driveForward(RobotState currentState, int distance) {
		double theta = currentState.dir * Math.PI / 180;
		int nextX = (int) (currentState.x + distance * Math.cos(theta) + 0.5);
		int nextY = (int) (currentState.y + distance * Math.sin(theta) + 0.5);
		return new RobotState(nextX, nextY, currentState.dir);
	}

	public static ArrayList<Point> getPath(RobotState currentState,
			RobotState nextState) {
		ArrayList<Point> path = new ArrayList<Point>();
		currentState.x = currentState.x - (currentState.x % Config.GRID_SIZE);
		currentState.y = currentState.y - (currentState.y % Config.GRID_SIZE);
		int length = euclideanDistance(
				new Point(currentState.x, currentState.y), new Point(
						nextState.x, nextState.y));
		for (int i = 0; i < length; i += Config.GRID_SIZE) {
			RobotState intermediate = driveForward(currentState, i);
			path.add(new Point(intermediate.x, intermediate.y));
		}
		return path;
	}

	public static ArrayList<Point> getPath(RobotState currentState, int distance) {
		ArrayList<Point> path = new ArrayList<Point>();
		currentState.x = currentState.x - (currentState.x % Config.GRID_SIZE);
		currentState.y = currentState.y - (currentState.y % Config.GRID_SIZE);
		for (int i = 0; i <= distance; i += Config.GRID_SIZE) {
			RobotState intermediate = driveForward(currentState, i);
			path.add(new Point(intermediate.x, intermediate.y));
		}
		return path;
	}
}
