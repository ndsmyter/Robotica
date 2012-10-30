package emulator;

public class Event {

	private EventType eventType;

	private String message;
	private int distance;
	private int degrees;
	private boolean turnRight;
	private int driveMode;

	/**
	 * Make an event with given eventType and data
	 * 
	 * @param eventType
	 *            The type of event
	 * @param msg
	 *            The message of the event
	 */
	public Event(EventType eventType, String msg) {
		this(eventType);
		this.message = msg;
	}

	public Event(EventType eventType, int distance, int degrees,
			boolean turnRight, int driveMode) {
		this(eventType);
		this.distance = distance;
		this.degrees = degrees;
		this.turnRight = turnRight;
		this.driveMode = driveMode;
	}

	public Event(EventType eventType, int distance, int driveMode) {
		this(eventType, distance, 0, false, driveMode);
	}

	public Event(EventType eventType) {
		this.eventType = eventType;
	}

	/**
	 * Return the event type
	 * 
	 * @return The type of event
	 */
	public EventType getType() {
		return eventType;
	}

	/**
	 * Set the new type for this event
	 * 
	 * @param eventType
	 *            The type of event
	 */
	public void setEventType(EventType eventType) {
		this.eventType = eventType;
	}

	/**
	 * Return the message of the event
	 * 
	 * @return The message of the event
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Set the message of the event
	 * 
	 * @param message
	 *            The message of the event
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * @return the distance
	 */
	public int getDistance() {
		return distance;
	}

	/**
	 * @param distance
	 *            the distance to set
	 */
	public void setDistance(int distance) {
		this.distance = distance;
	}

	/**
	 * @return the degrees
	 */
	public int getDegrees() {
		return degrees;
	}

	/**
	 * @param degrees
	 *            the degrees to set
	 */
	public void setDegrees(int degrees) {
		this.degrees = degrees;
	}

	/**
	 * @return the turnRight
	 */
	public boolean isTurnRight() {
		return turnRight;
	}

	/**
	 * @param turnRight
	 *            the turnRight to set
	 */
	public void setTurnRight(boolean turnRight) {
		this.turnRight = turnRight;
	}

	/**
	 * @return the driveMode
	 */
	public int getDriveMode() {
		return driveMode;
	}

	/**
	 * @param driveMode
	 *            the driveMode to set
	 */
	public void setDriveMode(int driveMode) {
		this.driveMode = driveMode;
	}

}
