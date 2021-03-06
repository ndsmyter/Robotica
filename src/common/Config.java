package common;

public class Config {
	/******************
	 * General config *
	 ******************/
	public static final int GRID_CELL_SIZE = 100;
	public static final int GOAL_REACHED_TRESHOLD = 100;
	public static final int NUMBER_OF_PARTICLES = 50;
        public static final boolean USE_ROOMBA = false;

	/********************
	 * Simulator config *
	 ********************/
	public static final double SIMULATED_MOVEMENT_NOISE_PCT = 0;
	public static final double SIMULATED_ROTATION_NOISE_PCT = 0;
	public static final double SIMULATED_SENSOR_NOISE_PCT = 0;

	public static final int SIMULATED_STEP_SIZE = 10;

	/*******************
	 * FastSLAM config *
	 *******************/
	public static final int ITERATIONS_PER_RESAMPLE = 500;
	public static final double ALPHA1 = 0.02;
	public static final double ALPHA2 = 0.01;
	// p(occupied | z) = 0.9 => log 0.9/0.1 = 0.95
	public static final double LOGODD_OCCUPIED_CORRECT = 0.95;
	// p(occupied | z) = 0.1 => log 0.1/0.9 = -0.95
	public static final double LOGODD_OCCUPIED_WRONG = -0.95;
	public static final double LOGODD_START = 0;
        public static final double DECAY_FACTOR = 0.8;

	/**************
	 * Bug config *
	 **************/
	public static final int NROFGOALS = 50;
	public static final int BUG_STEP = 100;
	public static final int BUG_TURN = 15;
	public static final int BUG_SPIRAL = 2000;

	public static final boolean BUG_EXPLORE_OBSTACLES_MORE = true;
	public static final int BUG_OBST_EXPLORE_TURN = 90;
	public static final int BUG_OBST_EXPLORE_ITERATIONS = 5;
}
