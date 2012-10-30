package emulator.interfaces;

import emulator.Event;

public interface ViewListenerInterface {
	/**
	 * Message that will receive the notifications if a state has changed
	 * @param event The event that has happened
	 */
	public void viewStateChanged(Event event);
}

