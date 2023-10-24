/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modules;

import edu.memphis.ccrg.lida.framework.FrameworkModule;
import edu.memphis.ccrg.lida.framework.shared.ElementFactory;
import edu.memphis.ccrg.lida.framework.shared.Link;
import edu.memphis.ccrg.lida.framework.shared.Linkable;
import edu.memphis.ccrg.lida.framework.shared.Node;
import edu.memphis.ccrg.lida.framework.shared.NodeStructure;
import edu.memphis.ccrg.lida.framework.shared.NodeStructureImpl;
import edu.memphis.ccrg.lida.framework.tasks.TaskManager;
import edu.memphis.ccrg.lida.pam.PerceptualAssociativeMemoryImpl;
import edu.memphis.ccrg.lida.workspace.structurebuildingcodelets.StructureBuildingCodeletImpl;
import edu.memphis.ccrg.lida.workspace.workspacebuffers.WorkspaceBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import structures.PlaceNode;
import structures.Position;
import ws3dproxy.model.Creature;
import ws3dproxy.model.WorldPoint;

/**
 *
 * @author bruno
 */
public class MapStructureBuildingCodelet extends StructureBuildingCodeletImpl {
    
    private static final Logger logger = Logger.getLogger(MapStructureBuildingCodelet.class.getCanonicalName());
    
    private static final int X_SIZE = 13;
    private static final int Y_SIZE = 17;
    
    protected SensoryMemory sensoryMemory;
    private boolean mapCreated;
    private ElementFactory factory;
    private PlaceNode[][] pos = new PlaceNode[X_SIZE][Y_SIZE];
    
    public MapStructureBuildingCodelet(){
        mapCreated = false;
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
        NodeStructure ns = (NodeStructure) writableBuffer.getBufferContent(null);
        //logger.warning("Init");
        
        //Create Initial spatial map
        if (!mapCreated){
            for (int i=0; i<X_SIZE; i++){
                for (int j=0; j<Y_SIZE; j++){
                    pos[i][j] = (PlaceNode) ns.addNode("PlaceNodeImpl", "pos"+i+"_"+j, 0.0, -1.0);
                    pos[i][j].setPos(i,j);
                }                
            }
            for (int i=0; i<X_SIZE; i++){
                for (int j=0; j<Y_SIZE; j++){
                    if (i>0)
                        ns.addLink(factory.getLink(pos[i-1][j], pos[i][j], PerceptualAssociativeMemoryImpl.LATERAL, 1.0, -1.0), "LinkImpl");
                    if (j<Y_SIZE-1)
                        ns.addLink(factory.getLink(pos[i][j+1], pos[i][j], PerceptualAssociativeMemoryImpl.LATERAL, 1.0, -1.0), "LinkImpl");
                    if (i<X_SIZE-1)
                        ns.addLink(factory.getLink(pos[i+1][j], pos[i][j], PerceptualAssociativeMemoryImpl.LATERAL, 1.0, -1.0), "LinkImpl");
                    if (j>0)
                        ns.addLink(factory.getLink(pos[i][j-1], pos[i][j], PerceptualAssociativeMemoryImpl.LATERAL, 1.0, -1.0), "LinkImpl");
                }                
            } 
            mapCreated = true;
        }
        //logger.warning("1");
        
        //Update self node
        Node selfNode;
        Optional<Node> presentNode = ns.getNodes().stream().filter(n -> n.getLabel().equals("self")).findFirst();
        if(presentNode.isPresent()){
            selfNode = presentNode.get();
        } else {
            selfNode = (Node) ns.addNode("NodeImpl", "self", 1.0, -1.0);
        }
        //logger.warning("2");
        
        Map<String, Object> detectorParams = new HashMap<>();
        detectorParams.put("mode", "body");
        WorldPoint body = (WorldPoint) sensoryMemory.getSensoryContent("", detectorParams);
        //logger.warning("3");
        
        if(body != null){
            int i = (int) Math.floor( (body.getY() + 25) / 50);
            int j = (int) Math.floor( (body.getX() + 25) / 50);
            //logger.warning(String.format("Agent %d-%d", i,j));
            if (body.distanceTo(new WorldPoint(j*50, i*50)) < 10)
                ns.addLink(factory.getLink(pos[i][j], selfNode, PerceptualAssociativeMemoryImpl.PARENT, 1.0, 0.0), "LinkImpl");
        
            
            Optional<Node> goalNodeOpt = ns.getNodes().stream().filter(n -> n.getLabel().equals("goal")).findFirst();
            Node goalNode;
            Position goalPos;
            int q = 0;
            int w = 0;
            if (goalNodeOpt.isPresent()){
                goalNode = goalNodeOpt.get();
                

                Map<Node, Link> goalLinks = ns.getConnectedSources((Linkable) goalNode);
                //logger.warning(String.format("links %d", goalLinks.size()));
                if (goalLinks.size() >= 1){
                    PlaceNode goalPlaceNode = (PlaceNode) goalLinks.keySet().toArray()[0];
                    goalPos = goalPlaceNode.getPos();
                    q = goalPos.getX();
                    w = goalPos.getY();
                }
            }
            
            
            String mapp = "\n";
            for (int k=0; k<X_SIZE; k++){
                for (int l=0; l<Y_SIZE; l++){
                    mapp += pos[k][l].isOccupied()? "o" : (i == k && j == l? "x": (q == k && w == l? "g": " "));
                }                
                mapp += "\n";
            }
            logger.warning(mapp);
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
