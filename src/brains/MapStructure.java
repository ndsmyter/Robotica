package brains;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import brains.interfaces.MapInterface;
import brains.interfaces.ObstacleListener;

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
		logOdds.put(point, Math.log(value/(1-value)));
		fireObstacleAdded(point, value);
	}
        
        
	public void putLogOdds(Point point, double value) {
		cells.put(point, 1 - (1/(1+Math.exp(value))));
		logOdds.put(point, value);
		fireObstacleAdded(point, value);
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

	private ArrayList<ObstacleListener> obstacleListener = new ArrayList<ObstacleListener>();

	public void addObstacleListener(ObstacleListener listener) {
		obstacleListener.add(listener);
	}

	public void fireObstacleAdded(Point point, double value) {
		for (ObstacleListener listener : obstacleListener)
			listener.obstacleAdded(point, value);
	}

	public MapStructure clone() {
		MapStructure newMap = new MapStructure();
		for (Entry<Point, Double> cell : this.cells.entrySet())
			newMap.put(cell.getKey(), cell.getValue());
		return newMap;
	}
}