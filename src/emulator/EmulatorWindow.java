package emulator;

import javax.swing.JFrame;

import emulator.interfaces.Event;
import emulator.interfaces.ListenerInterface;

@SuppressWarnings("serial")
public class EmulatorWindow extends JFrame implements ListenerInterface {

	public EmulatorWindow() {
		super("Emulator");
		
		this.setSize(600, 500);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
	}

	@Override
	public void stateChanged(Event event) {
		// TODO Auto-generated method stub

	}

}
