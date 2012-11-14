package emulator;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileFilter;

import emulator.interfaces.ViewListenerInterface;

@SuppressWarnings("serial")
public class EmulatorWindow extends JFrame implements ViewListenerInterface {

	private MapPanel mapPanel;
	private TextArea logArea;
	private Emulator emulator;
	private JButton startStopButton;

	private boolean running = false;

	public EmulatorWindow(final Emulator emulator) {
		super("Emulator");
		this.emulator = emulator;

		mapPanel = new MapPanel(emulator);

		// Init log area
		logArea = new TextArea();
		logArea.setPreferredSize(new Dimension(200, 500));
		emulator.addChangeListener(this);

		// Init button bar
		JPanel buttonPanel = new JPanel(new GridLayout(1, 0));
		startStopButton = new JButton();
		Action zoomInAction = new AbstractAction("+") {
			@Override
			public void actionPerformed(ActionEvent e) {
				mapPanel.zoom(true);
			}
		};
		Action zoomOutAction = new AbstractAction("-") {
			@Override
			public void actionPerformed(ActionEvent e) {
				mapPanel.zoom(false);
			}
		};
		Action openAction = new AbstractAction("Open") {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser chooser = new JFileChooser();
				FileFilter filter = new FileFilter() {
					@Override
					public String getDescription() {
						return null;
					}

					@Override
					public boolean accept(File f) {
						return f.getName().endsWith(".bmp");
					}
				};
				chooser.setMultiSelectionEnabled(false);
				chooser.setFileFilter(filter);
				int returnValue = chooser.showOpenDialog(EmulatorWindow.this);
				if (returnValue == JFileChooser.APPROVE_OPTION) {
					emulator.loadBackgroundMap(chooser.getSelectedFile());
					notifyReset();
				}
			}
		};
		Action resetAction = new AbstractAction("Reset") {
			@Override
			public void actionPerformed(ActionEvent e) {
				notifyReset();
			}
		};
		Action startStopAction = new AbstractAction("Start") {
			@Override
			public void actionPerformed(ActionEvent e) {
				new Thread(new Runnable() {
					@Override
					public void run() {
						startStop();
					}
				}).start();
			}
		};
		startStopButton.setAction(startStopAction);
		startStopAction.putValue(AbstractAction.SHORT_DESCRIPTION,
				"Start/Stop execution (Space)");
		openAction.putValue(AbstractAction.SHORT_DESCRIPTION,
				"Open image (Ctrl+O)");
		zoomInAction.putValue(AbstractAction.SHORT_DESCRIPTION,
				"Zoom IN (+, scroll up)");
		zoomOutAction.putValue(AbstractAction.SHORT_DESCRIPTION,
				"Zoom OUT (-, scroll down)");
		resetAction
				.putValue(AbstractAction.SHORT_DESCRIPTION, "Reset (Ctrl+R)");
		buttonPanel.add(new JButton(openAction));
		buttonPanel.add(new JButton(zoomInAction));
		buttonPanel.add(new JButton(zoomOutAction));
		buttonPanel.add(new JButton(resetAction));
		buttonPanel.add(startStopButton);

		JPanel panel = new JPanel(new BorderLayout());
		panel.add(mapPanel, BorderLayout.CENTER);
		panel.add(logArea, BorderLayout.EAST);
		panel.add(buttonPanel, BorderLayout.NORTH);
		panel.getInputMap().put(KeyStroke.getKeyStroke("ctrl O"), "openAction");
		panel.getInputMap().put(KeyStroke.getKeyStroke(' '), "startAction");
		panel.getInputMap()
				.put(KeyStroke.getKeyStroke("ctrl R"), "resetAction");
		panel.getInputMap().put(KeyStroke.getKeyStroke('+'), "zoomInAction");
		panel.getInputMap().put(KeyStroke.getKeyStroke('-'), "zoomOutAction");
		panel.getActionMap().put("openAction", openAction);
		panel.getActionMap().put("startAction", startStopAction);
		panel.getActionMap().put("resetAction", resetAction);
		panel.getActionMap().put("zoomInAction", zoomInAction);
		panel.getActionMap().put("zoomOutAction", zoomOutAction);
		this.setContentPane(panel);

		this.pack();
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
	}

	public void notifyReset() {
		if (running)
			startStop();
		emulator.reset();
		mapPanel.reset();
	}

	public void startStop() {
		if (running) {
			startStopButton.setText("Start");
			running = false;
			emulator.stop();
		} else {
			startStopButton.setText("Stop");
			running = true;
			emulator.restart();
		}
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
