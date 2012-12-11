package brains;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;

import brains.interfaces.MapInterface;

import common.RobotState;

public class MapStructure implements MapInterface {
	private HashMap<Point, Double> cells;
	private HashMap<Point, Double> logOdds;
	private RobotState position;
	private ArrayList<Point> path;

	public MapStructure() {
		cells = new HashMap<Point, Double>();
		logOdds = new HashMap<Point, Double>();
		path = new ArrayList<Point>();
		position = new RobotState(0, 0, 0);
	}

	@Override
	public void put(Point point, double value) {
		cells.put(point, value);
		logOdds.put(point, Math.log(value / (1 - value)));
	}

	public void putLogOdds(Point point, double value) {
		cells.put(point, 1 - (1 / (1 + Math.exp(value))));
		logOdds.put(point, value);
	}

	/**
	 * @return the position
	 */
	public RobotState getPosition() {
		return position;
	}

	/**
	 * @param position
	 *            the position to set
	 */
	public void setPosition(RobotState position) {
		this.position = position.clone();
	}

	/**
	 * @param position
	 *            the position to set
	 */
	public void logMovement() {
		path.add(new Point(position.x, position.y));
	}

	/**
	 * @return the path
	 */
	public ArrayList<Point> getPath() {
		return path;
	}

	/**
	 * @param path
	 *            the path to set
	 */
	public void setPath(ArrayList<Point> path) {
		// Make deepcopy of the ArrayList
		this.path = new ArrayList<Point>(path);
	}

	@Override
	public double get(Point point) {
		return cells.containsKey(point) ? cells.get(point) : 0.5;
	}

	public double getLogOdds(Point point) {
		return logOdds.containsKey(point) ? logOdds.get(point) : 0;
	}

	@Override
	public HashMap<Point, Double> getCells() {
		return cells;
	}

	public void setCells(HashMap<Point, Double> cells) {
		// Make deepcopy of the HashMap
		this.cells = new HashMap<Point, Double>(cells);
	}

	public HashMap<Point, Double> getLogOdds() {
		return logOdds;
	}

	public void setLogOdds(HashMap<Point, Double> logOdds) {
		// Make deepcopy of the HashMap
		this.logOdds = new HashMap<Point, Double>(logOdds);
	}

	public MapStructure clone() {
		MapStructure newMap = new MapStructure();
		newMap.setCells(getCells());
		newMap.setLogOdds(getLogOdds());
		newMap.setPosition(getPosition());
		newMap.setPath(getPath());
		return newMap;
	}

	public void useNewMap(MapStructure newMap) {
		setCells(newMap.getCells());
		setLogOdds(newMap.getLogOdds());
		setPosition(newMap.getPosition());
		setPath(newMap.getPath());
	}
}