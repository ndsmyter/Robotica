package common;

public class Config {
	/*************************
	 * Algemene instellingen *
	 *************************/
	public static final int GRID_CELL_SIZE = 20;
	public static final int GOAL_REACHED_TRESHOLD = 10;
	public static final int NUMBER_OF_PARTICLES = 250;
	
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
	public static final int ITERATIONS_PER_RESAMPLE = 10;
	public static final double ALPHA1 = 0.1;
	public static final double ALPHA2 = 0.1;
	
	/**************
	 * Bug config *
	 **************/
	public static final int NROFGOALS = 50;
	public static final int BUG_STEP = 20;
	public static final int BUG_TURN = 10;
	public static final int BUG_SPIRAL = 1000;
}
