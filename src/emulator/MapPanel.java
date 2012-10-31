package emulator;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;

import javax.swing.JPanel;

import roomba.RoombaConfig;

import common.RobotState;
import common.Sensor;
import common.Utils;

import emulator.interfaces.ViewListenerInterface;

/**
 * The panel will draw a grid where the robot can move on
 * 
 * @author Nicolas
 * 
 */
@SuppressWarnings("serial")
public class MapPanel extends JPanel implements ViewListenerInterface {

	// Some scaling parameters
	private final static int PIXEL_SIZE = 20;
	private final static int ROBOT_SIZE = 10;
	private final static int LINE_LENGTH = 10;

	private double scale = 0.2;
	private final static double ZOOM_FACTOR = 0.05;

	// The colors which you can change to the color you like
	private final static Color BACKGROUND_COLOR = Color.WHITE;
	private final static Color ZERO_COLOR = Color.BLACK;
	private final static Color OBSTACLE_COLOR = Color.BLACK;
	private final static Color ROBOT_COLOR = Color.BLUE;
	private final static Color GRID_COLOR = Color.LIGHT_GRAY;
	private final static Color PATH_COLOR = Color.GRAY;
	private final static Color SENSOR_COLOR = Color.PINK;
	private final static Color TEXT_COLOR = Color.BLACK;

	// Points to draw on the screen (current & previous states, obstacles..)
	private RobotState position = null;
	private ArrayList<RobotState> historyOfPoints = new ArrayList<RobotState>();
	private ArrayList<Point> obstacles = new ArrayList<Point>();

	private final Emulator emulator;
	private Point windowPosition;

	public MapPanel(Emulator emulator) {
		super();
		this.emulator = emulator;
		int w = 500, h = 500;
		this.setBackground(BACKGROUND_COLOR);
		this.setPreferredSize(new Dimension(w, h));

		move(0, 0, 0);

		emulator.addChangeListener(this);

		windowPosition = new Point(w / 2, h / 2);

		PanMouseListener l = new PanMouseListener();
		this.addMouseMotionListener(l);
		this.addMouseWheelListener(l);
		this.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;

		// Move the (0,0) point to middle of screen
		g.translate(windowPosition.x, windowPosition.y);
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

		// Draw the scale
		drawScale(g2);
	}

	private void drawScale(Graphics2D g) {
		g.setColor(TEXT_COLOR);
		g.scale(1, -1);
		g.drawString("1 kotje = " + ((int) (scale * 50)) + " cm",
				-windowPosition.x + 5, (getHeight() - windowPosition.y) - 10);
	}

	/**
	 * Scale the value and round to the nearest integer
	 * 
	 * @param value
	 *            The value to scale
	 * @return The scaled value
	 */
	private int scale(double value) {
		return (int) (scale2(value) + 0.5);
	}

	/**
	 * Scale the value
	 * 
	 * @param value
	 *            The value to scale
	 * @return The scaled value
	 */
	private double scale2(double value) {
		return scale * value;
	}

	/**
	 * Draw a grid on the screen
	 * 
	 * @param g
	 *            The Graphics to be used for painting
	 */
	private void drawGrid(Graphics g) {
		Rectangle clip = g.getClipBounds();
		int xMax = clip.width + clip.x;
		int yMax = clip.height + clip.y;
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
		for (RobotState state : historyOfPoints) {
			g.drawRect(scale(state.x), scale(state.y), 1, 1);
		}
		for (int i = 0; i < historyOfPoints.size() - 1; i++) {
			RobotState first = historyOfPoints.get(i);
			RobotState last = historyOfPoints.get(i + 1);
			g.drawLine(scale(first.x), scale(first.y), scale(last.x),
					scale(last.y));
		}
	}

	/**
	 * Draw robot on the panel
	 * 
	 * @param g
	 *            The Graphics used to draw the robot on
	 */
	private void drawRobot(Graphics g) {
		// Draw the sensors of the robot
		g.setColor(SENSOR_COLOR);
		for (int i = 0; i < RoombaConfig.SENSORS.length; i++) {
			Sensor sensor = RoombaConfig.SENSORS[i];
			Point pos = Utils.sensorDataToPoint(position, 0, sensor);
			g.fillRect(scale(pos.x - 2), scale(pos.y - 2), 4, 4);
		}

		// Draw a dot to represent the robot
		g.setColor(ROBOT_COLOR);
		g.fillArc((int) (scale2(position.x) + 0.5 - ROBOT_SIZE / 2),
				(int) (scale2(position.y) + 0.5 - ROBOT_SIZE / 2), ROBOT_SIZE,
				ROBOT_SIZE, 0, 360);

		// Draw a line to show the direction of the robot
		int x = scale(position.x);
		int y = scale(position.y);
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
		try {
			g.setColor(OBSTACLE_COLOR);
			for (Point p : obstacles)
				g.drawRect(scale(p.x), scale(p.y), 1, 1);
		} catch (ConcurrentModificationException e) {
		}
	}

	@Override
	public void viewStateChanged(Event event) {
		switch (event.getType()) {
		case DRIVE:
			double theta = Math.PI * position.dir / 180;
			int newx = (int) (position.x + event.getDistance()
					* Math.cos(theta) + 0.5);
			int newy = (int) (position.y + event.getDistance()
					* Math.sin(theta) + 0.5);
			move(newx, newy, position.dir);
			break;
		case TURN:
			int angle = (event.isTurnRight() ? -event.getDegrees() : event
					.getDegrees());
			move(position.x, position.y, (position.dir + angle + 360) % 360);
			break;
		case OBSTACLE:
			addObstacle(event.getObstacle());
		default:
			break;
		}
	}

	private void move(int x, int y, int dir) {
		position = new RobotState(x, y, dir);
		historyOfPoints.add(position);
		repaint();
	}

	private void addObstacle(ArrayList<Point> obstacle) {
		obstacles.addAll(obstacle);
		repaint();
	}

	public void zoom(boolean zoomIn) {
		scale = zoomIn ? scale + ZOOM_FACTOR : Math.max(scale - ZOOM_FACTOR,
				ZOOM_FACTOR);
		repaint();
	}

	public void movePanel(Point newPosition) {
		if (!newPosition.equals(windowPosition)) {
			windowPosition = newPosition;
			repaint();
		}
	}

	private class PanMouseListener implements MouseMotionListener,
			MouseWheelListener {

		private Point previousPosition;

		@Override
		public void mouseDragged(MouseEvent e) {
			e.translatePoint(-windowPosition.x, -windowPosition.y);
			movePanel(new Point(windowPosition.x + e.getX()
					- previousPosition.x, windowPosition.y + e.getY()
					- previousPosition.y));
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			e.translatePoint(-windowPosition.x, -windowPosition.y);
			previousPosition = new Point(e.getX(), e.getY());
		}

		@Override
		public void mouseWheelMoved(MouseWheelEvent e) {
			zoom(e.getWheelRotation() < 0);
		}
	}
}
