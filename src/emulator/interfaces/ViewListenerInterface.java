package emulator.interfaces;

import java.util.EventListener;

import emulator.Event;

public interface ViewListenerInterface extends EventListener {
	/**
	 * Message that will receive the notifications if a state has changed
	 * 
	 * @param event
	 *            The event that has happened
	 */
	public void viewStateChanged(Event event);
}
