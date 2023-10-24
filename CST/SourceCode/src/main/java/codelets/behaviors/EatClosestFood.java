/** ***************************************************************************
 * Copyright 2007-2015 DCA-FEEC-UNICAMP
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *    Klaus Raizer, Andre Paraense, Ricardo Ribeiro Gudwin
 **************************************************************************** */
package codelets.behaviors;

import java.awt.Point;
import java.awt.geom.Point2D;

import org.json.JSONException;
import org.json.JSONObject;

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.Memory;
import br.unicamp.cst.core.entities.MemoryContainer;
import br.unicamp.cst.core.entities.MemoryObject;
import br.unicamp.cst.representation.idea.Idea;
import memory.CreatureInnerSense;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import ws3dproxy.model.Thing;
import ws3dproxy.model.WorldPoint;

public class EatClosestFood extends Codelet {

    private Memory closestAppleMO;
    private Memory innerSenseMO;
    private Memory knownMO;
    private int reachDistance;
    private MemoryContainer handsMO;
    Thing closestApple;
    Idea cis;
    List<Thing> known;

    public EatClosestFood(int reachDistance) {
        setTimeStep(50);
        this.reachDistance = reachDistance;
        this.name = "EatClosestApple";
    }

    @Override
    public void accessMemoryObjects() {
        closestAppleMO = (MemoryObject) this.getInput("CLOSEST_FOOD");
        innerSenseMO = (MemoryObject) this.getInput("INNER");
        handsMO = (MemoryContainer) this.getOutput("HANDS");
        knownMO = (MemoryObject) this.getOutput("KNOWN_FOODS");
    }

    @Override
    public void proc() {
        String appleName = "";
        closestApple = (Thing) closestAppleMO.getI();
        cis = (Idea) innerSenseMO.getI();
        known = (List<Thing>) knownMO.getI();
        //Find distance between closest apple and self
        //If closer than reachDistance, eat the apple

        if (closestApple != null) {
            double appleX = 0;
            double appleY = 0;
            try {
                appleX = closestApple.getCenterPosition().getX();
                appleY = closestApple.getCenterPosition().getY();
                appleName = closestApple.getName();

            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            double selfX = ((WorldPoint) cis.get("position").getValue()).getX();
            double selfY = ((WorldPoint) cis.get("position").getValue()).getY();

            Point2D pApple = new Point();
            pApple.setLocation(appleX, appleY);

            Point2D pSelf = new Point();
            pSelf.setLocation(selfX, selfY);

            double distance = pSelf.distance(pApple);
            JSONObject message = new JSONObject();
            try {
                if (distance <= reachDistance) { //eat it						
                    message.put("OBJECT", appleName);
                    message.put("ACTION", "EATIT");
                    activation = 1.0;
                    handsMO.setI(message.toString(), activation, name);
                    DestroyClosestApple();
                } else {
                    activation = 0.0;
                    handsMO.setI("", activation, name);
                }

//				System.out.println(message);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            activation = 0.0;
            handsMO.setI("", activation, name);
        }
        //System.out.println("Before: "+known.size()+ " "+known);

        //System.out.println("After: "+known.size()+ " "+known);
        //System.out.println("EatClosestApple: "+ handsMO.getInfo());	
    }

    @Override
    public void calculateActivation() {

    }

    public void DestroyClosestApple() {
        int r = -1;
        int i = 0;
        synchronized (known) {
            CopyOnWriteArrayList<Thing> myknown = new CopyOnWriteArrayList<>(known);
            for (Thing t : known) {
                if (closestApple != null) {
                    if (t.getName().equals(closestApple.getName())) {
                        r = i;
                    }
                }
                i++;
            }
            if (r != -1) {
                known.remove(r);
            }
            closestApple = null;
        }
    }

}
