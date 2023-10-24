/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codelets.perception;

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.Memory;
import br.unicamp.cst.core.entities.MemoryObject;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import ws3dproxy.model.Thing;

/**
 *
 * @author bruno
 */
public class BrickDetector extends Codelet {

    private Memory visionMO;
    private Memory knownBricksMO;
    
    public BrickDetector(){
        this.name = "BrickDetector";
    }
    
    @Override
    public void accessMemoryObjects() {
        synchronized(this) {
            this.visionMO=(MemoryObject)this.getInput("VISION");
        }
        this.knownBricksMO=(MemoryObject)this.getOutput("KNOWN_BRICKS");
	
    }

    @Override
    public void calculateActivation() {
    
    }

    @Override
    public void proc() {
            CopyOnWriteArrayList<Thing> vision;
            List<Thing> known;
            synchronized (visionMO) {
               vision = new CopyOnWriteArrayList((List<Thing>) visionMO.getI());    
               known = Collections.synchronizedList((List<Thing>) knownBricksMO.getI());  
               synchronized(vision) {
                 for (Thing t : vision) {
                    boolean found = false;
                    synchronized(known) {
                       CopyOnWriteArrayList<Thing> myknown = new CopyOnWriteArrayList<>(known);
                       for (Thing e : myknown)
                          if (t.getName().equals(e.getName())) {
                            found = true;
                            break;
                          }
                       if (found == false && t.getName().contains("Brick")) known.add(t);
                    }
               
                 }
               }
            }
    }
    
}
