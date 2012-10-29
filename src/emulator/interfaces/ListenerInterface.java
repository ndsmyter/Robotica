package emulator.interfaces;

import emulator.Event;

public interface ListenerInterface {
	/**
	 * Message that will receive the notifications if a state has changed
	 * @param event The event that has happened
	 */
	public void stateChanged(Event event);
}
