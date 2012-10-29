package emulator;

public class Event {

	private EventType eventType;
	private Object data;

	public Event() {
		this(null);
	}

	public Event(EventType eventType) {
		this(eventType, null);
	}

	/**
	 * Make an event with given eventType and data
	 * 
	 * @param eventType
	 *            The type of event
	 * @param data
	 *            The data of the event
	 */
	public Event(EventType eventType, Object data) {
		this.eventType = eventType;
		this.data = data;
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

	/**
	 * Return the data of the event
	 * 
	 * @return The data of the event
	 */
	public Object getData() {
		return data;
	}

	/**
	 * Set the data of the event
	 * 
	 * @param data
	 *            The data of the event
	 */
	public void setData(Object data) {
		this.data = data;
	}
}
