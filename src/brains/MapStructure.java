package brains;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;

import brains.interfaces.MapInterface;
import brains.interfaces.ObstacleListener;

public class MapStructure implements MapInterface {
	private HashMap<Point, Double> cells;

	public MapStructure() {
		cells = new HashMap<Point, Double>();
	}

	@Override
	public void put(Point point, double value) {
		cells.put(point, value);
		fireObstacleAdded(point, value);
	}

	@Override
	public double get(Point point) {
            double value;
            if(cells.containsKey(point))
		value = cells.get(point);
            else 
                value = 0.5;
            return value;
	}

	@Override
	public HashMap<Point, Double> getAll() {
		return cells;
	}

	private ArrayList<ObstacleListener> obstacleListener = new ArrayList<ObstacleListener>();

	public void addObstacleListener(ObstacleListener listener) {
		obstacleListener.add(listener);
	}

	public void fireObstacleAdded(Point point, double value) {
		for (ObstacleListener listener : obstacleListener)
			listener.obstacleAdded(point, value);
	}
}