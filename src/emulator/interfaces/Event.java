package emulator.interfaces;

public class Event {
	/**
	 * The different event types
	 */
	public static enum EventType {
		NOTIFICATION, // Just a notification that something has happened
		COLLISION, // The robot has collided with an object
		WALL_FOUND, // If a wall has been detected
		OBJECT_FOUND, // If an object has been detected
	}

	private EventType eventType;

	/**
	 * Make a default Event, with event type NOTIFICATION
	 */
	public Event() {
		this(EventType.NOTIFICATION);
	}

	/**
	 * Make an event with given eventType
	 * 
	 * @param eventType
	 *            The type of event
	 */
	public Event(EventType eventType) {
		this.eventType = eventType;
	}

	/**
	 * Return the event type
	 * 
	 * @return The type of event
	 */
	public EventType getEventType() {
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

}
