/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package brains.algorithms;

import brains.Brains;
import brains.MapStructure;
import brains.Particle;
import common.RobotState;
import java.util.ArrayList;

/**
 *
 * @author sofie
 */
public class FastSlamOccupancyGrid implements AlgorithmInterface {
    
    public void run(Brains b) {
        for (int i = 0; i < 10; i++) {
            int[] u = {0,0};
            int[] z = b.getSensorData();
            ArrayList<Particle> particles = slamComputation(b.getParticles(), u, z);
            b.setParticles(particles);
        }        
    }
    
    public ArrayList<Particle> slamComputation(ArrayList<Particle> particles, int[] u, int[] z){
        ArrayList<Particle> particlesNewTmp = new ArrayList<Particle>();
        ArrayList<Particle> particlesNew = new ArrayList<Particle>();
        for(Particle p : particles){
            RobotState newPosition = sampleMotionModel(u, p.getPosition());
            double weight = measurementModelMap(z, newPosition, p.getMap());
            MapStructure newMap = updatedOccupancyGrid(z, newPosition, p.getMap());
            particlesNewTmp.add(new Particle(newPosition,newMap,weight));
        }
        for (int i = 0; i < particles.size(); i++) {
            int index = i;
            particlesNew.add(particlesNewTmp.get(i));
        }
        return particlesNew;
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
