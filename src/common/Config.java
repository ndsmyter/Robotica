package common;

public class Config {
	/******************
	 * General config *
	 ******************/
	public static final int GRID_CELL_SIZE = 100;
	public static final int GOAL_REACHED_TRESHOLD = 100;
	public static final int NUMBER_OF_PARTICLES = 1000;
	
    /********************
	 * Simulator config *
	 ********************/
	public static final double SIMULATED_MOVEMENT_NOISE_PCT = 0.1;
	public static final double SIMULATED_ROTATION_NOISE_PCT = 0.1;
	public static final double SIMULATED_SENSOR_NOISE_PCT = 0;
	
	public static final int SIMULATED_STEP_SIZE = 10;
	
	/*******************
	 * FastSLAM config *
	 *******************/
	public static final int ITERATIONS_PER_RESAMPLE = 5;
	public static final double ALPHA1 = 0.3;
	public static final double ALPHA2 = 0.3;
	public static final double LOGODD_OCCUPIED_CORRECT = 0.6; // p(occupied | z) = 0.8 => log 0.8/0.2 = log 4 = 0.6
        public static final double LOGODD_OCCUPIED_WRONG = -0.6; // p(occupied | z) = 0.2 => 0.2/0.8 = log 0.25 = -0.6
        public static final double LOGODD_START = 0;
        
	/**************
	 * Bug config *
	 **************/
	public static final int NROFGOALS = 50;
	public static final int BUG_STEP = 200;
	public static final int BUG_TURN = 15;
	public static final int BUG_SPIRAL = 900;
}
