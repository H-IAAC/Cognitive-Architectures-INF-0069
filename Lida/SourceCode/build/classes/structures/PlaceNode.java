/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package structures;

import edu.memphis.ccrg.lida.framework.shared.ElementFactory;
import edu.memphis.ccrg.lida.framework.shared.Node;
import edu.memphis.ccrg.lida.framework.shared.NodeImpl;
import edu.memphis.ccrg.lida.framework.tasks.TaskManager;
import edu.memphis.ccrg.lida.pam.PamNodeImpl;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author bruno
 */
public class PlaceNode extends NodeImpl {
    
    private static final Logger logger = Logger.getLogger(PlaceNode.class.getCanonicalName());
    
    private Position pos;
    
    public PlaceNode(){
        super();
        pos = new Position();
        ElementFactory fac = ElementFactory.getInstance();
        //logger.warning(fac.containsStrategy("noDecay")?"Y":"N");
        this.setDecayStrategy(fac.getDecayStrategy("noDecay"));
    }
    
    public PlaceNode(NodeImpl pn){
        super(pn);
        pos = new Position();
        ElementFactory fac = ElementFactory.getInstance();
        //logger.warning(fac.containsStrategy("noDecay")?"Y":"N");
        this.setDecayStrategy(fac.getDecayStrategy("noDecay"));
    }
    
    @Override
    public void updateNodeValues(Node n) {
        if (n instanceof PlaceNode) {
            PlaceNode pn = (PlaceNode) n;
            super.updateNodeValues(n);
            this.pos.setPos(pn.getPos());
        } else {
            logger.log(Level.FINEST,
                    "Cannot set PamNodeImpl-specific values. Required: {1} \n Received: {2}",
                    new Object[] { TaskManager.getCurrentTick(),
                                    PamNodeImpl.class.getCanonicalName(), n });
        }
    }
        
    public Position getPos(){
        return this.pos;
    }
    
    public void setPos(int x, int y){
        pos.setX(x);
        pos.setY(y);
    }
    
    public boolean isOccupied(){
        return this.pos.occupied;
    }
    
    public void setOccupied(){
        this.pos.occupied = true;
    }
    
}
