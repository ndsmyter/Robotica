package roomba.interfaces;

public interface RoombaInterface {
	/**
	 * Drive for a certain distance
	 * 
	 * Drive mode of the roomba can by slow (100 mm/s), med (300 mm/s) or fast (500 mm/s)
	 * 
	 * @param millimeters - The distance the robot should drive (negative number to drive backwards)
	 * @param drive_mode - The drive mode of the Roomda
	 */
	public void drive(int millimeters, int drive_mode);
	
	/**
	 * Drive for a certain distance
	 * Standard mode is chosen (med -> 300 mm/s)
	 * 
	 * @param millimeters - The distance the robot should drive (negative number to drive backwards)
	 */
	public void drive(int millimeters);

	/**
	 * Turn the robot
	 * 
	 * @param degrees - number of degrees to turn
	 * @param turnRight - true to turn right (clockwise), false to turn left
	 * @param turn_mode - turning modi (spot, sharp, wide, verywide)
	 */
	public void turn(int degrees, boolean turnRight, int turn_mode, int drive_mode);
	
	/**
	 * Turn the robot at the spot.
	 * 
	 * @param degrees - number of degrees to turn
	 * @param turnRight - true to turn right (clockwise), false to turn left
	 */
	public void turnAtSpot(int degrees, boolean turnRight);
	
	
}
