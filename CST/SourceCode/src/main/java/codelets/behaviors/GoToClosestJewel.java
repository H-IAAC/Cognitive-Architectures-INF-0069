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
import org.json.JSONException;
import org.json.JSONObject;
import ws3dproxy.model.Thing;
import ws3dproxy.model.WorldPoint;

/**
 *
 * @author ia941
 */
public class GoToClosestJewel extends Codelet {

    private Memory closestJewelMO;
    private Memory selfInfoMO;
    private Memory leafletsMO;
    private Memory mapMO;
    private MemoryContainer legsMO;
    private double creatureBasicSpeed;
    private double reachDistance;
    private double minFuel;

    public GoToClosestJewel(double creatureBasicSpeed, int reachDistance, double minFuel) {
        this.creatureBasicSpeed = creatureBasicSpeed;
        this.reachDistance = reachDistance;
        this.name = "GoToClosestJewel";
        this.minFuel = minFuel;
    }

    @Override
    public void accessMemoryObjects() {
        closestJewelMO = (MemoryObject) this.getInput("CLOSEST_LEAFLET_JEWEL");
        selfInfoMO = (MemoryObject) this.getInput("INNER");
        leafletsMO = (MemoryObject) this.getInput("LEAFLETS");
        legsMO = (MemoryContainer) this.getOutput("LEGS");
        mapMO = (MemoryObject) this.getInput("MAP");
    }

    @Override
    public void calculateActivation() {

    }

    @Override
    public void proc() {
        Thing closestJewel = (Thing) closestJewelMO.getI();
        Idea cis = (Idea) selfInfoMO.getI();
        Idea leaflets = (Idea) leafletsMO.getI();
        int[][] map = (int[][]) mapMO.getI();

        if (closestJewel != null && ((double) cis.get("fuel").getValue()) >= minFuel) {

            double jewelX = 0;
            double jewelY = 0;
            try {
                jewelX = closestJewel.getCenterPosition().getX();
                jewelY = closestJewel.getCenterPosition().getY();

            } catch (Exception e) {
                e.printStackTrace();
            }

            double selfX = ((WorldPoint) cis.get("position").getValue()).getX();
            double selfY = ((WorldPoint) cis.get("position").getValue()).getY();

            Point2D pJewel = new Point();
            pJewel.setLocation(jewelX, jewelY);

            Point2D pSelf = new Point();
            pSelf.setLocation(selfX, selfY);

            double distance = pSelf.distance(pJewel);
            JSONObject message = new JSONObject();
            try {
                if (distance > reachDistance) { //Go to it
                    int j = (int) Math.floor((jewelX + 20) / 40);
                    int i = (int) Math.floor((jewelY + 20) / 40);

                    Grid grid = new Grid(map, i, j);
                    grid.route();

                    message.put("ACTION", "GOTO");
                    message.put("X", (int) grid.miny * 40);
                    message.put("Y", (int) grid.minx * 40);
                    message.put("SPEED", creatureBasicSpeed);
                    activation = 0.9;

                } else {//Stop
                    message.put("ACTION", "GOTO");
                    message.put("X", (int) jewelX);
                    message.put("Y", (int) jewelY);
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
    }

}
