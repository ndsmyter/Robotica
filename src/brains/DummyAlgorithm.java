/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package brains;

import common.RobotState;
import common.Utils;
import java.awt.Point;
import java.util.ArrayList;

public class DummyAlgorithm {

    public void dummy(Brains b) {
        b.processSensorData();
        
        for (int i = 0; i < 10; i++) {
            int step = 100; // Step length in mm 
            boolean free = true;
            ArrayList<Point> path = Utils.getPath(b.getCurrentState(), step);
            for (Point p : path) {
                free &= (b.getMap().get(p) < 0.60);
            }
            if(free){
                b.drive(step);
                b.processSensorData();
            } else {
                b.turn(90, true);                
            }
        }
                
    }
}
