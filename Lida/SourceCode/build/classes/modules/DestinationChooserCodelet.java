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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import structures.PlaceNode;
import structures.Position;

/**
 *
 * @author bruno
 */
public class DestinationChooserCodelet extends StructureBuildingCodeletImpl {
    private static final Logger logger = Logger.getLogger(DestinationChooserCodelet.class.getCanonicalName());
    private ElementFactory factory;
    
    public DestinationChooserCodelet(){
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
        //logger.warning("Init W");
        NodeStructure ns = (NodeStructure) writableBuffer.getBufferContent(null);
        
        Optional<Node> selfNodeOpt = ns.getNodes().stream().filter(n -> n.getLabel().equals("self")).findFirst();
        if (selfNodeOpt.isPresent()){
            Node selfNode = selfNodeOpt.get();
            
            Map<Node, Link> map = ns.getConnectedSources((Linkable) selfNode);
            if (!map.isEmpty()){
                PlaceNode selfPlaceNode = (PlaceNode) map.keySet().toArray()[0];
                Position selfPos = selfPlaceNode.getPos();

                Optional<Node> goalNodeOpt = ns.getNodes().stream().filter(n -> n.getLabel().equals("goal")).findFirst();
                Node goalNode;
                Position goalPos;
                if (goalNodeOpt.isPresent()){
                    goalNode = goalNodeOpt.get();
                } else {
                    goalNode = ns.addNode("NodeImpl", "goal", 1.0, -1.0);
                }
            
                Map<Node, Link> goalLinks = ns.getConnectedSources((Linkable) goalNode);
                //logger.warning(String.format("links %d", goalLinks.size()));
                if (goalLinks.size() == 1){
                    PlaceNode goalPlaceNode = (PlaceNode) goalLinks.keySet().toArray()[0];
                    goalPos = goalPlaceNode.getPos();
                
                    if (Position.distBetween(goalPos, selfPos) < 4){
                        //logger.warning("New goal");
                        List<Node> posNodes = ns.getNodes().stream().filter(n -> n.getLabel().contains("pos")).collect(Collectors.toList());
                        Collections.shuffle(posNodes);
                        PlaceNode newGoalPlaceNode = (PlaceNode) posNodes.get(0);
                        ns.removeLink(goalLinks.get(goalPlaceNode));
                        ns.addLink(factory.getLink(newGoalPlaceNode, goalNode, PerceptualAssociativeMemoryImpl.PARENT, 1.0, -1.0), "LinkImpl");
                        
                        //logger.warning(newGoalPlaceNode.getLabel());
                    } else {
                        Link goalLink = (Link) goalLinks.values().toArray()[0];
                        goalLink.excite(1.0);
                        //logger.warning(goalPlaceNode.getLabel());
                    }
                } else if (goalLinks.isEmpty()){
                    //logger.warning("First goal");
                    List<Node> posNodes = ns.getNodes().stream().filter(n -> n.getLabel().contains("pos")).collect(Collectors.toList());
                    Collections.shuffle(posNodes);
                    PlaceNode newGoalPlaceNode = (PlaceNode) posNodes.get(0);
                    ns.addLink(factory.getLink(newGoalPlaceNode, goalNode, PerceptualAssociativeMemoryImpl.PARENT, 1.0, -1.0), "LinkImpl");
                    //logger.warning(newGoalPlaceNode.getLabel());
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
