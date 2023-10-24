/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codelets.sensors;

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.Memory;
import br.unicamp.cst.core.entities.MemoryObject;
import br.unicamp.cst.representation.idea.Idea;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import ws3dproxy.CommandExecException;
import ws3dproxy.CommandUtility;
import ws3dproxy.model.Bag;
import ws3dproxy.model.Creature;
import ws3dproxy.model.Leaflet;
import ws3dproxy.util.Constants;

/**
 *
 * @author ia941
 */
public class LeafletSense extends Codelet{
    
    private Creature c;
    private Memory leafletSenseMO;
    private Idea leaflets;
    private final String[] COLORS = {Constants.colorRED, Constants.colorGREEN,
                               Constants.colorBLUE, Constants.colorYELLOW, 
                               Constants.colorMAGENTA, Constants.colorWHITE};
    
    public LeafletSense(Creature nc){
        c = nc;
        this.name = "LeafletSense";
    }

    @Override
    public void accessMemoryObjects() {
        leafletSenseMO = (MemoryObject) this.getOutput("LEAFLETS");
        leaflets = (Idea) leafletSenseMO.getI();
    }

    @Override
    public void calculateActivation() {
        
    }

    @Override
    public void proc() {
        c.updateBag();
        Bag bag = c.getBag();
        int id = 1;
        try {
            List<Leaflet> leafletList = CommandUtility.getCreatureState(c.getName()).getLeaflets();
            for (Leaflet leaflet : leafletList){
                //Get leaflet or created if does not exist
                Idea leafletIdea = leaflets.get(String.format("LEAFLET_%d", id));
                if (leafletIdea == null){
                    leafletIdea = leaflets.add(new Idea(String.format("LEAFLET_%d", id), null, 0));
                    for (String color : COLORS){
                        Idea colorIdea = new Idea(color, null, 0);
                        colorIdea.add(new Idea("NEED", 0, 1));
                        colorIdea.add(new Idea("HAS", 0, 1));
                        leafletIdea.add(colorIdea);
                    }
                    leafletIdea.add(new Idea("COMPLETED", false, 1));
                    leafletIdea.add(new Idea("ID", false, 1));
                }

                for (String color : COLORS){
                    Idea colorIdea = leafletIdea.get(color);
                    colorIdea.get("NEED").setValue(leaflet.getTotalNumberOfType(color));
                    colorIdea.get("HAS").setValue(bag.getNumberCrystalPerType(color));
                }  
                
                leafletIdea.get("ID").setValue(leaflet.getID());

                id += 1;
            }
        } catch (CommandExecException ex) {
            Logger.getLogger(LeafletSense.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
