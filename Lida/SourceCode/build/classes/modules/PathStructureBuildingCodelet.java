/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modules;

import edu.memphis.ccrg.lida.framework.FrameworkModule;
import edu.memphis.ccrg.lida.framework.initialization.GlobalInitializer;
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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import structures.PlaceNode;

/**
 *
 * @author bruno
 */
public class PathStructureBuildingCodelet extends StructureBuildingCodeletImpl {
    
    private static final Logger logger = Logger.getLogger(PathStructureBuildingCodelet.class.getCanonicalName());
    private static final GlobalInitializer initializer = GlobalInitializer.getInstance();
    ElementFactory factory;
    
    public PathStructureBuildingCodelet(){
        factory = ElementFactory.getInstance();
    }
    
    @Override
    public void setAssociatedModule(FrameworkModule module, String usage) {
        if (module instanceof WorkspaceBuffer) {
            writableBuffer = (WorkspaceBuffer) module;
        } else {
            logger.log(
                    Level.WARNING,
                    "Expected module to be a WorkspaceBuffer or SensoryMemory but it was not.  Module not added.",
                    TaskManager.getCurrentTick());
        }
    }
    
    @Override
    protected void runThisFrameworkTask() {
        //logger.warning("Inti P");
        NodeStructure ns = (NodeStructure) writableBuffer.getBufferContent(null);
        
        //Get goal node
        Optional<Node> goalNodeOpt = ns.getNodes().stream().filter(n -> n.getLabel().equals("goal")).findFirst();
        if (goalNodeOpt.isPresent()){
            Node goalNode = goalNodeOpt.get();
            
            //Get PlaceNode linked to goal 
            Map<Node, Link> mapGoal = ns.getConnectedSources((Linkable) goalNode);
            if (!mapGoal.isEmpty()){
                //logger.warning("goal pos");
                PlaceNode goalPlaceNode = (PlaceNode) mapGoal.keySet().toArray()[0];
                goalPlaceNode.setActivation(1.0);
            
                //Get all PlaceNodes from map
                List<Node> posNodes = ns.getNodes().stream().filter(n -> n.getLabel().contains("pos")).collect(Collectors.toList());
                //---Propagate activations---
                propagateActivations(posNodes, goalPlaceNode, ns);
                Collections.reverse(posNodes);
                propagateActivations(posNodes, goalPlaceNode, ns);
            }
            
            //Get self node
            Optional<Node> selfNodeOpt = ns.getNodes().stream().filter(n -> n.getLabel().equals("self")).findFirst();
            if (selfNodeOpt.isPresent()){
                Node selfNode = selfNodeOpt.get();
                 //Get PlaceNode linked to self 
                Map<Node, Link> mapSelf = ns.getConnectedSources((Linkable) selfNode);
                if (!mapSelf.isEmpty()){
                    //logger.warning("self pos");
                    PlaceNode selfPlaceNode = (PlaceNode) mapSelf.keySet().toArray()[0];
                    //Check for adjecent pos with higher activation
                    PlaceNode moveDir = selfPlaceNode;
                    Map<Node, Link> connections = ns.getConnectedSources((Linkable) selfPlaceNode);
                    for (Entry e : connections.entrySet()){
                        PlaceNode connectedNode = (PlaceNode) e.getKey();
                        double neighbourActivation = connectedNode.getActivation();
                        if (neighbourActivation > moveDir.getActivation())
                            moveDir = connectedNode;
                    }
                    
                    Optional<Node> moveNodeOpt = ns.getNodes().stream().filter(n -> n.getLabel().equals("move")).findFirst();
                    Node moveNode;
                    if (moveNodeOpt.isPresent()){
                        moveNode = moveNodeOpt.get();
                    } else {
                        moveNode = ns.addNode("PamNodeImpl", "move", 1.0, -1.0);
                    }
                    moveNode.excite(1.0);
                    ns.addLink(factory.getLink(moveDir, moveNode, PerceptualAssociativeMemoryImpl.PARENT, 1.0, 0.0), "LinkImpl");
                    initializer.setAttribute("moveDir", moveDir.getLabel());
                    //logger.warning(moveDir.getLabel());
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

    private void propagateActivations(List<Node> posNodes, PlaceNode goalPlaceNode, NodeStructure ns) {
        for (Node n : posNodes){
            PlaceNode pn = (PlaceNode) n;
            if (pn.isOccupied()){
                //logger.warning("occ");
                n.setActivation(0.0);
            } else if (!n.getLabel().equals(goalPlaceNode.getLabel())){
                double strongestNeighbourActivation = 0.0;
                Map<Node, Link> connections = ns.getConnectedSources((Linkable) n);
                for (Entry e : connections.entrySet()){
                    Node connectedNode = (Node) e.getKey();
                    double neighbourActivation = connectedNode.getActivation();
                    if (neighbourActivation > strongestNeighbourActivation)
                        strongestNeighbourActivation = neighbourActivation;
                }
                n.setActivation(strongestNeighbourActivation*0.99);
            }
        } 
    }
}
