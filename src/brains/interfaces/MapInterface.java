package brains.interfaces;

import java.awt.Point;

public interface MapInterface {
	public void put(Point point, double value);

	public double get(Point point);
}
