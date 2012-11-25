package brains;

import java.awt.Point;
import java.util.HashMap;
import java.util.Map.Entry;

import brains.interfaces.MapInterface;

public class MapStructure implements MapInterface {
	private HashMap<Point, Double> cells;
	private HashMap<Point, Double> logOdds;

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
	public HashMap<Point, Double> getAll() {
		return cells;
	}

	public MapStructure clone() {
		MapStructure newMap = new MapStructure();
		for (Entry<Point, Double> cell : this.cells.entrySet())
			newMap.put(cell.getKey(), cell.getValue());
		return newMap;
	}

	public void useNewMap(MapStructure newMap) {
		this.cells.clear();
		for (Entry<Point, Double> cell : newMap.getAll().entrySet())
			this.put(cell.getKey(), cell.getValue());
	}
}