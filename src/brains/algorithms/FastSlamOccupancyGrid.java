package brains.algorithms;

import java.util.ArrayList;
import java.util.List;

import brains.Brains;
import brains.MapStructure;
import brains.Particle;

import common.RobotState;

/**
 *
 * @author sofie
 */
public class FastSlamOccupancyGrid implements AlgorithmInterface {
	private RouletteWheelSelection roulette;
	
	public FastSlamOccupancyGrid() {
		roulette = new RouletteWheelSelection();
	}
    
    public void run(Brains b) {
        for (int i = 0; i < 10; i++) {
            int[] u = {0,0};
            int[] z = b.getSensorData();
            List<Particle> particles = slamComputation(b.getParticles(), u, z);
            b.setParticles(particles);
        }        
    }
    
    /*
     * For quick reference: pg 478
     */
    public List<Particle> slamComputation(List<Particle> particles, int[] u, int[] z){
        List<Particle> updatedParticles = new ArrayList<Particle>();
        
        for(Particle p : particles){
            RobotState newPosition = sampleMotionModel(u, p.getPosition());
            double weight = measurementModelMap(z, newPosition, p.getMap());
            MapStructure newMap = updatedOccupancyGrid(z, newPosition, p.getMap());
            updatedParticles.add(new Particle(newPosition,newMap,weight));
        }
        
        List<Particle> resampledParticles = roulette.nextRandomParticles(updatedParticles, particles.size());
        
        return resampledParticles;
    }
    
    public RobotState sampleMotionModel(int[] u, RobotState x){
        return x;
    }
    
    public double measurementModelMap(int[] z, RobotState x, MapStructure m){
    
        return 1.0;
    }
    
    public MapStructure updatedOccupancyGrid(int[] z, RobotState x, MapStructure m){
        return m;
    }
    

    public void reset() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
