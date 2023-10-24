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
import ws3dproxy.model.Thing;
import ws3dproxy.model.WorldPoint;

public class GoToClosestFood extends Codelet {

    private Memory closestAppleMO;
    private Memory selfInfoMO;
    private MemoryContainer legsMO;
    private Memory mapMO;
    private double creatureBasicSpeed;
    private double reachDistance;
    private double minFuel;

    public GoToClosestFood(double creatureBasicSpeed, int reachDistance, double minFuel) {
        this.creatureBasicSpeed = creatureBasicSpeed;
        this.reachDistance = reachDistance;
        this.minFuel = minFuel;
        this.name = "GoToClosestFood";
    }

    @Override
    public void accessMemoryObjects() {
        closestAppleMO = (MemoryObject) this.getInput("CLOSEST_FOOD");
        selfInfoMO = (MemoryObject) this.getInput("INNER");
        legsMO = (MemoryContainer) this.getOutput("LEGS");
        mapMO = (MemoryObject) this.getInput("MAP");
    }

    @Override
    public void proc() {
        // Find distance between creature and closest apple
        //If far, go towards it
        //If close, stops

        Thing closestApple = (Thing) closestAppleMO.getI();
        Idea cis = (Idea) selfInfoMO.getI();
        int[][] map = (int[][]) mapMO.getI();

        if (closestApple != null && ((double) cis.get("fuel").getValue()) < minFuel) {
            double appleX = 0;
            double appleY = 0;
            try {
                appleX = closestApple.getCenterPosition().getX();
                appleY = closestApple.getCenterPosition().getY();

            } catch (Exception e) {
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
                if (distance > reachDistance) { //Go to it
                    int j = (int) Math.floor((appleX) / 40);
                    int i = (int) Math.floor((appleY) / 40);

                    Grid grid = new Grid(map, i, j);
                    grid.route();

                    message.put("ACTION", "GOTO");
                    message.put("X", (int) grid.miny * 40);
                    message.put("Y", (int) grid.minx * 40);
                    message.put("SPEED", creatureBasicSpeed);
                    activation = 0.9;

                } else {//Stop
                    message.put("ACTION", "GOTO");
                    message.put("X", (int) appleX);
                    message.put("Y", (int) appleY);
                    message.put("SPEED", 0.0);
                    activation = 0.5;
                }
                legsMO.setI(message.toString(), activation, name);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            activation = 0.0;
            legsMO.setI("", activation, name);
        }

    }//end proc

    @Override
    public void calculateActivation() {

    }

}
