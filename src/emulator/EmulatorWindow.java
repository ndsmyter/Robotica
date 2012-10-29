package emulator;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.TextArea;

import javax.swing.JFrame;
import javax.swing.JPanel;

import emulator.interfaces.ListenerInterface;

@SuppressWarnings("serial")
public class EmulatorWindow extends JFrame implements ListenerInterface {

	private MapPanel mapPanel;
	private TextArea logArea;

	public EmulatorWindow(Emulator emulator) {
		super("Emulator");

		mapPanel = new MapPanel(emulator);

		// Init log area
		logArea = new TextArea();
		logArea.setPreferredSize(new Dimension(200, 500));
		emulator.addChangeListener(this);

		JPanel panel = new JPanel(new BorderLayout());
		panel.add(mapPanel, BorderLayout.CENTER);
		panel.add(logArea, BorderLayout.EAST);
		this.setContentPane(panel);

		this.pack();
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
	}

	@Override
	public void stateChanged(Event event) {
		if (event.getEventType() == EventType.LOG)
			logArea.append((String) event.getData() + "\r\n");
	}

}
