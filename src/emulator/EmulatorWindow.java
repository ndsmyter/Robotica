package emulator;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
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
	private ArrayList<Point> backgroundMap;

	public EmulatorWindow(final Emulator emulator) {
		super("Emulator");

		backgroundMap = new ArrayList<Point>();
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
					File file = chooser.getSelectedFile();
					try {
						emulator.log("Approved");
						BufferedImage img = ImageIO.read(file);
						emulator.log("Read");
						PixelGrabber grabber = new PixelGrabber(img, 0, 0, -1,
								-1, true);
						grabber.grabPixels();
						emulator.log("Grabbed");
						int[] pixels = (int[]) grabber.getPixels();
						int w = img.getWidth(), h = img.getHeight();
						emulator.log("Pixels=" + pixels.length + ", w=" + w
								+ ", h=" + h);
						ArrayList<Integer> diffs = new ArrayList<Integer>();
						for (int i = 0; i < w; i++) {
							for (int j = 0; j < h; j++) {
								int k = w * i + j;
								if (!diffs.contains(pixels[k]))
									diffs.add(pixels[k]);
							}
						}
						emulator.log(diffs.size() + " different colors");
						for (int i = 0; i < diffs.size(); i++) {
							emulator.log("color " + i + ": " + diffs.get(i));
						}
						emulator.log("GET");
						if (pixels != null) {
							emulator.log("Length: " + pixels.length);
						} else
							emulator.log("NULL");
					} catch (IOException e) {
						e.printStackTrace();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		});
		buttonPanel.add(openButton);
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
