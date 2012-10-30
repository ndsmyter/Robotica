package emulator;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;

import javax.swing.JPanel;

import emulator.interfaces.ViewListenerInterface;

@SuppressWarnings("serial")
public class MapPanel extends JPanel implements ViewListenerInterface {

	private final static int PIXEL_SIZE = 20;
	private final static int ROBOT_SIZE = 10;
	private final static int LINE_LENGTH = 10;

	private final Emulator emulator;
	private RobotState position;
	private ArrayList<RobotState> historyOfPoints = new ArrayList<RobotState>();

	public MapPanel(Emulator emulator) {
		super();
		this.emulator = emulator;
		this.setBackground(Color.WHITE);
		this.setPreferredSize(new Dimension(500, 500));

		move(0, 0, 0);

		emulator.addChangeListener(this);
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		int width = getWidth(), height = getHeight();
		Graphics2D g2 = (Graphics2D) g;

		// Move the (0,0) point to middle of screen
		g.translate(width / 2, height / 2);
		g2.scale(1, -1);

		// Draw the (0,0) point
		g.setColor(Color.BLACK);
		g.fillArc(-2, -2, 4, 4, 0, 360);

		// Draw previous points
		drawPreviousPoints(g);

		// Draw robot
		drawRobot(g2, position);

		// Draw grid
		Rectangle clip = g.getClipBounds();
		int xMax = clip.height + clip.x;
		int yMax = clip.width + clip.y;
		g.setColor(Color.GRAY);
		for (int i = clip.x; i < xMax; i++)
			if (i % PIXEL_SIZE == 0)
				g.drawLine(i, clip.y, i, yMax);
		for (int i = clip.y; i < yMax; i++)
			if (i % PIXEL_SIZE == 0)
				g.drawLine(clip.x, i, xMax, i);
	}

	private void drawPreviousPoints(Graphics g) {
		g.setColor(Color.LIGHT_GRAY);
		for (RobotState state : historyOfPoints)
			g.fillRect(state.x * PIXEL_SIZE, state.y * PIXEL_SIZE, PIXEL_SIZE,
					PIXEL_SIZE);
	}

	/**
	 * Draw robot on the panel
	 * 
	 * @param g
	 *            The Graphics used to draw the robot on
	 * @param state
	 *            The current position and direction of the robot
	 */
	private void drawRobot(Graphics g, RobotState state) {
		g.setColor(Color.BLUE);
		// Draw a dot to represent the robot
		g.fillArc((int) ((state.x + 0.25) * PIXEL_SIZE + 0.5),
				(int) ((state.y + 0.25) * PIXEL_SIZE + 0.5), ROBOT_SIZE,
				ROBOT_SIZE, 0, 360);

		// Draw a line to show the direction of the robot
		int x = (int) (state.x * PIXEL_SIZE + PIXEL_SIZE / 2 + 0.5);
		int y = (int) (state.y * PIXEL_SIZE + PIXEL_SIZE / 2 + 0.5);
		g.drawLine(
				x,
				y,
				(int) (x + LINE_LENGTH
						* Math.cos(1.0 * state.dir * Math.PI / 180) + 0.5),
				(int) (y + LINE_LENGTH
						* Math.sin(1.0 * state.dir * Math.PI / 180) + 0.5));
	}

	@Override
	public void viewStateChanged(Event event) {
		switch (event.getType()) {
		case DRIVE:
			if (position.dir <= 45 || position.dir > 315)
				move(position.x + 1, position.y, position.dir);
			else if (position.dir <= 135)
				move(position.x, position.y + 1, position.dir);
			else if (position.dir <= 225)
				move(position.x - 1, position.y, position.dir);
			else
				move(position.x, position.y - 1, position.dir);
			repaint();
			break;
		case TURN:
			repaint();
			break;
		case TURN_LEFT:
			move(position.x, position.y, (position.dir + 90 + 360) % 360);
			repaint();
			break;
		case TURN_RIGHT:
			move(position.x, position.y, (position.dir - 90 + 360) % 360);
			repaint();
			break;
		default:
			break;
		}
	}

	private void move(int x, int y, int dir) {
		RobotState point = new RobotState(x, y, dir);
		if (!historyOfPoints.contains(point))
			historyOfPoints.add(point);
		position = point;
	}

	private class RobotState {
		public final int x;
		public final int y;
		public final int dir;

		public RobotState(int x, int y, int dir) {
			this.x = x;
			this.y = y;
			this.dir = dir;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null || getClass() != obj.getClass())
				return false;
			RobotState other = (RobotState) obj;
			return x == other.x && y == other.y;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "PreviousPoint [x=" + x + ", y=" + y + ", dir=" + dir + "]";
		}
	}
}
