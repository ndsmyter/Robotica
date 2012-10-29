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
	 * Turn the robot for the amount of time according to the given direction
	 * 
	 * @param milliseconds
	 *            The amount of time the robot should turn
	 * @param right
	 *            What is the direction in which the robot should turn
	 */
	public void turn(int milliseconds, boolean right);

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
