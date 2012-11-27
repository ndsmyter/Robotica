package emulator;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileFilter;

import emulator.interfaces.ViewListenerInterface;

@SuppressWarnings("serial")
public class EmulatorWindow extends JFrame implements ViewListenerInterface,
		ActionListener {

	private MapPanel mapPanel;
	private TextArea logArea;
	private Emulator emulator;
	private JButton startStopButton;
	private JComboBox<Object> mapBox;

	private boolean running = false;

	private static final String SCREENSHOTS = "screenshots";

	public EmulatorWindow(final Emulator emulator) {
		super("Emulator");
		this.emulator = emulator;

		mapPanel = new MapPanel(emulator);

		// Init log area
		logArea = new TextArea();
		logArea.setPreferredSize(new Dimension(200, 500));
		logArea.setEditable(false);
		emulator.addChangeListener(this);

		// Maps
		mapBox = new JComboBox<Object>(emulator.getBackgroundMaps().toArray());
		mapBox.setSelectedItem(emulator.getMap());
		mapBox.addActionListener(this);

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
		Action saveAction = new AbstractAction("Screenshot") {
			@Override
			public void actionPerformed(ActionEvent e) {
				BufferedImage offImage = new BufferedImage(mapPanel.getWidth(),
						mapPanel.getHeight(), BufferedImage.TYPE_INT_ARGB);

				offImage.setRGB(0, 0, Color.BLACK.getRGB());
				Graphics2D g2 = offImage.createGraphics();

				g2.setBackground(Color.BLUE);
				g2.setColor(Color.RED);
				g2.drawRect(0, 0, 50, 50);

				g2.setClip(0, 0, mapPanel.getWidth(), mapPanel.getHeight());
				mapPanel.paintComponent(g2);

				// Make screenshot folder
				if (!new File(SCREENSHOTS).exists())
					new File(SCREENSHOTS).mkdir();
				int nr = 1;
				while (new File(SCREENSHOTS + "/screenshot" + nr + ".png")
						.exists())
					nr++;
				try {
					ImageIO.write(offImage, "png", new File(SCREENSHOTS
							+ "/screenshot" + nr + ".png"));
				} catch (IOException e1) {
					e1.printStackTrace();
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
		Action stepAction = new AbstractAction("Step") {
			@Override
			public void actionPerformed(ActionEvent e) {
				new Thread(new Runnable() {
					@Override
					public void run() {
						doStep();
					}
				}).start();
			}
		};
		Action showMapAction = new AbstractAction("Show map") {
			@Override
			public void actionPerformed(ActionEvent e) {
				mapPanel.mapShowing = !mapPanel.mapShowing;
				repaint();
			}
		};
		startStopButton.setAction(startStopAction);
		startStopAction.putValue(AbstractAction.SHORT_DESCRIPTION,
				"Start/Stop execution (Space)");
		saveAction.putValue(AbstractAction.SHORT_DESCRIPTION,
				"Save image (Ctrl+S)");
		zoomInAction.putValue(AbstractAction.SHORT_DESCRIPTION,
				"Zoom IN (+, scroll up)");
		zoomOutAction.putValue(AbstractAction.SHORT_DESCRIPTION,
				"Zoom OUT (-, scroll down)");
		stepAction.putValue(AbstractAction.SHORT_DESCRIPTION,
				"Do one step of the Algorithm (Ctrl+N)");
		resetAction
				.putValue(AbstractAction.SHORT_DESCRIPTION, "Reset (Ctrl+R)");
		showMapAction.putValue(AbstractAction.SHORT_DESCRIPTION,
				"Show map or not");
		buttonPanel.add(mapBox);
		buttonPanel.add(new JButton(saveAction));
		buttonPanel.add(new JButton(zoomInAction));
		buttonPanel.add(new JButton(zoomOutAction));
		buttonPanel.add(new JButton(resetAction));
		buttonPanel.add(startStopButton);
		buttonPanel.add(new JButton(stepAction));
		JToggleButton mapShowingButton = new JToggleButton(showMapAction);
		mapShowingButton.setSelected(mapPanel.mapShowing);
		buttonPanel.add(mapShowingButton);

		JPanel panel = new JPanel(new BorderLayout());
		panel.add(mapPanel, BorderLayout.CENTER);
		panel.add(logArea, BorderLayout.EAST);
		panel.add(buttonPanel, BorderLayout.NORTH);
		panel.getInputMap().put(KeyStroke.getKeyStroke("ctrl S"), "saveAction");
		panel.getInputMap().put(KeyStroke.getKeyStroke(' '), "startAction");
		panel.getInputMap()
				.put(KeyStroke.getKeyStroke("ctrl R"), "resetAction");
		panel.getInputMap().put(KeyStroke.getKeyStroke('+'), "zoomInAction");
		panel.getInputMap().put(KeyStroke.getKeyStroke('-'), "zoomOutAction");
		panel.getInputMap().put(KeyStroke.getKeyStroke("ctrl N"), "stepAction");
		panel.getActionMap().put("saveAction", saveAction);
		panel.getActionMap().put("startAction", startStopAction);
		panel.getActionMap().put("resetAction", resetAction);
		panel.getActionMap().put("zoomInAction", zoomInAction);
		panel.getActionMap().put("zoomOutAction", zoomOutAction);
		panel.getActionMap().put("stepAction", stepAction);
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

	public void doStep() {
		emulator.doStep();
	}

	@Override
	public void viewStateChanged(Event event) {
		if (event.getType() == EventType.LOG)
			log(event.getMessage());
	}

	private void log(String message) {
		logArea.append(message + "\r\n");
	}

	@Override
	/**
	 * Called whenever the map combobox has a new value
	 */
	public void actionPerformed(ActionEvent e) {
		emulator.setMap(mapBox.getSelectedItem().toString());
	}
}
