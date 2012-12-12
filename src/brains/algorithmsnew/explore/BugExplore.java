package brains.algorithmsnew.explore;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import roomba.RoombaConfig;
import brains.MapStructure;
import brains.algorithmsnew.Stopper;

import common.RobotState;
import common.Utils;

public class BugExplore extends ExploreAlgorithmInterface {

	// Step length in mm, turn in degrees
	private static final int STEP = 10;
	private static final int TURN = 5;
	private static final int SPIRAL = 50;

	private ArrayList<Point> goals;
	private int goalIndex;
	private ArrayList<Point> straightPath;
	private int straightDir;
	private Point lastPosition;
	private ArrayList<Point> obstaclePositions;
	private boolean followingObstacle;
	private Stopper stopper;
	private int internalState;
	private boolean free = true;
	private int totalTurn = 0;
	
	private Point spiralPoint;
	private int dir;

	public BugExplore(Stopper stopper) {
		this.stopper = stopper;
	}

	public int[] setGoal(RobotState robotState, Point goal) {
		followingObstacle = false;
		obstaclePositions.clear();
		straightDir = Utils.angle(new Point(robotState.x, robotState.y),
				goals.get(goalIndex));
		System.out.println("Dir: " + straightDir);
		straightPath = Utils.getPath(robotState,
				new RobotState(goals.get(goalIndex), 0));
		return turn(straightDir - robotState.dir);
	}

	@Override
	public void reset() {
		internalState = -1;
		goalIndex = 0;
		goals = new ArrayList<Point>();
		goals.add(new Point(1000, 1000));
		goals.add(new Point(-300, 300));
		goals.add(new Point(-300, -300));
		goals.add(new Point(-200, -200));
		goals.add(new Point(0, 0));
		
		//initialise spiral
		dir = 0;
		spiralPoint = new Point(0, -SPIRAL);
		
		followingObstacle = false;
		obstaclePositions = new ArrayList<Point>();
	}

	@Override
	public int[] explore(MapStructure map) {
		RobotState robotState = map.getPosition();
		if (internalState == -1) {
			straightDir = Utils.angle(new Point(robotState.x, robotState.y),
					goals.get(goalIndex));
			System.out.println("Dir: " + straightDir);
			straightPath = Utils.getPath(robotState,
					new RobotState(goals.get(goalIndex), 0));
			System.out.println("Path: ");
			for (Point p : straightPath) {
				System.out.print("point(" + p.x + ", " + p.y + "), ");
			}
			System.out.println("");
			internalState = 0;

			return turn(straightDir);
		} else if (internalState == 0) {
			Point currentOnGrid = Utils.pointToGrid(new Point(robotState.x,
					robotState.y));
			System.out.println("Current : " + robotState + " (=> "
					+ currentOnGrid + ")");

			if (currentOnGrid.equals(goals.get(goalIndex))) {
				System.out.println("Goal " + goalIndex + " reached! :D");
				goalIndex++;
				if (goalIndex >= goals.size()) {
					stopper.execute();
					return dontMove();
				} else {
					return setGoal(robotState, goals.get(goalIndex));
				}
			} else if (followingObstacle) {
				int c = obstaclePositions.indexOf(currentOnGrid);
				if (c > 0 && c < obstaclePositions.size() - 5) {
					System.out.println("Goal " + goalIndex
							+ " is unreachable! D:");
					goalIndex++;
					if (goalIndex >= goals.size()) {
						stopper.execute();
						return dontMove();
					} else {
						return setGoal(robotState, goals.get(goalIndex));
					}
				} else {
					obstaclePositions.add(currentOnGrid);
					// Following an obstacle
					if (straightPath.contains(currentOnGrid)
							&& Utils.euclideanDistance(lastPosition,
									goals.get(goalIndex)) > Utils
									.euclideanDistance(currentOnGrid,
											goals.get(goalIndex))) {
						System.out.println("Found the path again! ^^");
						return setGoal(robotState, goals.get(goalIndex));
					} else {
						// Turn right until she finds the obstacle, to follow
						// the obstacle
						internalState = 1;

						free = true;
						totalTurn = TURN;
						return turn(-TURN);
					}
				}
			} else {
				// On the straight line
				ArrayList<Point> path = Utils.getPath(robotState, STEP
						+ RoombaConfig.ROOMBA_DIAMETER / 2,
						RoombaConfig.ROOMBA_DIAMETER);
				free = isPathFree(path, map);

				if (free) {
					// Stay on the straight line
					return drive(STEP);
				} else {
					// Reached an obstacle
					System.out.println("Encountered an obstacle! :(");
					lastPosition = Utils.pointToGrid(new Point(robotState.x,
							robotState.y));
					followingObstacle = true;
					return dontMove();
				}
			}
		} else if (internalState == 1) {
			List<Point> path = Utils.getPath(robotState, STEP
					+ RoombaConfig.ROOMBA_DIAMETER / 2,
					RoombaConfig.ROOMBA_DIAMETER);
			free = isPathFree(path, map);

			if (free && totalTurn <= 360) {
				totalTurn += TURN;
				return turn(-TURN);
			} else {
				if (!free) {
					// Then turn left again until she can move forward
					internalState = 2;
					totalTurn = TURN;
					return turn(TURN);
				} else {
					// where did the obstacle go?
					internalState = 0;
					totalTurn = 0;
					return drive(STEP);
				}
			}
		} else if (internalState == 2) {
			List<Point> path = Utils.getPath(robotState, STEP
					+ RoombaConfig.ROOMBA_DIAMETER / 2,
					RoombaConfig.ROOMBA_DIAMETER);
			free = isPathFree(path, map);

			if (!free && totalTurn <= 360) {
				totalTurn += TURN;
				return turn(TURN);
			} else {
				internalState = 0;
				return drive(STEP);
			}
		} else {
			System.err.println("BugMovement.java: NO SUCH INTERNAL STATE!");
			return dontMove();
		}
	}

	private boolean isPathFree(List<Point> path, MapStructure map) {
		boolean freeTmp = true;
		int points = path.size();
		for (int i = 0; i < points && freeTmp; i++)
			freeTmp &= (map.get(path.get(i)) < 0.60);
		return freeTmp;
	}
	
	private Point getNextSpiralPoint() {
		if ( dir == 0 ){
			spiralPoint.y = spiralPoint.y + SPIRAL;
			if (spiralPoint.y > spiralPoint.x)
				dir = 1;
		}else if ( dir == 1 ){
			spiralPoint.x = spiralPoint.x + SPIRAL;
			if (spiralPoint.x == spiralPoint.y)
				dir = 2;
		}else if ( dir == 2 ){
			spiralPoint.y = spiralPoint.y - SPIRAL;
			if (Math.abs(spiralPoint.y) == spiralPoint.x)
				dir = 3;
		}else if ( dir == 3 ){
			spiralPoint.x = spiralPoint.x - SPIRAL;
			if ( spiralPoint.y == spiralPoint.x)
				dir = 0;
		}
		
		return spiralPoint;
	}
}
