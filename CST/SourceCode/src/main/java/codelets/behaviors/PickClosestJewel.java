/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codelets.behaviors;

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.Memory;
import br.unicamp.cst.core.entities.MemoryContainer;
import br.unicamp.cst.core.entities.MemoryObject;
import br.unicamp.cst.representation.idea.Idea;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.json.JSONException;
import org.json.JSONObject;
import ws3dproxy.model.Thing;
import ws3dproxy.model.WorldPoint;

/**
 *
 * @author ia941
 */
public class PickClosestJewel extends Codelet {

	private Memory closestJewelMO;
	private Memory innerSenseMO;
        private Memory knownMO;
	private int reachDistance;
	private MemoryContainer handsMO;
        Thing closestJewel;
        Idea cis;
        List<Thing> known;

	public PickClosestJewel(int reachDistance) {
                setTimeStep(50);
		this.reachDistance=reachDistance;
                this.name = "PickClosestJewel";
	}

	@Override
	public void accessMemoryObjects() {
		closestJewelMO=(MemoryObject)this.getInput("CLOSEST_JEWEL");
		innerSenseMO=(MemoryObject)this.getInput("INNER");
		handsMO=(MemoryContainer)this.getOutput("HANDS");
                knownMO = (MemoryObject)this.getOutput("KNOWN_JEWELS");
	}

	@Override
	public void proc() {
                String jewelName="";
                closestJewel = (Thing) closestJewelMO.getI();
                cis = (Idea) innerSenseMO.getI();
                known = (List<Thing>) knownMO.getI();
		
		if(closestJewel != null)
		{
			double jewelX=0;
			double jewelY=0;
			try {
				jewelX=closestJewel.getCenterPosition().getX();
				jewelY=closestJewel.getCenterPosition().getY();
                                jewelName = closestJewel.getName();
                                

			} catch (Exception e) {
				e.printStackTrace();
			}

			double selfX=((WorldPoint) cis.get("position").getValue()).getX();
			double selfY=((WorldPoint) cis.get("position").getValue()).getY();

			Point2D pJewel = new Point();
			pJewel.setLocation(jewelX, jewelY);

			Point2D pSelf = new Point();
			pSelf.setLocation(selfX, selfY);

			double distance = pSelf.distance(pJewel);
			JSONObject message=new JSONObject();
			try {
				if(distance<=reachDistance){						
					message.put("OBJECT", jewelName);
					message.put("ACTION", "PICKUP");
                                        activation=1.0;
					handsMO.setI(message.toString(), activation, name);
                                        DestroyClosestJewel();
				}else{
                                        activation=0.0;
					handsMO.setI("", activation, name);
				}
				
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}else{
                        activation=0.0;
                        handsMO.setI("", activation, name);
		}

	}
        
        @Override
        public void calculateActivation() {
        
        }
        
        public void DestroyClosestJewel() {
           int r = -1;
           int i = 0;
           synchronized(known) {
             CopyOnWriteArrayList<Thing> myknown = new CopyOnWriteArrayList<>(known);  
             for (Thing t : myknown) {
              if (closestJewel != null) 
                 if (t.getName().equals(closestJewel.getName())) r = i;
              i++;
             }   
             if (r != -1) known.remove(r);
             closestJewel = null;
           }
        }

}
