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
	TURN_RIGHT, // The robot turns right
	TURN_LEFT, // The robot turns left
	COLLISION, // The robot has collided with an object
	LOG // A log message
}
