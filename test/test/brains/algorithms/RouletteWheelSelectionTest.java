package test.brains.algorithms;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import brains.Particle;
import brains.algorithms.RouletteWheelSelection;

public class RouletteWheelSelectionTest {
	private List<Particle> particles;
	private RouletteWheelSelection rws;

	@Before
	public void setUp() throws Exception {
		particles = new ArrayList<Particle>();
		particles.add(new Particle(null, null, 0.1));
		particles.add(new Particle(null, null, 0.5));
		particles.add(new Particle(null, null, 0.9));
		particles.add(new Particle(null, null, 1.5));
		particles.add(new Particle(null, null, 1.9));
		particles.add(new Particle(null, null, 2.5));
		
		rws = new RouletteWheelSelection();
	}

	@Test
	public void testNextRandomParticles() {
		int amount = 1000000;
		List<Particle> sample = rws.nextRandomParticles(particles, amount);
	
		assertEquals("Did not receive the right amount of samples", amount, sample.size());
		
		double sumWeights = 0;
		Map<Double, Integer> counting = new HashMap<Double, Integer>();
		for (Particle p : particles) {
			counting.put(p.getWeight(), 0);
			sumWeights += p.getWeight();
		}
		for (Particle p : sample) {
			double key = p.getWeight();
			counting.put(key, counting.get(key)+1);
		}
		for (Particle p : particles) {
			double key = p.getWeight();
			double probability = key / sumWeights;
			double occurrenceRate = ((double) counting.get(key)) / amount;
			double diff = probability - occurrenceRate; 
			
			String msg = 
				"Particle with key " + key + " occurred too often or too little. \n(With an occurrence rate of " +
				occurrenceRate + " instead of the expected rate of " + probability + ".)";
			assertTrue(msg, diff < 0.01);
			//System.out.println("Particle " + key + ", probability - occurrence_rate = " + diff);
		}
	}

	@Test
	public void testNextRandomParticle() {
		int amount = 1000000;
		List<Particle> sample = new ArrayList<Particle>();
		for (int i = 0; i < amount; i++) {
			sample.add(rws.nextRandomParticle(particles));
		}
	
		assertEquals("Did not receive the right amount of samples", amount, sample.size());
		
		double sumWeights = 0;
		Map<Double, Integer> counting = new HashMap<Double, Integer>();
		for (Particle p : particles) {
			counting.put(p.getWeight(), 0);
			sumWeights += p.getWeight();
		}
		for (Particle p : sample) {
			double key = p.getWeight();
			counting.put(key, counting.get(key)+1);
		}
		for (Particle p : particles) {
			double key = p.getWeight();
			double probability = key / sumWeights;
			double occurrenceRate = ((double) counting.get(key)) / amount;
			double diff = probability - occurrenceRate; 
			
			String msg = 
				"Particle with key " + key + " occurred too often or too little. \n(With an occurrence rate of " +
				occurrenceRate + " instead of the expected rate of " + probability + ".)";
			assertTrue(msg, diff < 0.01);
			//System.out.println("Particle " + key + ", probability - occurrence_rate = " + diff);
		}
	}
}
