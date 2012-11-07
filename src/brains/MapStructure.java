package brains;

import java.awt.Point;
import java.util.HashMap;

import brains.interfaces.MapInterface;

public class MapStructure implements MapInterface {
	private HashMap<Point, Double> cells;

	public MapStructure() {
		cells = new HashMap<>();
	}

	public void put(Point point, double value) {
		cells.put(point, value);
	}

	public double get(Point point) {
		return cells.get(point);
	}
}