package common;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;

import roomba.RoombaConfig;
import brains.MapStructure;

public class Utils {
	private static Random rand = new Random();

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
	
	public static ArrayList<Point> getFullPath(RobotState currentState,
			RobotState nextState){
		Point currentPoint = new Point(currentState.x,	currentState.y);
		Point nextPoint = new Point(nextState.x, nextState.y);
		int distance = euclideanDistance(currentPoint, nextPoint);
		RobotState r = new RobotState(currentPoint, Utils.angle(currentPoint, nextPoint));
		//TODO: 20 in config file plaatsen
		return getPath(r, 20 * distance, 0);
	}

	public static ArrayList<Point> getPath(RobotState currentState,
			RobotState nextState, int width) {
		Point currentPoint = new Point(currentState.x,	currentState.y);
		Point nextPoint = new Point(nextState.x, nextState.y);
		int distance = euclideanDistance(currentPoint, nextPoint);
		RobotState r = new RobotState(currentPoint, Utils.angle(currentPoint, nextPoint));
		return getPath(r, distance, width);
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
	
	public static ArrayList<Point> getDetailedPath(RobotState currentState,
			RobotState nextState, int stepSize) {
		return getDetailedPath(currentState, nextState, 0, stepSize);
	}

	public static ArrayList<Point> getDetailedPath(RobotState currentState,
			RobotState nextState, int width, int stepSize) {
		Point currentPoint = new Point(currentState.x,	currentState.y);
		Point nextPoint = new Point(nextState.x, nextState.y);
		int distance = euclideanDistance(currentPoint, nextPoint);
		RobotState r = new RobotState(currentPoint, Utils.angle(currentPoint, nextPoint));
		return getDetailedPath(r, distance, width, stepSize);
	}

	public static ArrayList<Point> getDetailedPath(RobotState currentState, int distance, int stepSize) {
		return getDetailedPath(currentState, distance, 0, stepSize);
	}
	
	public static ArrayList<Point> getDetailedPath(RobotState currentState,
			int distance, int width, int stepSize) {
		ArrayList<Point> path = new ArrayList<Point>();
		RobotState current = new RobotState(currentState.x, currentState.y,
				currentState.dir - 90);
		current = driveForward(current, width / 2);
		current.dir = currentState.dir;
		RobotState intermediate;
		for (int w = 0; w <= width; w += stepSize) {
			for (int d = 0; d < distance; d += stepSize) {
				intermediate = driveForward(current, d);
				path.add(new Point(intermediate.x, intermediate.y));
			}
			current.dir += 90;
			current = driveForward(current, stepSize);
			current.dir -= 90;
		}
		return path;
	}

	public static Point pointToGrid(Point p) {
		p.x = roundToGrid(p.x);
		p.y = roundToGrid(p.y);
		return p;
	}

	public static RobotState stateToGrid(RobotState p) {
		p.x = roundToGrid(p.x);
		p.y = roundToGrid(p.y);
		return p;
	}

	// Named formula "Formula", because I don't know what it does (NDS)
	private static int roundToGrid(int x) {
		x += Config.GRID_CELL_SIZE / 2;
		return x - (x % Config.GRID_CELL_SIZE + Config.GRID_CELL_SIZE)
				% Config.GRID_CELL_SIZE;
	}

	public static boolean goalReached(Point robot, Point goal) {
		return euclideanDistance(robot, goal) < Config.GOAL_REACHED_TRESHOLD;
	}
	
	// gaussian sample
	public static double gaussSample(double b2) {
		double result = 0;
		double r = Math.sqrt(b2);
		for (int i = 0; i < 12; i++) {
			result += (-r + rand.nextDouble() * 2 * r);
		}
		result /= 2;
		// Math.sqrt(6)/2 * (-r + rand.nextDouble() * r);
		return result;
	}
	
	public static double gaussSample(double b2, double o) {
		return gaussSample(b2) + o;
	}
	
	public static boolean isPathFree(RobotState robotState, int step, MapStructure map) {
		ArrayList<Point> path = Utils.getPath(
				robotState,
				step + RoombaConfig.ROOMBA_DIAMETER,
				RoombaConfig.ROOMBA_DIAMETER * 2
			);
		boolean freeTmp = true;
		int points = path.size();
		for (int i = 0; i < points && freeTmp; i++)
			freeTmp &= (map.get(path.get(i)) <= 0.51);
		return freeTmp;
	}
	
    // return phi(x) = standard Gaussian pdf
    public static double gaussian(double x) {
        return Math.exp(-x*x / 2) / Math.sqrt(2 * Math.PI);
    }

    // return phi(x, mu, signma) = Gaussian pdf with mean mu and stddev sigma
    public static double gaussian(double x, double mu, double sigma) {
        return gaussian((x - mu) / sigma) / sigma;
    }

    // return Phi(z) = standard Gaussian cdf using Taylor approximation
    public static double Gaussian(double z) {
        if (z < -8.0) return 0.0;
        if (z >  8.0) return 1.0;
        double sum = 0.0, term = z;
        for (int i = 3; sum + term != sum; i += 2) {
            sum  = sum + term;
            term = term * z * z / i;
        }
        return 0.5 + sum * gaussian(z);
    }

    // return Phi(z, mu, sigma) = Gaussian cdf with mean mu and stddev sigma
    public static double Gaussian(double z, double mu, double sigma) {
        return gaussian((z - mu) / sigma);
    } 

    // Compute z such that Phi(z) = y via bisection search
    public static double GaussianInverse(double y) {
        return GaussianInverse(y, .00000001, -8, 8);
    } 

    // bisection search
    private static double GaussianInverse(double y, double delta, double lo, double hi) {
        double mid = lo + (hi - lo) / 2;
        if (hi - lo < delta) return mid;
        if (Gaussian(mid) > y)
        	return GaussianInverse(y, delta, lo, mid);
        else
        	return GaussianInverse(y, delta, mid, hi);
    }
    
    public static double probToLogOdd(double p){
        return Math.log(p/(1-p));
    }
}
