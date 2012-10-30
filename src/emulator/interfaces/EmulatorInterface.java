package emulator.interfaces;

public interface EmulatorInterface {
	/**
	 * Drive forward for the amount of time
	 * 
	 * @param milliseconds
	 *            The amount of milliseconds the robot should drive forward
	 */
	public void drive(int milliseconds);

	/**
	 * Turn the robot
	 * 
	 * @param degrees
	 *            number of degrees to turn
	 * @param turnRight
	 *            true to turn right (clockwise), false to turn left
	 * @param turnMode
	 *            turning modi (spot, sharp, wide, verywide)
	 */
	public void turn(int degrees, boolean turnRight, int turnMode,
			int driveMode);

	/**
	 * Turn the robot 90 degrees to the right
	 */
	public void turnRight();

	/**
	 * Turn the robot 90 degrees to the left
	 */
	public void turnLeft();

	/**
	 * This method will accept all log messages, which will be shown on the
	 * window
	 * 
	 * @param message
	 *            The message to show on the screen
	 */
	public void log(String message);

}
