package emulator.interfaces;

import javax.swing.event.EventListenerList;

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
	private EventListenerList listeners = new EventListenerList();

	/**
	 * Register a new listener
	 * 
	 * @param listener
	 *            The new listener that needs to be added
	 */
	public void addChangeListener(ListenerInterface listener) {
		listeners.add(ListenerInterface.class, listener);
	}

	public void addChangeListener(ViewListenerInterface listener) {
		listeners.add(ViewListenerInterface.class, listener);
	}

	/**
	 * Remove a listener from the list of listeners
	 * 
	 * @param listener
	 *            The listener that has to be removed
	 */
	public void removeChangeListener(ListenerInterface listener) {
		listeners.remove(ListenerInterface.class, listener);
	}

	public void removeChangeListener(ViewListenerInterface listener) {
		listeners.remove(ViewListenerInterface.class, listener);
	}

	/**
	 * Receive a new state message that will be passed on to all the users
	 * 
	 * @param view
	 *            Should the view be updated or not
	 * @param event
	 *            The event that happened and needs to be passed on
	 */
	protected void fireStateChanged(boolean view, Event event) {
		Object[] listenerList = listeners.getListenerList();
		for (int i = listenerList.length - 2; i >= 0; i -= 2) {
			if (!view && listenerList[i] == ListenerInterface.class)
				((ListenerInterface) listenerList[i + 1]).stateChanged(event);
			else if (view && listenerList[i] == ViewListenerInterface.class)
				((ViewListenerInterface) listenerList[i + 1])
						.viewStateChanged(event);
		}
	}
}