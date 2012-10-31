package emulator;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;

import javax.swing.JPanel;

import emulator.interfaces.ViewListenerInterface;
import java.awt.Point;

/**
 * The panel will draw a grid where the robot can move on
 * 
 * @author Nicolas
 * 
 */
@SuppressWarnings("serial")
public class MapPanel extends JPanel implements ViewListenerInterface {

	// TODO Solve following issues
	// - The grid isn't redrawn correctly if the size of the panel changes
	// - Make a method to draw walls, objects etcetera

	private final static int PIXEL_SIZE = 20;
	private final static int ROBOT_SIZE = 10;
	private final static int LINE_LENGTH = 10;

	private final static Color BACKGROUND_COLOR = Color.WHITE;
	private final static Color ZERO_COLOR = Color.BLACK;
	private final static Color OBSTACLE_COLOR = Color.BLACK;
	private final static Color ROBOT_COLOR = Color.BLUE;
	private final static Color GRID_COLOR = Color.LIGHT_GRAY;
	private final static Color PATH_COLOR = Color.GRAY;

	private RobotState position = null;
	private ArrayList<RobotState> historyOfPoints = new ArrayList<RobotState>();
	private ArrayList<Point> obstacles = new ArrayList<Point>();

	public MapPanel(Emulator emulator) {
		super();
		this.setBackground(BACKGROUND_COLOR);
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

		// Draw grid
		drawGrid(g);

		// Draw the (0,0) point
		g.setColor(ZERO_COLOR);
		g.fillArc(-2, -2, 4, 4, 0, 360);

		// Draw the obstacles
		drawObstacles(g);

		// Draw previous points
		drawPreviousPoints(g);

		// Draw robot
		drawRobot(g2);
	}

	/**
	 * Draw a grid on the screen
	 * 
	 * @param g
	 *            The Graphics to be used for painting
	 */
	private void drawGrid(Graphics g) {
		Rectangle clip = g.getClipBounds();
		int xMax = clip.height + clip.x;
		int yMax = clip.width + clip.y;
		g.setColor(GRID_COLOR);
		for (int i = clip.x; i < xMax; i++)
			if (i % PIXEL_SIZE == 0)
				g.drawLine(i, clip.y, i, yMax);
		for (int i = clip.y; i < yMax; i++)
			if (i % PIXEL_SIZE == 0)
				g.drawLine(clip.x, i, xMax, i);
	}

	/**
	 * Draw a path of previous positions
	 * 
	 * @param g
	 *            The Graphics to be used for painting
	 */
	private void drawPreviousPoints(Graphics g) {
		g.setColor(PATH_COLOR);
		for (RobotState state : historyOfPoints)
			g.drawRect(state.x, state.y, 1, 1);
		for (int i = 0; i < historyOfPoints.size() - 1; i++) {
			g.drawLine(historyOfPoints.get(i).x, historyOfPoints.get(i).y,
					historyOfPoints.get(i + 1).x, historyOfPoints.get(i + 1).y);
		}
	}

	/**
	 * Draw robot on the panel
	 * 
	 * @param g
	 *            The Graphics used to draw the robot on
	 */
	private void drawRobot(Graphics g) {
		g.setColor(ROBOT_COLOR);
		// Draw a dot to represent the robot
		g.fillArc((int) (position.x + 0.5 - ROBOT_SIZE / 2),
				(int) (position.y + 0.5 - ROBOT_SIZE / 2), ROBOT_SIZE,
				ROBOT_SIZE, 0, 360);

		// Draw a line to show the direction of the robot
		int x = (int) (position.x + 0.5);
		int y = (int) (position.y + 0.5);
		double theta = Math.PI * position.dir / 180;
		g.drawLine(x, y, (int) (x + LINE_LENGTH * Math.cos(theta) + 0.5),
				(int) (y + LINE_LENGTH * Math.sin(theta) + 0.5));
	}

	/**
	 * Draw obstacles on the panel
	 * 
	 * @param g
	 *            The Graphics used to draw the robot on
	 */
	private void drawObstacles(Graphics g) {
		g.setColor(OBSTACLE_COLOR);
		for (Point p : obstacles) {
			g.drawRect(p.x, p.y, 1, 1);
		}
	}

	@Override
	public void viewStateChanged(Event event) {
		switch (event.getType()) {
		case DRIVE:
			double theta = Math.PI * position.dir / 180;
			int newx = (int) (position.x + event.getDistance() / 10
					* Math.cos(theta) + 0.5);
			int newy = (int) (position.y + event.getDistance() / 10
					* Math.sin(theta) + 0.5);
			move(newx, newy, position.dir);
			break;
		case TURN:
			int angle = (event.isTurnRight() ? -event.getDegrees() : event
					.getDegrees());
			move(position.x, position.y, (position.dir + angle + 360) % 360);
			break;
		case TURN_LEFT:
			move(position.x, position.y, (position.dir + 90 + 360) % 360);
			break;
		case TURN_RIGHT:
			move(position.x, position.y, (position.dir - 90 + 360) % 360);
			break;
		case OBSTACLE:
			addObstacle(event.getObstacle());
		default:
			break;
		}
	}

	private void move(int x, int y, int dir) {
		RobotState point = new RobotState(x, y, dir);
		historyOfPoints.add(point);
		position = point;
		repaint();
	}

	private void addObstacle(ArrayList<Point> obstacle) {
		obstacles.addAll(obstacle);
		repaint();
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
