package emulator;

/**
 * An enum to make a difference between different events
 * 
 * @author Nicolas
 * 
 */
public enum EventType {
	DRIVE, // The robot drives forward
	TURN, // The robot turns
	OBSTACLE, // The robot detected an obstacle
	COLLISION, // The robot has collided with an object
	LOG // A log message
}
