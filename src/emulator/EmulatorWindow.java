package emulator;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;

import emulator.interfaces.ViewListenerInterface;

@SuppressWarnings("serial")
public class EmulatorWindow extends JFrame implements ViewListenerInterface {

	private MapPanel mapPanel;
	private TextArea logArea;
	private Emulator emulator;

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
		JButton openButton = new JButton(new AbstractAction("Open") {
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
		});
		JButton resetButton = new JButton(new AbstractAction("Reset") {
			@Override
			public void actionPerformed(ActionEvent e) {
				notifyReset();
			}
		});
		JButton startButton = new JButton(new AbstractAction("Start") {
			@Override
			public void actionPerformed(ActionEvent e) {
				new Thread(new Runnable() {
					@Override
					public void run() {
						emulator.restart();
					}
				}).start();
			}
		});
		buttonPanel.add(openButton);
		buttonPanel.add(zoomInButton);
		buttonPanel.add(zoomOutButton);
		buttonPanel.add(resetButton);
		buttonPanel.add(startButton);

		JPanel panel = new JPanel(new BorderLayout());
		panel.add(mapPanel, BorderLayout.CENTER);
		panel.add(logArea, BorderLayout.EAST);
		panel.add(buttonPanel, BorderLayout.NORTH);
		this.setContentPane(panel);

		this.pack();
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
	}

	public void notifyReset() {
		emulator.reset();
		mapPanel.reset();
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
