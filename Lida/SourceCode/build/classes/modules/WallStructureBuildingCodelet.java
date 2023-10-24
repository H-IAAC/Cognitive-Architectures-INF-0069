/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modules;

import edu.memphis.ccrg.lida.framework.FrameworkModule;
import edu.memphis.ccrg.lida.framework.shared.ElementFactory;
import edu.memphis.ccrg.lida.framework.shared.Node;
import edu.memphis.ccrg.lida.framework.shared.NodeStructure;
import edu.memphis.ccrg.lida.framework.shared.NodeStructureImpl;
import edu.memphis.ccrg.lida.framework.tasks.TaskManager;
import edu.memphis.ccrg.lida.workspace.structurebuildingcodelets.StructureBuildingCodeletImpl;
import edu.memphis.ccrg.lida.workspace.workspacebuffers.WorkspaceBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import structures.PlaceNode;
import ws3dproxy.model.Thing;
import ws3dproxy.model.WorldPoint;

/**
 *
 * @author bruno
 */
public class WallStructureBuildingCodelet extends StructureBuildingCodeletImpl {

    private static final Logger logger = Logger.getLogger(WallStructureBuildingCodelet.class.getCanonicalName());
    
    protected SensoryMemory sensoryMemory;
    private ElementFactory factory;
    
    public WallStructureBuildingCodelet(){
        factory = ElementFactory.getInstance();
    }
    
    @Override
    public void setAssociatedModule(FrameworkModule module, String usage) {
        if (module instanceof WorkspaceBuffer) {
            writableBuffer = (WorkspaceBuffer) module;
        } else if (module instanceof SensoryMemory){
            sensoryMemory = (SensoryMemory) module;
        } else {
            logger.log(
                    Level.WARNING,
                    "Expected module to be a WorkspaceBuffer or SensoryMemory but it was not.  Module not added.",
                    TaskManager.getCurrentTick());
        }
    }


    @Override
    protected void runThisFrameworkTask() {
        //logger.warning("Init W");
        NodeStructure ns = (NodeStructure) writableBuffer.getBufferContent(null);
        Optional<Node> brickNode = ns.getNodes().stream().filter(n -> n.getLabel().equals("brick")).findFirst();
        if (brickNode.isPresent()){
            Map<String, Object> detectorParams = new HashMap<>();
            detectorParams.put("mode", "brick");
            Thing brick = (Thing) sensoryMemory.getSensoryContent("", detectorParams);
            if (brick != null){
                WorldPoint pos = new WorldPoint((brick.getX1() + brick.getX2()) / 2, (brick.getY1() + brick.getY2()) / 2);
                int j = (int) Math.floor(pos.getX()/50);
                int i = (int) Math.floor(pos.getY()/50);

                Optional<Node> p1 = ns.getNodes().stream().filter(n -> n.getLabel().equals(String.format("pos%d_%d", i,j))).findFirst();
                if (p1.isPresent()){
                    PlaceNode pn = (PlaceNode) p1.get();
                    pn.setOccupied();
                }
            }
        }
    }

    @Override
    public boolean bufferContainsSoughtContent(WorkspaceBuffer wb) {
        return true;
    }

    @Override
    public NodeStructure retrieveWorkspaceContent(WorkspaceBuffer wb) {
        return new NodeStructureImpl();
    }
    
    
}
