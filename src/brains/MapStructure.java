package brains;

import java.awt.Point;
import java.util.HashMap;

import brains.interfaces.MapInterface;

import common.RobotState;

public class MapStructure implements MapInterface {
	private HashMap<Point, Double> cells;
	private HashMap<Point, Double> logOdds;
	private RobotState position = new RobotState(0, 0, 0);

	public MapStructure() {
		cells = new HashMap<Point, Double>();
		logOdds = new HashMap<Point, Double>();
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
		this.position = position;
	}

	@Override
	public double get(Point point) {
		double value;
		if (cells.containsKey(point))
			value = cells.get(point);
		else
			value = 0.5;
		return value;
	}

	public double getLogOdds(Point point) {
		double value;
		if (logOdds.containsKey(point))
			value = logOdds.get(point);
		else
			value = 0;
		return value;
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
		return newMap;
	}

	public void useNewMap(MapStructure newMap) {
		setCells(newMap.getCells());
		setLogOdds(newMap.getLogOdds());
		setPosition(newMap.getPosition());
	}
}