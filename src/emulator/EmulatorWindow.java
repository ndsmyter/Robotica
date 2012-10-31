package emulator;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.TextArea;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import emulator.interfaces.ViewListenerInterface;

@SuppressWarnings("serial")
public class EmulatorWindow extends JFrame implements ViewListenerInterface {

	private MapPanel mapPanel;
	private TextArea logArea;

	public EmulatorWindow(Emulator emulator) {
		super("Emulator");

		mapPanel = new MapPanel(emulator);

		// Init log area
		logArea = new TextArea();
		logArea.setPreferredSize(new Dimension(200, 500));
		emulator.addChangeListener(this);

		// Init button bar
		JPanel buttonPanel = new JPanel(new GridLayout(1, 0));
		JButton zoomInButton = new JButton(new AbstractAction("+") {
			@Override
			public void actionPerformed(ActionEvent e) {
				mapPanel.zoom(true);
			}
		});
		JButton zoomOutButton = new JButton(new AbstractAction("-") {
			@Override
			public void actionPerformed(ActionEvent e) {
				mapPanel.zoom(false);
			}
		});
		buttonPanel.add(zoomInButton);
		buttonPanel.add(zoomOutButton);

		JPanel panel = new JPanel(new BorderLayout());
		panel.add(mapPanel, BorderLayout.CENTER);
		panel.add(logArea, BorderLayout.EAST);
		panel.add(buttonPanel, BorderLayout.NORTH);
		this.setContentPane(panel);

		this.pack();
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
	}

	@Override
	public void viewStateChanged(Event event) {
		if (event.getType() == EventType.LOG)
			log(event.getMessage());
	}

	private void log(String message) {
		logArea.append(message + "\r\n");
	}
}
