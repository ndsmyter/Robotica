package brains.interfaces;

import java.awt.Point;
import java.util.HashMap;

public interface MapInterface {
	public void put(Point point, double value);

	public double get(Point point);

	public HashMap<Point, Double> getAll();
}
