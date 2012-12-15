package roomba.interfaces;

public interface RoombaInterface {

	/**
	 * Sends start command to roomba
	 */
	public void start();

	/**
	 * Sends the mode to the roomba
	 * 
	 * @param roombaMode
	 *            The mode to select.
	 */
	public void selectMode(int roombaMode);

	/**
	 * Drive for a certain distance
	 * 
	 * Drive mode of the roomba can by slow (100 mm/s), med (300 mm/s) or fast
	 * (500 mm/s)
	 * 
	 * @param millimeters
	 *            The distance the robot should drive (negative number to drive
	 *            backwards)
	 * @param driveMode
	 *            The drive mode of the Roomda
	 */
	public void drive(int millimeters, int driveMode);

	/**
	 * Drive for a certain distance Standard mode is chosen (med -> 300 mm/s)
	 * 
	 * @param millimeters
	 *            The distance the robot should drive (negative number to drive
	 *            backwards)
	 */
	public void drive(int millimeters);

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
	public void turn(int degrees, boolean turnRight, int turnMode, int driveMode);

	/**
	 * Turn the robot at the spot.
	 * 
	 * @param degrees
	 *            number of degrees to turn
	 * @param turnRight
	 *            true to turn right (clockwise), false to turn left
	 */
	public void turnAtSpot(int degrees, boolean turnRight);

	/**
	 * Sends stop command to roomba
	 */
	public void stop();
	
	/**
	 * Get sensor data from range sensors
	 * @param ids - byte[] with id's
	 */
	public int[] getSensorData(byte[] ids);

}
