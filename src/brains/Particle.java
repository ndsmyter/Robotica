package brains;


public class Particle {
    private MapStructure map;
    private double weight;
    
    public Particle(MapStructure map, double weight){
        this.map = map;
        this.weight = weight;
    }

    public MapStructure getMap() {
        return map;
    }

    public void setMap(MapStructure map) {
        this.map = map;
    }
   
    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }
    
    public Particle clone() {
    	return new Particle(map.clone(), weight);
    }
}
