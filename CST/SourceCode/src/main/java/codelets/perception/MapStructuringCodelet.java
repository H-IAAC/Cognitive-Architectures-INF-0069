/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codelets.perception;

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.Memory;
import br.unicamp.cst.core.entities.MemoryObject;
import br.unicamp.cst.representation.idea.Idea;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import ws3dproxy.model.Thing;
import ws3dproxy.model.WorldPoint;

/**
 *
 * @author bruno
 */
public class MapStructuringCodelet extends Codelet {

    private Memory knownFoods;
    private Memory knownJewels;
    private Memory knownBricks;
    private Memory selfMO;
    private Memory mapMO;

    public MapStructuringCodelet() {
        this.name = "MapStructuringCodelet";
    }

    @Override
    public void accessMemoryObjects() {
        knownBricks = (MemoryObject) this.getInput("KNOWN_BRICKS");
        mapMO = (MemoryObject) this.getOutput("MAP");
        selfMO = (MemoryObject) this.getInput("INNER");
    }

    @Override
    public void calculateActivation() {

    }

    @Override
    public void proc() {
        List<Thing> bricks = Collections.synchronizedList((List<Thing>) knownBricks.getI());
        int[][] map = (int[][]) mapMO.getI();
        Idea cis = (Idea) selfMO.getI();

        synchronized (bricks) {
            if (bricks.size() != 0) {
                synchronized (map) {
                    CopyOnWriteArrayList<Thing> myknown = new CopyOnWriteArrayList<>(bricks);
                    for (Thing t : myknown) {

                        WorldPoint pos = new WorldPoint((t.getX1() + t.getX2()) / 2, (t.getY1() + t.getY2()) / 2);
                        int j = (int) Math.floor(pos.getX() / 40);
                        int i = (int) Math.floor(pos.getY() / 40);

                        map[i][j] = -1;

                    }
                }
            }

            for (int i = 0; i < map.length; i++) {
                for (int j = 0; j < map[i].length; j++) {
                    if (map[i][j] == 1) {
                        map[i][j] = 0;
                    }
                }
            }
            WorldPoint self = (WorldPoint) cis.get("position").getValue();
            int j = (int) Math.floor((self.getX() + 20) / 40);
            int i = (int) Math.floor((self.getY() + 20) / 40);
            map[i][j] = 1;

            String line = "map    ";
            for (int l = 0; l < map.length; l++) {
                for (int m = 0; m < map[l].length; m++) {
                    line += map[l][m] == -1 ? "x" : (map[l][m] == 1 ? "o" : " ");
                }
                line += "\nmap    ";
            }
            System.out.println(line);
        }

    }
}
