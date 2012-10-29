package emulator.interfaces;

import java.util.ArrayList;

import emulator.Event;

/**
 * A class that implements the methods to register, remove and notify listeners
 * 
 * @author Nicolas
 * 
 */
public class ModelInterface {

	/**
	 * List of listeners
	 */
	private ArrayList<ListenerInterface> listeners = new ArrayList<ListenerInterface>();

	/**
	 * Register a new listener
	 * 
	 * @param listener
	 *            The new listener that needs to be added
	 */
	public void addChangeListener(ListenerInterface listener) {
		listeners.add(listener);
	}

	/**
	 * Remove a listener from the list of listeners
	 * 
	 * @param listener
	 *            The listener that has to be removed
	 */
	public void removeChangeListener(ListenerInterface listener) {
		listeners.remove(listener);
	}

	/**
	 * Receive a new state message that will be passed on to all the users
	 * 
	 * @param event
	 *            The event that happened and needs to be passed on
	 */
	protected void fireStateChanged(Event event) {
		for (ListenerInterface listener : listeners)
			listener.stateChanged(event);
	}
}