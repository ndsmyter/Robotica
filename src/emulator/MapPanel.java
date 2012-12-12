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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.JPanel;
import javax.swing.Timer;

import roomba.RoombaConfig;
import brains.Brains;

import common.Config;
import common.RobotState;
import common.Sensor;
import common.Utils;

/**
 * The panel will draw a grid where the robot can move on
 */
@SuppressWarnings("serial")
public class MapPanel extends JPanel {

	// Some scaling parameters
	private final static int GRID_LEGEND = (Emulator.CELLS_IN_GRID * Config.GRID_CELL_SIZE) / 10;

	// Scaling parameters
	private double scale = Emulator.ORIGINAL_ZOOM;

	private final static int REFRESH_TIME = 200;

	// Points to draw on the screen (current & previous states, obstacles..)
	private final Emulator emulator;
	private Point winPos;
	private Brains brains;

	public MapPanel(Emulator emulator) {
		super();
		this.emulator = emulator;
		this.brains = emulator.getBrains();
		int w = 500, h = 500;
		this.setBackground(Emulator.BACKGROUND_COLOR);
		this.setPreferredSize(new Dimension(w, h));

		winPos = new Point(w / 2, h / 2);

		PanelListener l = new PanelListener();
		this.addMouseMotionListener(l);
		this.addMouseWheelListener(l);
		this.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));

		KeyEventDispatcher myKeyEventDispatcher = new KeyEventDispatcher() {
			@Override
			public boolean dispatchKeyEvent(KeyEvent e) {
				boolean found = false;
				if (getParent().hasFocus()) {
					found = true;
					switch (e.getKeyCode()) {
					case KeyEvent.VK_LEFT:
						movePanel(new Point(winPos.x + Emulator.ARROW_MOVEMENT,
								winPos.y));
						break;
					case KeyEvent.VK_RIGHT:
						movePanel(new Point(winPos.x - Emulator.ARROW_MOVEMENT,
								winPos.y));
						break;
					case KeyEvent.VK_UP:
						movePanel(new Point(winPos.x, winPos.y
								+ Emulator.ARROW_MOVEMENT));
						break;
					case KeyEvent.VK_DOWN:
						movePanel(new Point(winPos.x, winPos.y
								- Emulator.ARROW_MOVEMENT));
						break;
					default:
						found = false;
						break;
					}
				}
				return found;
			}
		};
		KeyboardFocusManager.getCurrentKeyboardFocusManager()
				.addKeyEventDispatcher(myKeyEventDispatcher);

		new Timer(REFRESH_TIME, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				repaint();
			}
		}).start();
	}

	/**
	 * Reset the view
	 */
	public void reset() {
		scale = Emulator.ORIGINAL_ZOOM;
		winPos = new Point(getWidth() / 2, getHeight() / 2);
	}

	/**
	 * Paint all the elements on the screen. In this method it is decided which
	 * element is painted over another
	 */
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;

		// Move the (0,0) point to middle of screen
		g.translate(winPos.x, winPos.y);
		g2.scale(1, -1);

		// Draw the (0,0) point
		g.setColor(Emulator.ZERO_COLOR);
		g.fillArc(-2, -2, 4, 4, 0, 360);

		// Draw map
		if (emulator.isMapShowing())
			drawMap(g);

		// Draw the obstacles
		drawObstacles(g);

		// Draw grid
		drawGrid(g);

		// Draw previous points
		drawPreviousPoints(g);

		// Draw robot
		if (emulator.isRoombaShowing())
			drawRobot(g2);

		// Draw current state
		if (emulator.isCurrentStateShowing())
			drawCurrentState(g2);

		// Draw the scale
		drawScale(g2);
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
		g.setColor(Emulator.GRID_COLOR);
		int line = scale(Emulator.CELLS_IN_GRID * Config.GRID_CELL_SIZE);

		int firstLine = (int) Math.ceil(1.0 * clip.x / line) * line;
		for (int i = firstLine; i < xMax; i += line)
			g.drawLine(i, clip.y, i, yMax);

		firstLine = (int) Math.ceil(1.0 * clip.y / line) * line;
		for (int i = firstLine; i < yMax; i += line)
			g.drawLine(clip.x, i, xMax, i);
	}

	private void drawScale(Graphics2D g) {
		g.setColor(Emulator.TEXT_COLOR);
		g.scale(1, -1);
		g.drawString("1 kotje = " + GRID_LEGEND + " cm", -winPos.x + 5,
				(getHeight() - winPos.y) - 10);

	}

	private void drawMap(Graphics g) {
		try {
			g.setColor(Emulator.MAP_COLOR);
			int gridSize = scale(Config.GRID_CELL_SIZE);
			int halfGridSize = (int) (0.5 * Config.GRID_CELL_SIZE);
			for (Point p : emulator.getBackground()) {
				g.fillRect(scale(p.x - halfGridSize),
						scale(p.y - halfGridSize), gridSize, gridSize);
			}
		} catch (Exception e) {
		}
	}

	private int scale(double value) {
		return (int) (scale2(value) + 0.5);
	}

	private double scale2(double value) {
		return scale * value;
	}

	private int descale(double value) {
		return (int) (descale2(value) + 0.5);
	}

	private double descale2(double value) {
		return value / scale;
	}

	/**
	 * Draw a path of previous positions
	 * 
	 * @param g
	 *            The Graphics to be used for painting
	 */
	private void drawPreviousPoints(Graphics g) {
		try {
			g.setColor(Emulator.PATH_COLOR);
			ArrayList<Point> historyOfPoints = brains.getBestParticleMap()
					.getPath();
			for (int i = 0; i < historyOfPoints.size() - 1; i++) {
				Point first = historyOfPoints.get(i);
				g.drawRect(scale(first.x), scale(first.y), 1, 1);

				Point last = historyOfPoints.get(i + 1);
				g.drawLine(scale(first.x), scale(first.y), scale(last.x),
						scale(last.y));
			}
			if (!historyOfPoints.isEmpty()) {
				Point last = historyOfPoints.get(historyOfPoints.size() - 1);
				g.drawRect(scale(last.x), scale(last.y), 1, 1);
			}
		} catch (ConcurrentModificationException e) {
		}
	}

	private void drawCurrentState(Graphics g) {
		g.setColor(Emulator.CURRENT_STATE_COLOR);

		RobotState currentState = emulator.getSimulatedRobotState();
		int x = scale(currentState.x);
		int y = scale(currentState.y);
		g.fillRect(x - 5, y - 5, 10, 10);

		// Draw a line to show the direction of the robot
		RobotState endpoint = Utils.driveForward(currentState,
				Emulator.LINE_LENGTH);
		int endX = scale(endpoint.x);
		int endY = scale(endpoint.y);
		g.drawLine(x, y, endX, endY);
	}

	/**
	 * Draw robot on the panel
	 * 
	 * @param g
	 *            The Graphics used to draw the robot on
	 */
	private void drawRobot(Graphics g) {
		RobotState position = brains.getBestParticleMap().getPosition();
		// Draw a dot to represent the robot
		g.setColor(Emulator.ROBOT_COLOR);
		double scaledRobotSize = 0.5 * scale(Emulator.ROBOT_SIZE);
		double scaledX = scale2(position.x) + 0.5;
		double scaledY = scale2(position.y) + 0.5;
		g.fillArc((int) (scaledX - scaledRobotSize),
				(int) (scaledY - scaledRobotSize), scale(Emulator.ROBOT_SIZE),
				scale(Emulator.ROBOT_SIZE), 0, 360);

		// Draw the sensors of the robot
		g.setColor(Emulator.SENSOR_COLOR);
		for (int i = 0; i < RoombaConfig.SENSORS.length; i++) {
			Sensor sensor = RoombaConfig.SENSORS[i];
			Point pos = Utils.sensorDataToPoint(position, 0, sensor);
			g.fillRect(scale(pos.x - 2), scale(pos.y - 2), 4, 4);
		}
		// Draw a line to show the direction of the robot
		RobotState endpoint = Utils
				.driveForward(position, Emulator.LINE_LENGTH);
		int x = (int) scaledX;
		int y = (int) scaledY;
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
			Set<Entry<Point, Double>> points = brains.getBestParticleMap()
					.getCells().entrySet();
			int scaledGridSize = scale(Config.GRID_CELL_SIZE);
			int halfScaledGridSize = (int) (0.5 * Config.GRID_CELL_SIZE);
			for (Entry<Point, Double> entry : points) {
				Point key = entry.getKey();
				double value = entry.getValue();
				float c = (float) (1.0 - value);
				g.setColor(new Color(c, c, c));
				g.fillRect(scale(key.x - halfScaledGridSize), scale(key.y
						- halfScaledGridSize), scaledGridSize, scaledGridSize);
			}
		} catch (ConcurrentModificationException e) {
		}
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
		if (zoomIn) {
			// scale = scale + Emulator.ZOOM_FACTOR;
			if (scale < Emulator.ZOOM_FACTOR)
				scale += Emulator.ZOOM_FACTOR2;
			else
				scale += Emulator.ZOOM_FACTOR;
		} else {
			// scale = Math
			// .max(scale - Emulator.ZOOM_FACTOR, Emulator.ZOOM_FACTOR);
			if (scale - Emulator.ZOOM_FACTOR < Emulator.ZOOM_FACTOR) {
				scale = Math.max(scale - Emulator.ZOOM_FACTOR2,
						Emulator.ZOOM_FACTOR2);
			} else {
				scale -= Emulator.ZOOM_FACTOR;
			}
		}
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
}
