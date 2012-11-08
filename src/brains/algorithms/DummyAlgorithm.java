package brains.algorithms;

import java.awt.Point;
import java.util.ArrayList;

import brains.Brains;

import common.Utils;
import roomba.RoombaConfig;

public class DummyAlgorithm implements AlgorithmInterface {
	// Step length in mm
	private static final int STEP = 50;
	private static final int MAX_VALUE = 100;

	private int i;

	public DummyAlgorithm() {
		reset();
	}

	public void run(Brains b) {
		while (i < MAX_VALUE && !b.isStopped()) {
			// System.out.println(b.getCurrentState());
			b.processSensorData();
			boolean free = true;
			ArrayList<Point> path = Utils.getPath(b.getCurrentState(), STEP
					+ RoombaConfig.ROOMBA_DIAMETER / 2);
			for (Point p : path) {
				free &= (b.getMap().get(Utils.pointToGrid(p)) < 0.60);
			}
			if (free) {
				b.drive(STEP);
			} else {
				b.turn(90, false);
			}
			i++;
		}
	}

	public void reset() {
		i = 0;
	}
}
