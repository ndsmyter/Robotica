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
	private ArrayList<Point> continuePath;
	private ArrayList<Point> straightPath = null;
	private int straightDir;
	private Point lastPosition;
	private ArrayList<Point> obstaclePositions;
	private boolean followingObstacle;
	private Stopper stopper;
	private int internalState;
	private int totalTurn = 0;
	private int exploreCounter;

	private Point goal;
        private Point tmpGoal;
	private int dir;
	private boolean goalChanged = false;
	private boolean stopWhenGoalReached = false;

	public BugExplore(Stopper stopper) {
		this.stopper = stopper;
	}

	public int[] setGoal(RobotState robotState) {
		followingObstacle = false;
		obstaclePositions.clear();
		straightDir = Utils.angle(new Point(robotState.x, robotState.y), goal);
		System.out.println("Dir: " + straightDir);
		straightPath = Utils.getPath(robotState, new RobotState(goal, 0));
		continuePath = Utils.getFullPath(robotState, new RobotState(goal, 0));
		return turn(straightDir - robotState.dir);
	}

        public void setGoal(Point p) {
            this.tmpGoal = p;
            goalChanged = true;
            stopWhenGoalReached = true;
        }

        @Override
        public void reset() {
            internalState = -1;
            goalIndex = 0;
            // initialise spiral
            dir = 1;
            goal = new Point(0, 0);
            goalIndex = 0;
            exploreCounter = 0;

            followingObstacle = false;
            obstaclePositions = new ArrayList<Point>();
            stopWhenGoalReached = false;
            if (straightPath != null) {
                straightPath.clear();
            }
        }

	@Override
	public int[] explore(MapStructure map) {
		RobotState robotState = map.getPosition();
		if (goalChanged) {
                    // The goal has been changed in the mean while, use this new goal
                    goal = tmpGoal;
                    goalChanged = false;
                    internalState = 0;
                    return setGoal(robotState);
                }
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
			if (Utils.goalReached(currentOnGrid,
					Utils.pointToGrid(new Point(goal.x, goal.y)))) {
				System.out.println("Goal " + goalIndex + " reached! :D");
				goalIndex++;
				getNextGoal();
				if (stopWhenGoalReached) {
					stopper.execute();
					return home();
				} else if (goalIndex >= Config.NROFGOALS) {
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
					if (stopWhenGoalReached) {
						stopper.execute();
						return home();
					} else if (goalIndex >= Config.NROFGOALS) {
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
					}
					else if(continuePath.contains(currentOnGrid) && !straightPath.contains(currentOnGrid)
							&& Utils.euclideanDistance(lastPosition, goal) > Utils
							.euclideanDistance(currentOnGrid, goal)){
						System.out.println("Found the continuation of the path");
						return setGoal(robotState);
					}
					else {
						// Turn right until she finds the obstacle, to follow
						// the obstacle
						internalState = 1;

						totalTurn = Config.BUG_TURN;
						return turn(-Config.BUG_TURN);
					}
				}
			} else {
				// On the straight line
				boolean free = Utils.isPathFree(robotState, Config.BUG_STEP,
						map);

				if (free) {
					// Stay on the straight line
					int distance = Utils.crossesPath(goal, robotState);
					if (distance != 0)
						return drive(distance);
					else
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
				exploreCounter++;
				// totalTurn = Config.BUG_TURN;
				// return turn(Config.BUG_TURN);

				if (Config.BUG_EXPLORE_OBSTACLES_MORE
						&& exploreCounter % Config.BUG_OBST_EXPLORE_ITERATIONS == 0) {
					totalTurn = -Config.BUG_OBST_EXPLORE_TURN;
					return turn(-Config.BUG_OBST_EXPLORE_TURN);
				} else {
					totalTurn = Config.BUG_TURN;
					return turn(Config.BUG_TURN);
				}
			} else {
				// where did the obstacle go?
				internalState = 0;
				totalTurn = 0;
				Point currentOnGrid = Utils.pointToGrid(new Point(robotState.x,
						robotState.y));
				int distance = Utils.crossesPath(straightPath, robotState);
				if (distance != 0 && Utils.euclideanDistance(lastPosition, goal) > Utils
						.euclideanDistance(currentOnGrid, goal))
					return drive(distance);
				else
					return drive(Config.BUG_STEP);
			}
		} else if (internalState == 2) {
			boolean free = Utils.isPathFree(robotState, Config.BUG_STEP, map);

			if (!free && totalTurn <= 360) {
				totalTurn += Config.BUG_TURN;
				return turn(Config.BUG_TURN);
			} else {
				internalState = 0;
				Point currentOnGrid = Utils.pointToGrid(new Point(robotState.x,
						robotState.y));
				int distance = Utils.crossesPath(straightPath, robotState);
				if (distance != 0 && Utils.euclideanDistance(lastPosition, goal) > Utils
						.euclideanDistance(currentOnGrid, goal))
					return drive(distance);
				else
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

		System.out.println("Next goal: " + goal.x + " : " + goal.y +  " " + dir);

		return goal;
	}
	
	private Point getNextGoal() {
		if (dir == 0) { // UP
			goal.y = Math.abs(goal.x);
			dir = 1;
		} else if (dir == 1) { // RIGHT
			goal.x = Math.abs(goal.y) + Config.BUG_SPIRAL;
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

	public Point getGoal() {
		return goal;
	}

	public ArrayList<Point> getGoalPath() {
		return straightPath;
	}
}
