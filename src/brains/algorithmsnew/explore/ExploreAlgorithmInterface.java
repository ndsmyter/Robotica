package brains.algorithmsnew.explore;

import brains.MapStructure;

public abstract class ExploreAlgorithmInterface {
	// returns u
	public abstract int[] explore(MapStructure map);

	public abstract void reset();

	public int[] drive(int x) {
		return new int[] { x, 0 };
	}

	public int[] turn(int x) {
		return new int[] { 0, x };
	}

	public int[] dontMove() {
		return new int[] { 0, 0 };
	}
}
