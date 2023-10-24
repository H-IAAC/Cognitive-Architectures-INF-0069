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

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.Memory;
import br.unicamp.cst.core.entities.MemoryContainer;
import br.unicamp.cst.core.entities.MemoryObject;
import br.unicamp.cst.representation.idea.Idea;
import java.util.List;
import java.util.Random;
import org.json.JSONException;
import org.json.JSONObject;
import ws3dproxy.model.Thing;
import ws3dproxy.model.WorldPoint;

/**
 *
 * @author klaus
 *
 *
 */
public class Forage extends Codelet {

    private Memory selfMO;
    private Memory mapMO;
    private List<Thing> known;
    private MemoryContainer legsMO;
    private int goalX = 0, goalY = 0;
    private double creatureBasicSpeed;
    private Random rnd = new Random();

    /**
     * Default constructor
     */
    public Forage(double creatureBasicSpeed) {
        this.name = "Forage";
        this.creatureBasicSpeed = creatureBasicSpeed;
    }

    @Override
    public void proc() {
        Idea cis = (Idea) selfMO.getI();
        int[][] map = (int[][]) mapMO.getI();

        JSONObject message = new JSONObject();
        try {
            WorldPoint self = (WorldPoint) cis.get("position").getValue();
            WorldPoint goalPoint = new WorldPoint(goalY * 40, goalX * 40);
            if (self.distanceTo(goalPoint) < 120) {
                goalX = rnd.nextInt(15);
                goalY = rnd.nextInt(20);
            }

            Grid grid = new Grid(map, goalX, goalY);
            grid.route();
            int aa = (int) grid.miny * 40;
            int bb = (int) grid.minx * 40;
            if (aa == 0 && bb == 0) {
                System.out.println("------------");
                System.out.println("---" + self.simpleToString());
                System.out.println("------------");
            }
            message.put("ACTION", "GOTO");
            message.put("X", (int) grid.miny * 40);
            message.put("Y", (int) grid.minx * 40);
            message.put("SPEED", creatureBasicSpeed);
            activation = 0.3;
            legsMO.setI(message.toString(), activation, name);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void accessMemoryObjects() {
        legsMO = (MemoryContainer) this.getOutput("LEGS");
        selfMO = (MemoryObject) this.getInput("INNER");
        mapMO = (MemoryObject) this.getInput("MAP");
    }

    @Override
    public void calculateActivation() {

    }

}
