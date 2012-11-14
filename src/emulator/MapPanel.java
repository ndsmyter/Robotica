package emulator;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.swing.JPanel;

import roomba.RoombaConfig;
import brains.Brains;
import brains.interfaces.ObstacleListener;

import common.Config;
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
public class MapPanel extends JPanel implements ViewListenerInterface,
		ObstacleListener {

	// Some scaling parameters
	private final static int CELLS_IN_GRID = 10;
	private final static int ROBOT_SIZE = RoombaConfig.ROOMBA_DIAMETER;
	private final static int LINE_LENGTH = 100;
	private final static int ARROW_MOVEMENT = 5;

	// Scaling parameters
	private final static double ZOOM_FACTOR = 0.05;
	private final static double ORIGINAL_ZOOM = 0.2;
	private double scale = ORIGINAL_ZOOM;

	// The colors which you can change to the color you like
	private final static Color BACKGROUND_COLOR = Color.GRAY;
	private final static Color ZERO_COLOR = Color.BLACK;
	private final static Color ROBOT_COLOR = new Color(210, 250, 255);
	private final static Color GRID_COLOR = Color.DARK_GRAY;
	private final static Color PATH_COLOR = Color.ORANGE;
	private final static Color SENSOR_COLOR = new Color(5, 80, 90);
	private final static Color TEXT_COLOR = Color.BLACK;
	private final static Color MAP_COLOR = Color.YELLOW;

	// Points to draw on the screen (current & previous states, obstacles..)
	private RobotState position = null;
	private ArrayList<RobotState> historyOfPoints = new ArrayList<RobotState>();
	private final Emulator emulator;
	private Point winPos;
	private Brains brains;

	public boolean mapShowing = false;

	public MapPanel(Emulator emulator) {
		super();
		this.emulator = emulator;
		this.brains = emulator.getBrains();
		int w = 500, h = 500;
		this.setBackground(BACKGROUND_COLOR);
		this.setPreferredSize(new Dimension(w, h));

		move(brains.getCurrentState());

		emulator.addChangeListener(this);

		winPos = new Point(w / 2, h / 2);

		PanelListener l = new PanelListener();
		this.addMouseMotionListener(l);
		this.addMouseWheelListener(l);
		this.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));

		KeyEventDispatcher myKeyEventDispatcher = new KeyEventDispatcher() {

			@Override
			public boolean dispatchKeyEvent(KeyEvent e) {
				boolean found = true;
				switch (e.getKeyCode()) {
				case KeyEvent.VK_LEFT:
					movePanel(new Point(winPos.x + ARROW_MOVEMENT, winPos.y));
					break;
				case KeyEvent.VK_RIGHT:
					movePanel(new Point(winPos.x - ARROW_MOVEMENT, winPos.y));
					break;
				case KeyEvent.VK_UP:
					movePanel(new Point(winPos.x, winPos.y + ARROW_MOVEMENT));
					break;
				case KeyEvent.VK_DOWN:
					movePanel(new Point(winPos.x, winPos.y - ARROW_MOVEMENT));
					break;
				default:
					found = false;
					break;
				}
				return found;
			}
		};
		KeyboardFocusManager.getCurrentKeyboardFocusManager()
				.addKeyEventDispatcher(myKeyEventDispatcher);

		brains.addObstacleListener(this);
	}

	/**
	 * Reset the view
	 */
	public void reset() {
		scale = ORIGINAL_ZOOM;
		historyOfPoints.clear();
		winPos = new Point(getWidth() / 2, getHeight() / 2);
		move(brains.getCurrentState());
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;

		// Move the (0,0) point to middle of screen
		g.translate(winPos.x, winPos.y);
		g2.scale(1, -1);

		// Draw the (0,0) point
		g.setColor(ZERO_COLOR);
		g.fillArc(-2, -2, 4, 4, 0, 360);

		// Draw map
		if (mapShowing)
			drawMap(g);

		// Draw the obstacles
		drawObstacles(g);

		// Draw grid
		drawGrid(g);

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
		g.drawString("1 kotje = "
				+ ((int) (CELLS_IN_GRID * Config.GRID_SIZE) / 10) + " cm",
				-winPos.x + 5, (getHeight() - winPos.y) - 10);

	}

	private void drawMap(Graphics g) {
		try {
			g.setColor(MAP_COLOR);
			for (Point p : emulator.getBackground()) {
				g.drawRect(scale(p.x), scale(p.y), scale(Config.GRID_SIZE),
						scale(Config.GRID_SIZE));
			}
		} catch (Exception e) {
		}
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

	private int descale(double value) {
		return (int) (descale2(value) + 0.5);
	}

	private double descale2(double value) {
		return 1.0 * value / scale;
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
		for (int i = clip.x; i < xMax; i++) {
			if (i % scale(CELLS_IN_GRID * Config.GRID_SIZE) == 0) {
				g.drawLine(i, clip.y, i, yMax);
			}
		}
		for (int i = clip.y; i < yMax; i++) {
			if (i % scale(CELLS_IN_GRID * Config.GRID_SIZE) == 0) {
				g.drawLine(clip.x, i, xMax, i);
			}
		}
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

		// Draw a dot to represent the robot
		g.setColor(ROBOT_COLOR);
		g.fillArc((int) (scale2(position.x) + 0.5 - scale(ROBOT_SIZE) / 2),
				(int) (scale2(position.y) + 0.5 - scale(ROBOT_SIZE) / 2),
				scale(ROBOT_SIZE), scale(ROBOT_SIZE), 0, 360);

		// Draw the sensors of the robot
		g.setColor(SENSOR_COLOR);
		for (int i = 0; i < RoombaConfig.SENSORS.length; i++) {
			Sensor sensor = RoombaConfig.SENSORS[i];
			Point pos = Utils.sensorDataToPoint(position, 0, sensor);
			g.fillRect(scale(pos.x - 2), scale(pos.y - 2), 4, 4);
		}
		// Draw a line to show the direction of the robot
		RobotState endpoint = Utils.driveForward(position, LINE_LENGTH);
		int x = scale(position.x);
		int y = scale(position.y);
		int endX = scale(endpoint.x);
		int endY = scale(endpoint.y);
		g.drawLine(x, y, endX, endY);
	}

	/**
	 * Draw obstacles on the panel
	 * 
	 * @param g
	 *            The Graphics used to draw the robot on
	 */
	private void drawObstacles(Graphics g) {
		try {
			HashMap<Point, Double> points = brains.getMap().getAll();
			for (Entry<Point, Double> entry : points.entrySet()) {
				float c = (float) (double) (1 - entry.getValue());
				g.setColor(new Color(c, c, c));
				g.fillRect(scale(entry.getKey().x), scale(entry.getKey().y),
						scale(Config.GRID_SIZE), scale(Config.GRID_SIZE));// 1,1);
			}
		} catch (ConcurrentModificationException e) {
		}
	}

	@Override
	public void viewStateChanged(Event event) {
		switch (event.getType()) {
		case DRIVE:
			move(brains.getCurrentState());
			break;
		case TURN:
			move(brains.getCurrentState());
			break;
		default:
			break;
		}
	}

	private void move(RobotState s) {
		position = s;
		historyOfPoints.add(position);
		repaint();
	}

	/**
	 * Zoom in or out
	 * 
	 * @param zoomIn
	 *            If true zoom in, otherwise zoom out
	 */
	public void zoom(boolean zoomIn) {

		Point middle = new Point(getWidth() / 2, getHeight() / 2);
		int xDist = descale(middle.x - winPos.x);
		int yDist = descale(middle.y - winPos.y);
		scale = zoomIn ? scale + ZOOM_FACTOR : Math.max(scale - ZOOM_FACTOR,
				ZOOM_FACTOR);
		xDist = scale(xDist);
		yDist = scale(yDist);
		winPos = new Point(middle.x - xDist, middle.y - yDist);
		repaint();
	}

	public void movePanel(Point newPosition) {
		if (!newPosition.equals(winPos)) {
			winPos = newPosition;
			repaint();
		}
	}

	private class PanelListener implements MouseMotionListener,
			MouseWheelListener {

		private Point previousPosition;

		@Override
		public void mouseDragged(MouseEvent e) {
			e.translatePoint(-winPos.x, -winPos.y);
			movePanel(new Point(winPos.x + e.getX() - previousPosition.x,
					winPos.y + e.getY() - previousPosition.y));
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			e.translatePoint(-winPos.x, -winPos.y);
			previousPosition = new Point(e.getX(), e.getY());
		}

		@Override
		public void mouseWheelMoved(MouseWheelEvent e) {
			zoom(e.getWheelRotation() < 0);
		}
	}

	@Override
	public void obstacleAdded(Point point, double value) {
		repaint();
	}
}
