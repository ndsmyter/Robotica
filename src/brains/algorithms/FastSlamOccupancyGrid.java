package brains.algorithms;

import java.util.ArrayList;
import java.util.List;

import brains.Brains;
import brains.MapStructure;
import brains.Particle;
import common.Config;

import common.RobotState;
import common.Sensor;
import common.Utils;
import java.awt.Point;
import roomba.RoombaConfig;

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
        MapStructure mapNew = m.clone();
        for (int i = 0; i < RoombaConfig.SENSORS.length; i++) {
            Sensor s = RoombaConfig.SENSORS[i];
            RobotState sensorState = Utils.getSensorState(x,s);
            Point measurement = Utils.sensorDataToPoint(x, z[i], s);
            
            ArrayList<Point> path = Utils.getPath(sensorState, s.zMax);
            for (Point p : path) {
                double logOdds = m.getLogOdds(p) + inverseSensorModel(m,p,x,z[i], s);
                mapNew.putLogOdds(Utils.pointToGrid(p), logOdds);
            }
        }
        return mapNew;
    }
    
    public double inverseSensorModel(MapStructure m, Point p, RobotState x, int z, Sensor s){
        double result = 0;
        int r = Utils.euclideanDistance(new Point(x.x, x.y), p);
        p = Utils.pointToGrid(p);
        Point measurement = Utils.pointToGrid(Utils.sensorDataToPoint(x, z, s));
        if(r > Math.min(s.zMax,z)){
            result = 0;
        } else if (z < s.zMax && p.equals(measurement)){
            result = 1;
        } else if (r < z){
            result = -1;
        }        
        return result;
    }

    public void reset() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
