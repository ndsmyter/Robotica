package emulator;

public class Event {

	private EventType eventType;
	private Object data;
	private int time;

	/**
	 * Make an event with given eventType and data
	 * 
	 * @param eventType
	 *            The type of event
	 * @param data
	 *            The data of the event
	 */
	public Event(EventType eventType, Object data) {
		this(eventType);
		this.data = data;
	}

	public Event(EventType eventType, int time) {
		this(eventType);
		this.time = time;
	}

	public Event(EventType eventType) {
		this.eventType = eventType;
	}

	public Event(EventType eventType, int time, Object data) {
		this(eventType, time);
		this.data = data;
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

	/**
	 * @return the time
	 */
	public int getTime() {
		return time;
	}

	/**
	 * @param time
	 *            the time to set
	 */
	public void setTime(int time) {
		this.time = time;
	}

}
