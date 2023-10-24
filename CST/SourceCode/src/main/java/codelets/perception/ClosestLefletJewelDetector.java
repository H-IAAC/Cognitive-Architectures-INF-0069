/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codelets.perception;

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.Memory;
import br.unicamp.cst.core.entities.MemoryObject;
import br.unicamp.cst.representation.idea.Idea;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import ws3dproxy.model.Thing;
import ws3dproxy.model.WorldPoint;

/**
 *
 * @author bruno
 */
public class ClosestLefletJewelDetector extends Codelet {

    private Memory knownMO;
    private Memory closestLefletJewelMO;
    private Memory innerSenseMO;
    private Memory leafletsMO;

    private List<Thing> known;

    public ClosestLefletJewelDetector() {
        this.name = "ClosestLefletJewelDetector";
    }

    @Override
    public void accessMemoryObjects() {
        this.knownMO = (MemoryObject) this.getInput("KNOWN_JEWELS");
        this.innerSenseMO = (MemoryObject) this.getInput("INNER");
        this.closestLefletJewelMO = (MemoryObject) this.getOutput("CLOSEST_LEAFLET_JEWEL");
        this.leafletsMO = (MemoryObject) this.getInput("LEAFLETS");
    }

    @Override
    public void proc() {
        Thing closest_jewel = null;
        known = Collections.synchronizedList((List<Thing>) knownMO.getI());
        Idea cis = (Idea) innerSenseMO.getI();
        Idea leaflets = (Idea) leafletsMO.getI();

        Set<String> neededColors = new HashSet();
        for (Idea l : leaflets.getL()) {
            for (Idea child : l.getL()) {
                if (child.getType() == 0) {
                    int miss = ((int) child.get("NEED").getValue()) - ((int) child.get("HAS").getValue());
                    if (miss > 0 && !((boolean) l.get("COMPLETED").getValue())) {
                        neededColors.add(child.getName());
                    }
                }
            }
        }

        synchronized (known) {
            if (known.size() != 0) {
                //Iterate over objects in vision, looking for the closest apple
                CopyOnWriteArrayList<Thing> myknown = new CopyOnWriteArrayList<>(known);
                for (Thing t : myknown) {
                    String objectName = t.getName();
                    String objectColor = t.getAttributes().getColor();
                    if (objectName.contains("Jewel") && neededColors.contains(objectColor)) { //Then, it is an apple
                        if (closest_jewel == null) {
                            closest_jewel = t;
                        } else {
                            double px = ((WorldPoint) cis.get("position").getValue()).getX();
                            double py = ((WorldPoint) cis.get("position").getValue()).getY();
                            double Dnew = calculateDistance(t.getX1(), t.getY1(), px, py);
                            double Dclosest = calculateDistance(closest_jewel.getX1(), closest_jewel.getY1(), px, py);
                            if (Dnew < Dclosest) {
                                closest_jewel = t;
                            }
                        }
                    }
                }

                if (closest_jewel != null) {
                    if (closestLefletJewelMO.getI() == null || !closestLefletJewelMO.getI().equals(closest_jewel)) {
                        closestLefletJewelMO.setI(closest_jewel);
                    }

                } else {
                    //couldn't find any nearby apples
                    closest_jewel = null;
                    closestLefletJewelMO.setI(closest_jewel);
                }
            } else { // if there are no known apples closest_apple must be null
                closest_jewel = null;
                closestLefletJewelMO.setI(closest_jewel);
            }
        }
    }//end proc

    @Override
    public void calculateActivation() {

    }

    private double calculateDistance(double x1, double y1, double x2, double y2) {
        return (Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2)));
    }

}
