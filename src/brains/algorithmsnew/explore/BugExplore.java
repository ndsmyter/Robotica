package brains.algorithmsnew.explore;

import java.awt.Point;
import java.util.ArrayList;

import brains.MapStructure;
import brains.algorithmsnew.Stopper;

import common.Config;
import common.RobotState;
import common.Utils;

public class BugExplore extends ExploreAlgorithmInterface {
	private int goalIndex;
	private ArrayList<Point> straightPath;
	private int straightDir;
	private Point lastPosition;
	private ArrayList<Point> obstaclePositions;
	private boolean followingObstacle;
	private Stopper stopper;
	private int internalState;
	private int totalTurn = 0;

	private Point goal;
	private int dir;

	public BugExplore(Stopper stopper) {
		this.stopper = stopper;
	}

	public int[] setGoal(RobotState robotState) {
		followingObstacle = false;
		obstaclePositions.clear();
		straightDir = Utils.angle(new Point(robotState.x, robotState.y), goal);
		System.out.println("Dir: " + straightDir);
		straightPath = Utils.getPath(robotState, new RobotState(goal, 0));
		return turn(straightDir - robotState.dir);
	}

	@Override
	public void reset() {
		internalState = -1;
		goalIndex = 0;
		// goals = new ArrayList<Point>();
		// goals.add(new Point(1000, 0));
		// goals.add(new Point(1000, 1000));
		// goals.add(new Point(-300, 300));
		// goals.add(new Point(-300, -300));
		// goals.add(new Point(-200, -200));
		// goals.add(new Point(0, 0));

		// initialise spiral
		dir = 0;
		goal = new Point(0, 0);
		goalIndex = 0;

		followingObstacle = false;
		obstaclePositions = new ArrayList<Point>();
	}

	@Override
	public int[] explore(MapStructure map) {
		RobotState robotState = map.getPosition();
		if (internalState == -1) {
			straightDir = Utils.angle(new Point(robotState.x, robotState.y),
					goal);
			System.out.println("Dir: " + straightDir);
			straightPath = Utils.getPath(robotState, new RobotState(goal, 0));
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

			if (Utils.goalReached(currentOnGrid, Utils.pointToGrid(goal))) {
				System.out.println("Goal " + goalIndex + " reached! :D");
				goalIndex++;
				getNextGoal();
				if (goalIndex >= Config.NROFGOALS) {
					stopper.execute();
					return dontMove();
				} else {
					return setGoal(robotState);
				}
			} else if (followingObstacle) {
				int c = obstaclePositions.indexOf(currentOnGrid);
				if (c > 0 && c < obstaclePositions.size() - 5) {
					System.out.println("Goal " + goalIndex
							+ " is unreachable! D:");
					goalIndex++;
					getNextGoal();
					if (goalIndex >= Config.NROFGOALS) {
						stopper.execute();
						return dontMove();
					} else {
						return setGoal(robotState);
					}
				} else {
					obstaclePositions.add(currentOnGrid);
					// Following an obstacle
					if (straightPath.contains(currentOnGrid)
							&& Utils.euclideanDistance(lastPosition, goal) > Utils
									.euclideanDistance(currentOnGrid, goal)) {
						System.out.println("Found the path again! ^^");
						return setGoal(robotState);
					} else {
						// Turn right until she finds the obstacle, to follow
						// the obstacle
						internalState = 1;

						totalTurn = Config.BUG_TURN;
						return turn(-Config.BUG_TURN);
					}
				}
			} else {
				// On the straight line
				boolean free = Utils.isPathFree(robotState, Config.BUG_STEP, map);

				if (free) {
					// Stay on the straight line
					return drive(Config.BUG_STEP);
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
			boolean free = Utils.isPathFree(robotState, Config.BUG_STEP, map);

			if (free && totalTurn <= 360) {
				totalTurn += Config.BUG_TURN;
				return turn(-Config.BUG_TURN);
			} else if (!free) {
				// Then turn left again until she can move forward
				internalState = 2;
				totalTurn = Config.BUG_TURN;
				return turn(Config.BUG_TURN);
			} else {
				// where did the obstacle go?
				internalState = 0;
				totalTurn = 0;
				return drive(Config.BUG_STEP);
			}
		} else if (internalState == 2) {
			boolean free = Utils.isPathFree(robotState, Config.BUG_STEP, map);

			if (!free && totalTurn <= 360) {
				totalTurn += Config.BUG_TURN;
				return turn(Config.BUG_TURN);
			} else {
				internalState = 0;
				return drive(Config.BUG_STEP);
			}
		} else {
			System.err.println("BugMovement.java: NO SUCH INTERNAL STATE!");
			return dontMove();
		}
	}
 
    private Point getNextGoalTMP() {
        if (dir == 0) { // UP
            goal.y = goal.y + Config.BUG_SPIRAL;
            if (goal.y > Math.abs(goal.x)) {
                dir = 1;
            }
        } else if (dir == 1) { // RIGHT
            goal.x = goal.x + Config.BUG_SPIRAL;
            if (goal.x == goal.y) {
                dir = 2;
            }
        } else if (dir == 2) { // DOWN
            goal.y = goal.y - Config.BUG_SPIRAL;
            if (Math.abs(goal.y) == goal.x) {
                dir = 3;
            }
        } else if (dir == 3) { // LEFT
            goal.x = goal.x - Config.BUG_SPIRAL;
            if (goal.y == goal.x) {
                dir = 0;
            }
        }

        System.out.println("Next goal: " + goal.x + " : " + goal.y);

        return goal;
    }

    private Point getNextGoal() {
        if (dir == 0) { // UP
            goal.y = Math.abs(goal.x) + Config.BUG_SPIRAL;
            dir = 1;
        } else if (dir == 1) { // RIGHT
            goal.x = goal.y;
            dir = 2;
        } else if (dir == 2) { // DOWN
            goal.y = -goal.x;
            dir = 3;
        } else if (dir == 3) { // LEFT
            goal.x = goal.y;
            dir = 0;
        }

        System.out.println("Next goal: " + goal.x + " : " + goal.y);

        return goal;
    }
}
