package emulator;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import javax.swing.JPanel;

import emulator.interfaces.ListenerInterface;

@SuppressWarnings("serial")
public class MapPanel extends JPanel implements ListenerInterface {

	private final static int PIXEL_SIZE = 20;
	private final static int ROBOT_SIZE = 10;

	private int xPosition = 0;
	private int yPosition = 0;
	private int direction = 0;

	public MapPanel(Emulator emulator) {
		super();
		this.setBackground(Color.WHITE);
		this.setPreferredSize(new Dimension(500, 500));

		emulator.addChangeListener(this);
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		int width = getWidth(), height = getHeight();
		Graphics2D g2 = (Graphics2D) g;

		g.translate(width / 2, height / 2);
		g2.scale(1, -1);

		Rectangle clip = g.getClipBounds();
		int xMax = clip.height + clip.x;
		int yMax = clip.width + clip.y;

		g.setColor(Color.BLACK);
		g.fillArc(-2, -2, 4, 4, 0, 360);
		drawRobot(g2, xPosition, yPosition, direction);

		// Draw grid
		g.setColor(Color.GRAY);
		for (int i = clip.x; i < xMax; i++)
			if (i % PIXEL_SIZE == 0)
				g.drawLine(i, clip.y, i, yMax);
		for (int i = clip.y; i < yMax; i++)
			if (i % PIXEL_SIZE == 0)
				g.drawLine(clip.x, i, xMax, i);
	}

	/**
	 * Draw robot on the panel
	 * @param g The Graphics used to draw the robot on
	 * @param x The x position of the robot
	 * @param y The y position of the robot
	 * @param direction The direction of the robot
	 */
	private void drawRobot(Graphics g, double x, double y, int direction) {
		g.setColor(Color.BLUE);
		g.fillArc((int) ((x + 0.25) * PIXEL_SIZE + 0.5), (int) ((y + 0.25)
				* PIXEL_SIZE + 0.5), ROBOT_SIZE, ROBOT_SIZE, 0, 360);
	}

	@Override
	public void stateChanged(Event event) {
		repaint();
	}
}
