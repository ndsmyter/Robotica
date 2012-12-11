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
		int diffX = point1.x - point2.x;
		int diffY = point1.y - point2.y;
		return (int) (Math.sqrt(diffX * diffX + diffY * diffY) + 0.5);
	}

	public static int angle(Point point1, Point point2) {
		return (int) Math.toDegrees(Math.atan2(point2.y - point1.y, point2.x
				- point1.x));
	}

	public static RobotState driveForward(RobotState currentState, int distance) {
		double theta = Math.toRadians(currentState.dir);
		int x = currentState.x + (int) (distance * Math.cos(theta));
		int y = currentState.y + (int) (distance * Math.sin(theta));
		return new RobotState(x, y, currentState.dir);
	}

	public static void driveStateful(RobotState state, int distance) {
		double theta = Math.toRadians(state.dir);
		state.x += (int) (distance * Math.cos(theta));
		state.y += (int) (distance * Math.sin(theta));
	}

	public static void turnStateful(RobotState state, int degrees) {
		state.dir = (state.dir + degrees + 360) % 360;
	}

	public static ArrayList<Point> getPath(RobotState currentState,
			RobotState nextState) {
		return getPath(currentState, nextState, 0);
	}

	public static ArrayList<Point> getPath(RobotState currentState,
			RobotState nextState, int width) {
		int distance = euclideanDistance(new Point(currentState.x,
				currentState.y), new Point(nextState.x, nextState.y));
		return getPath(currentState, distance, width);
	}

	public static ArrayList<Point> getPath(RobotState currentState, int distance) {
		return getPath(currentState, distance, 0);
	}

	/**
	 * Values returned by this method are points on the grid!
	 * 
	 * @param currentState
	 *            Current robot state
	 * @param distance
	 *            The distance of the path
	 * @param width
	 *            Width of the path
	 * @return
	 */
	public static ArrayList<Point> getPath(RobotState currentState,
			int distance, int width) {
		ArrayList<Point> path = new ArrayList<Point>();
		RobotState current = new RobotState(currentState.x, currentState.y,
				currentState.dir - 90);
		current = driveForward(current, width / 2);
		current.dir = currentState.dir;
		RobotState intermediate;
		for (int w = 0; w <= width; w += Config.GRID_CELL_SIZE) {
			for (int d = 0; d < distance; d += Config.GRID_CELL_SIZE) {
				intermediate = driveForward(current, d);
				path.add(pointToGrid(new Point(intermediate.x, intermediate.y)));
			}
			current.dir += 90;
			current = driveForward(current, Config.GRID_CELL_SIZE);
			current.dir -= 90;
		}
		return path;
	}

	public static Point pointToGrid(Point p) {
		p.x = formula(p.x);
		p.y = formula(p.y);
		return p;
	}

	public static RobotState stateToGrid(RobotState p) {
		p.x = formula(p.x);
		p.y = formula(p.y);
		return p;
	}

	// Named formula "Formula", because I don't know what it does (NDS)
	private static int formula(int x) {
		x += Config.GRID_CELL_SIZE / 2;
		return x - (x % Config.GRID_CELL_SIZE + Config.GRID_CELL_SIZE)
				% Config.GRID_CELL_SIZE;
	}

	public static boolean goalReached(Point robot, Point goal) {
		return euclideanDistance(robot, goal) < Config.GOAL_REACHED_TRESHOLD;
	}
}
