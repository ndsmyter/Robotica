/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package brains;

import java.awt.Point;
import java.util.ArrayList;

import common.Utils;
import roomba.RoombaConfig;

public class DummyAlgorithm {

	public static void run(Brains b) {
        int step = 50; // Step length in mm 
        for (int i = 0; i < 100; i++) {
            //System.out.println(b.getCurrentState());
            b.processSensorData();
            boolean free = true;
            ArrayList<Point> path = Utils.getPath(b.getCurrentState(), step+RoombaConfig.ROOMBA_DIAMETER/2);
            for (Point p : path) {
                free &= (b.getMap().get(Utils.pointToGrid(p)) < 0.60);
            }
            if(free){
                b.drive(step);
            } else {
                b.turn(90, false);
            }
        }
                
    }
}
