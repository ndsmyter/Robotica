package brains;

import common.RobotState;

public class Particle {
    private MapStructure map;
    private RobotState position;
    private double weight;
    
    public Particle(RobotState position, MapStructure map, double weight){
        this.map = map;
        this.position = position;
        this.weight = weight;
    }

    public MapStructure getMap() {
        return map;
    }

    public void setMap(MapStructure map) {
        this.map = map;
    }

    public RobotState getPosition() {
        return position;
    }

    public void setPosition(RobotState position) {
        this.position = position;
    }
   
    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }
}
