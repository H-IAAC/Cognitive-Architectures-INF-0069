package modules;

import edu.memphis.ccrg.lida.environment.EnvironmentImpl;
import edu.memphis.ccrg.lida.framework.initialization.GlobalInitializer;
import edu.memphis.ccrg.lida.framework.tasks.FrameworkTaskImpl;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import ws3dproxy.CommandExecException;
import ws3dproxy.CommandUtility;
import ws3dproxy.WS3DProxy;
import ws3dproxy.model.Creature;
import ws3dproxy.model.Leaflet;
import ws3dproxy.model.Thing;
import ws3dproxy.model.World;
import ws3dproxy.model.WorldPoint;
import ws3dproxy.util.Constants;

public class Environment extends EnvironmentImpl {

    private static final Logger logger = Logger.getLogger(Environment.class.getCanonicalName());
    private static final GlobalInitializer initializer = GlobalInitializer.getInstance();
    private static final int DEFAULT_TICKS_PER_RUN = 100;
    private int ticksPerRun;
    private WS3DProxy proxy;
    private Creature creature;
    private Thing food;
    private Thing jewel;
    private Thing brick;
    private WorldPoint body;
    private List<Thing> thingAhead;
    private Thing leafletJewel;
    private String currentAction;   

    public String getCurrentAction() {
        return currentAction;
    }
    
    public Environment() {
        this.ticksPerRun = DEFAULT_TICKS_PER_RUN;
        this.proxy = new WS3DProxy();
        this.creature = null;
        this.food = null;
        this.jewel = null;
        this.brick = null;
        this.body = null;
        this.thingAhead = new ArrayList<>();
        this.leafletJewel = null;
        this.currentAction = "rotate";
    }

    @Override
    public void init() {
        super.init();
        ticksPerRun = (Integer) getParam("environment.ticksPerRun", DEFAULT_TICKS_PER_RUN);
        taskSpawner.addTask(new BackgroundTask(ticksPerRun));
        
        try {
            System.out.println("Reseting the WS3D World ...");
            proxy.getWorld().reset();
            creature = proxy.createCreature(5, 5, 0);
            creature.start();
            System.out.println("Starting the WS3D Resource Generator ... ");
            generateMaze(proxy.getWorld());
            World.grow(2);
            Thread.sleep(4000);
            creature.updateState();
            System.out.println("DemoLIDA has started...");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class BackgroundTask extends FrameworkTaskImpl {

        public BackgroundTask(int ticksPerRun) {
            super(ticksPerRun);
        }

        @Override
        protected void runThisFrameworkTask() {
            updateEnvironment();
            performAction(currentAction);
        }
    }

    @Override
    public void resetState() {
        currentAction = "rotate";
    }

    @Override
    public Object getState(Map<String, ?> params) {
        Object requestedObject = null;
        String mode = (String) params.get("mode");
//        for (String k : params.keySet()){
//            logger.warning(k);
//        }
        //logger.warning("Request: " + mode);
        switch (mode) {
            case "food":
                requestedObject = food;
                break;
            case "jewel":
                requestedObject = jewel;
                break;
            case "thingAhead":
                requestedObject = thingAhead;
                break;
            case "leafletJewel":
                requestedObject = leafletJewel;
                break;
            case "brick":
                requestedObject = brick;
                break;
            case "body":
                requestedObject = body;
                break;
            default:
                break;
        }
        return requestedObject;
    }

    
    public void updateEnvironment() {
        creature.updateState();
        food = null;
        jewel = null;
        brick = null;
        leafletJewel = null;
        body = creature.getPosition();
        thingAhead.clear();
        
        List<Thing> vision = creature.getThingsInVision();
        Collections.shuffle(vision);
        for (Thing thing : vision) {
            if (creature.calculateDistanceTo(thing) <= Constants.OFFSET && thing.getCategory() != Constants.categoryBRICK) {
                // Identifica o objeto proximo
                thingAhead.add(thing);
                break;
            } else if (thing.getCategory() == Constants.categoryJEWEL 
                        && (creature.calculateDistanceTo(thing) <= 100 
                            || Math.abs(creature.calculateAngleTowards(thing)) < 0.01)) {
                
                if (leafletJewel == null) {
                    // Identifica se a joia esta no leaflet
                    for(Leaflet leaflet: creature.getLeaflets()){
                        if (leaflet.ifInLeaflet(thing.getMaterial().getColorName()) &&
                                leaflet.getTotalNumberOfType(thing.getMaterial().getColorName()) > leaflet.getCollectedNumberOfType(thing.getMaterial().getColorName())){
                            leafletJewel = thing;
                            break;
                        }
                    }
                } else {
                    // Identifica a joia que nao esta no leaflet
                    jewel = thing;
                }
            } else if (food == null && creature.getFuel() <= 500.0
                        && (thing.getCategory() == Constants.categoryFOOD
                        || thing.getCategory() == Constants.categoryPFOOD
                        || thing.getCategory() == Constants.categoryNPFOOD)
                        && (creature.calculateDistanceTo(thing) <= 100 
                            || Math.abs(creature.calculateAngleTowards(thing)) < 0.01)) {
                
                    // Identifica qualquer tipo de comida
                    food = thing;
            } else if (thing.getCategory() == Constants.categoryBRICK){
                brick = thing;
            }
           
        }
    }
    
    
    
    @Override
    public void processAction(Object action) {
        String actionName = (String) action;
        currentAction = actionName.substring(actionName.indexOf(".") + 1);
    }

    private void performAction(String currentAction) {
        double speed = 1.0;
        try {
            //System.out.println("Action: "+currentAction);
            switch (currentAction) {
                case "rotate":
                    creature.rotate(1.0);
                    //CommandUtility.sendSetTurn(creature.getIndex(), -1.0, -1.0, 3.0);
                    break;
                case "gotoFood":
                    if (food != null) 
                        creature.moveto(speed, food.getX1(), food.getY1());
                        //CommandUtility.sendGoTo(creature.getIndex(), 3.0, 3.0, food.getX1(), food.getY1());
                    break;
                case "gotoJewel":
                    if (leafletJewel != null)
                        creature.moveto(speed, leafletJewel.getX1(), leafletJewel.getY1());
                        //CommandUtility.sendGoTo(creature.getIndex(), 3.0, 3.0, leafletJewel.getX1(), leafletJewel.getY1());
                    break;                    
                case "get":
                    creature.move(0.0, 0.0, 0.0);
                    //CommandUtility.sendSetTurn(creature.getIndex(), 0.0, 0.0, 0.0);
                    if (thingAhead != null) {
                        for (Thing thing : thingAhead) {
                            if (thing.getCategory() == Constants.categoryJEWEL) {
                                creature.putInSack(thing.getName());
                            } else if (thing.getCategory() == Constants.categoryFOOD || thing.getCategory() == Constants.categoryNPFOOD || thing.getCategory() == Constants.categoryPFOOD) {
                                creature.eatIt(thing.getName());
                            }
                        }
                    }
                    this.resetState();
                    break;
                case "explore":
                    String moveDir = (String) initializer.getAttribute("moveDir");
                    //logger.warning(moveDir);
                    int py = Integer.parseInt(moveDir.replace("pos", "").split("_")[0]);
                    int px = Integer.parseInt(moveDir.replace("pos", "").split("_")[1]);
                    creature.moveto(speed, px*50, py*50);
                    
                    break;   
                default:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    //Gera um labirinto por divis�o recursiva
    private void generateMaze(World world){
        
        int worldH = world.getEnvironmentHeight();
        int worldW = world.getEnvironmentWidth();
        
        int ratio = 50;
        
        int h = worldH / ratio;
        int w = worldW / ratio;
        //logger.info(String.format("World grid size (x/y): %d/%d ", h, w));
        //Mapa em grid do ambiente indicando locais com parede (c�lula ocupada)
        boolean[][] walls = new boolean[h][w];
        
        walls = divideChambers(walls, 0, 0, h, w);
        
        for (int i=0; i<h; i++){
            for (int j=0; j<w; j++){
                System.out.print(walls[i][j] ? "x" : " ");
            }
            System.out.println("");
        }
        for (int i=1; i<h-1; i++){
            for (int j=1; j<w-1; j++){
                if (walls[i][j]){
//                    insertWall(world, i, j, i, j+1, ratio);                    
                    //Up
                    if (walls[i][j+1]){
                        insertWall(world, i, j, i, j+1, ratio);
                    }
//                    //Down
//                    if (walls[i][j-1]){
//                        insertWall(world, i, j, i, j-1, ratio);
//                    }
//                    //Left
//                    if (walls[i-1][j]){
//                        insertWall(world, i-1, j, i, j, ratio);
//                    }
                    //Rigth
                    if (walls[i+1][j]){
                        insertWall(world, i+1, j, i, j, ratio);
                    }
                    
                    
                }
            }
        }
        
        
    }

    private boolean[][] divideChambers(boolean[][] walls, int startH, int startW, int endH, int endW) {
        Random rnd = new Random();
        
        if ((endH - startH) >4 && (endW - startW) >4 ){
        
            //Intersec��o das duas linhas que dividem o ambiente em 4 c�maras
            //Garantindo que nenhuma parede estr� na borda do ambiente
            int divH = rnd.nextInt(endH - startH - 3) + startH + 2;
            int divW = rnd.nextInt(endW - startW - 3) + startW + 2;

            //Cria��o das paredes definidas pelas duas linhas
            for (int i=startH; i<endH; i++){
                walls[i][divW] = true;
            }
            for (int i=startW; i<endW; i++){
                walls[divH][i] = true;
            }

            //Cria uma abertura em 3 das quatro paredes formadas
            int noOpenning = rnd.nextInt(4);
            int i1 = rnd.nextInt(divH - startH) + startH;
            int j1 = rnd.nextInt(divW - startW) + startW;
            int i2 = rnd.nextInt(endH - divH - 1) + divH + 1;
            int j2 = rnd.nextInt(endW - divW - 1) + divW + 1;
            if (noOpenning != 0){
                walls[i1][divW] = false;
            }
            if (noOpenning != 1){
                walls[divH][j1] = false;
            }
            if (noOpenning != 2){
                walls[i2][divW] = false;
            }
            if (noOpenning != 3){
                walls[divH][j2] = false;
            }


            //Subdivis�o das 4 c�meras geradas
            walls = divideChambers(walls, startH, startW, divH, divW);
            walls = divideChambers(walls, divH, divW, endH, endW);
            walls = divideChambers(walls, startH, divW, divH, endW);
            walls = divideChambers(walls, divH, startW, endH, divW);
            
            //Reabertura
            if (noOpenning != 0){
                walls[i1][divW+1] = false;
                walls[i1][divW-1] = false;
            }
            if (noOpenning != 1){
                walls[divH+1][j1] = false;
                walls[divH-1][j1] = false;
            }
            if (noOpenning != 2){
                walls[i2][divW+1] = false;
                walls[i2][divW-1] = false;
            }
            if (noOpenning != 3){
                walls[divH+1][j2] = false;
                walls[divH-1][j2] = false;
            }
            
        } else {
            if ((endH - startH) == 4 && (endW - startW) > 4){
                int divW = rnd.nextInt(endW - startW - 2) + startW + 2;
                for (int i=startW; i<endW; i++){
                    walls[startH+2][i] = true;
                }
                int j1 = rnd.nextInt(divW - startW) + startW;
                walls[startH+2][j1] = false;

            } else {
                if ((endH - startH) > 4 && (endW - startW) == 4) {
                    int divH = rnd.nextInt(endH - startH - 2) + startH + 1;
                    for (int i = startH; i < endH; i++) {
                        walls[i][startH + 2] = true;
                    }
                    int j1 = rnd.nextInt(divH - startH) + startH;
                    walls[j1][startH + 2] = false;
                }
            }
        }
        return walls;
    }
    
    private void insertWall(World world, int y1, int x1, int y2, int x2, int ratio){
        int w = 5;
        try {
            world.createBrick(5, x1*ratio-w, y1*ratio-w, x1*ratio+w, y1*ratio+w);
            world.createBrick(5, x2*ratio-w, y2*ratio-w, x2*ratio+w, y2*ratio+w);
            world.createBrick(5, x1*ratio-w, y1*ratio-w, x2*ratio+w, y2*ratio+w);

        } catch (CommandExecException ex) {
            Logger.getLogger(Environment.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
