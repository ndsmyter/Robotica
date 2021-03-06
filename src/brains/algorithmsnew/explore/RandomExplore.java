package brains.algorithmsnew.explore;

import java.util.Random;

import brains.MapStructure;

import common.Utils;

public class RandomExplore extends ExploreAlgorithmInterface {
	private Random random = new Random();
	private static final int STEP = 50;
	private static final int TURN = 10;

	// rotation := the direction of the rotation.
	// should always be either -1 or 1.
	private int rotation = 1;

	// pirouette := amount of steps during which kate has to whirl about
	private int pirouette = 0;

	@Override
	public void reset() {
		rotation = 1;
		pirouette = 0;
	}

	@Override
	public int[] explore(MapStructure map) {
		// if kate is doing a pirouette, continue doing it until it's done
		if (pirouette > 0) {
			pirouette--;
			return turn(rotation * TURN);
		} else {
			// should kate perform a pirouette next?
			int pir_rand = random.nextInt(300);

			// yes, kate should perform a pirouette if pirouette == 0!
			// pirouette := amount of steps during which kate has to whirl
			// about.
			if (pir_rand == 0)
				pirouette = random.nextInt(180);

			// should kate switch rotation direction?
			int rot_rand = random.nextInt(300);
			if (rot_rand == 0)
				rotation = -rotation;

			// check whether kate is able to go straight or not
			boolean free = Utils.isPathFree(map.getPosition(), STEP, map);

			// if kate is able to go straight, do so.
			// else, rotate 2 degrees to the left or the right
			if (free) {
				return drive(STEP);
			} else {
				return turn(rotation * TURN);
			}
		}
	}
}
