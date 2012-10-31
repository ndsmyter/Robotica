package common;

import java.awt.Point;

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
}
