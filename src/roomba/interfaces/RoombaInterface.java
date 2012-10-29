package roomba.interfaces;

public interface RoombaInterface {
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
}
