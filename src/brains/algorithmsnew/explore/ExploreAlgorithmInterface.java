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
		int y = (x + 360) % 360;
		if (y > 180)
			y -= 360;
		return new int[] { 0, y };
	}

	public int[] dontMove() {
		return new int[] { 0, 0 };
	}

	public int[] home() {
		System.out.println("Finally, home again :-D");
		return new int[] { 0, 0 };
	}
}
