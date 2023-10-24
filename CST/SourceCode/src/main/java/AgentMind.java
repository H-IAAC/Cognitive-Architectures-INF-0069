/*****************************************************************************
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
 *****************************************************************************/

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.Memory;
import br.unicamp.cst.core.entities.MemoryContainer;
import br.unicamp.cst.core.entities.Mind;
import br.unicamp.cst.representation.idea.Idea;
import codelets.behaviors.DeliverLeaflet;
import codelets.behaviors.EatClosestFood;
import codelets.behaviors.Forage;
import codelets.behaviors.GoToClosestFood;
import codelets.behaviors.GoToClosestJewel;
import codelets.behaviors.PickClosestJewel;
import codelets.motor.HandsActionCodelet;
import codelets.motor.LegsActionCodelet;
import codelets.perception.BrickDetector;
import codelets.perception.FoodDetector;
import codelets.perception.ClosestFoodDetector;
import codelets.perception.ClosestJewelDetector;
import codelets.perception.ClosestLefletJewelDetector;
import codelets.perception.JewelDetector;
import codelets.perception.MapStructuringCodelet;
import codelets.sensors.InnerSense;
import codelets.sensors.LeafletSense;
import codelets.sensors.Vision;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import memory.CreatureInnerSense;
import ws3dproxy.model.Thing;
import ws3dproxy.model.WorldPoint;

/**
 *
 * @author rgudwin
 */
public class AgentMind extends Mind {
    
    private static double creatureBasicSpeed=0.8;
    private static int reachDistance=45;
    private static int minFuel=400;
    public ArrayList<Codelet> behavioralCodelets = new ArrayList<Codelet>();
    
    public AgentMind(Environment env) {
                super();
                
                // Create CodeletGroups and MemoryGroups for organizing Codelets and Memories
                createCodeletGroup("Sensory");
                createCodeletGroup("Motor");
                createCodeletGroup("Perception");
                createCodeletGroup("Behavioral");
                createMemoryGroup("Sensory");
                createMemoryGroup("Motor");
                createMemoryGroup("Working");
                
                // Declare Memory Objects
	        MemoryContainer legsMO;  // This Memory is going to be a MemoryContainer
	        MemoryContainer handsMO;
                Memory visionMO;
                Memory innerSenseMO;
                Memory closestFoodMO;
                Memory knownFoodsMO;
                Memory leafletsMO;
                Memory knownJewelsMO;
                Memory closestJewelMO;
                Memory closestLeafletJewelMO;
                Memory knownBricksMO;
                Memory mapMO;
                
                //Initialize Memory Objects
                legsMO=createMemoryContainer("LEGS");
                registerMemory(legsMO,"Motor");
		handsMO=createMemoryContainer("HANDS");
                registerMemory(handsMO,"Motor");
                List<Thing> vision_list = Collections.synchronizedList(new ArrayList<Thing>());
		visionMO=createMemoryObject("VISION",vision_list);
                registerMemory(visionMO,"Sensory");
                Idea cis = initializeInnerSenseIdea();
		innerSenseMO=createMemoryObject("INNER", cis);
                registerMemory(innerSenseMO,"Sensory");
                Thing closestFood = null;
                closestFoodMO=createMemoryObject("CLOSEST_FOOD", closestFood);
                registerMemory(closestFoodMO,"Working");
                List<Thing> knownFoods = Collections.synchronizedList(new ArrayList<Thing>());
                knownFoodsMO=createMemoryObject("KNOWN_FOODS", knownFoods);
                registerMemory(knownFoodsMO,"Working");
                leafletsMO = createMemoryObject("LEAFLETS", new Idea());
                registerMemory(leafletsMO, "Sensory");
                List<Thing> knowJewels = Collections.synchronizedList(new ArrayList<Thing>());
                knownJewelsMO=createMemoryObject("KNOWN_JEWELS", knowJewels);
                registerMemory(knownJewelsMO,"Working");
                Thing closestJewel = null;
                closestJewelMO=createMemoryObject("CLOSEST_JEWEL", closestJewel);
                registerMemory(closestJewelMO,"Working");
                Thing closestLeafletJewel = null;
                closestLeafletJewelMO=createMemoryObject("CLOSEST_LEAFLET_JEWEL", closestLeafletJewel);
                registerMemory(closestLeafletJewelMO,"Working");
                List<Thing> knownBricks = Collections.synchronizedList(new ArrayList<Thing>());
                knownBricksMO=createMemoryObject("KNOWN_BRICKS", knownBricks);
                registerMemory(knownBricksMO,"Working");
                int[][] map = new int[16][21];
                mapMO = createMemoryObject("MAP", map);
                registerMemory(mapMO, "Working");
                
 		// Create Sensor Codelets	
		Codelet vision=new Vision(env.c);
		vision.addOutput(visionMO);
                insertCodelet(vision); //Creates a vision sensor
                registerCodelet(vision,"Sensory");
		
		Codelet innerSense=new InnerSense(env.c);
		innerSense.addOutput(innerSenseMO);
                insertCodelet(innerSense); //A sensor for the inner state of the creature
                registerCodelet(innerSense,"Sensory");
		
		Codelet leafletSense=new LeafletSense(env.c);
		leafletSense.addOutput(leafletsMO);
                insertCodelet(leafletSense);
                registerCodelet(leafletSense,"Sensory");
		
		// Create Actuator Codelets
		Codelet legs=new LegsActionCodelet(env.c);
		legs.addInput(legsMO);
                insertCodelet(legs);
                registerCodelet(legs,"Motor");

		Codelet hands=new HandsActionCodelet(env.c);
		hands.addInput(handsMO);
                insertCodelet(hands);
                registerCodelet(hands,"Motor");
		
		// Create Perception Codelets
                Codelet ad = new FoodDetector();
                ad.addInput(visionMO);
                ad.addOutput(knownFoodsMO);
                insertCodelet(ad);
                registerCodelet(ad,"Perception");
                
		Codelet closestFoodDetector = new ClosestFoodDetector();
		closestFoodDetector.addInput(knownFoodsMO);
		closestFoodDetector.addInput(innerSenseMO);
		closestFoodDetector.addOutput(closestFoodMO);
                insertCodelet(closestFoodDetector);
                registerCodelet(closestFoodDetector,"Perception");
                
                Codelet jewelDetector = new JewelDetector();
                jewelDetector.addInput(visionMO);
                jewelDetector.addOutput(knownJewelsMO);
                insertCodelet(jewelDetector);
                registerCodelet(jewelDetector,"Perception");
                
		Codelet closestJewelDetector = new ClosestJewelDetector();
		closestJewelDetector.addInput(knownJewelsMO);
		closestJewelDetector.addInput(innerSenseMO);
		closestJewelDetector.addOutput(closestJewelMO);
                insertCodelet(closestJewelDetector);
                registerCodelet(closestJewelDetector,"Perception");
                
		Codelet closestLeafletJewelDetector = new ClosestLefletJewelDetector();
		closestLeafletJewelDetector.addInput(knownJewelsMO);
		closestLeafletJewelDetector.addInput(innerSenseMO);
		closestLeafletJewelDetector.addInput(leafletsMO);
		closestLeafletJewelDetector.addOutput(closestLeafletJewelMO);
                insertCodelet(closestLeafletJewelDetector);
                registerCodelet(closestLeafletJewelDetector,"Perception");
                
                Codelet brickDetector = new BrickDetector();
                brickDetector.addInput(visionMO);
                brickDetector.addOutput(knownBricksMO);
                insertCodelet(brickDetector);
                registerCodelet(brickDetector,"Perception");
                
                Codelet mapBuilding = new MapStructuringCodelet();
                mapBuilding.addInput(knownBricksMO);
                mapBuilding.addInput(innerSenseMO);
                mapBuilding.addOutput(mapMO);
                insertCodelet(mapBuilding);
                registerCodelet(mapBuilding, "Perception");
                //mapBuilding.setProfiling(true);
		
		// Create Behavior Codelets
		Codelet goToClosestApple = new GoToClosestFood(creatureBasicSpeed,reachDistance, minFuel);
		goToClosestApple.addInput(closestFoodMO);
		goToClosestApple.addInput(innerSenseMO);
		goToClosestApple.addInput(mapMO);
		goToClosestApple.addOutput(legsMO);
                insertCodelet(goToClosestApple);
                registerCodelet(goToClosestApple,"Behavioral");
                
                behavioralCodelets.add(goToClosestApple);
                
		Codelet goToClosestJewel = new GoToClosestJewel(creatureBasicSpeed,reachDistance, minFuel);
		goToClosestJewel.addInput(closestLeafletJewelMO);
		goToClosestJewel.addInput(innerSenseMO);
		goToClosestJewel.addInput(leafletsMO);
		goToClosestJewel.addInput(mapMO);
		goToClosestJewel.addOutput(legsMO);
                insertCodelet(goToClosestJewel);
                registerCodelet(goToClosestJewel,"Behavioral");
                //goToClosestJewel.setProfiling(true);
                
                behavioralCodelets.add(goToClosestJewel);
		
		Codelet eatApple=new EatClosestFood(reachDistance);
		eatApple.addInput(closestFoodMO);
		eatApple.addInput(innerSenseMO);
		eatApple.addOutput(handsMO);
                eatApple.addOutput(knownFoodsMO);
                insertCodelet(eatApple);
                registerCodelet(eatApple,"Behavioral");
                behavioralCodelets.add(eatApple);
		
		Codelet pickUpJewel=new PickClosestJewel(reachDistance);
		pickUpJewel.addInput(closestJewelMO);
		pickUpJewel.addInput(innerSenseMO);
		pickUpJewel.addOutput(handsMO);
                pickUpJewel.addOutput(knownJewelsMO);
                insertCodelet(pickUpJewel);
                registerCodelet(pickUpJewel,"Behavioral");
                behavioralCodelets.add(pickUpJewel);
                
                Codelet forage=new Forage(creatureBasicSpeed);
		forage.addInput(innerSenseMO);
		forage.addInput(mapMO);
                forage.addOutput(legsMO);
                insertCodelet(forage);
                registerCodelet(forage,"Behavioral");
                behavioralCodelets.add(forage);
                //forage.setProfiling(true);
                
                Codelet deliver = new DeliverLeaflet();
                deliver.addInput(leafletsMO);
                deliver.addOutput(handsMO);
                insertCodelet(deliver);
                registerCodelet(deliver, "Behavioral");
                behavioralCodelets.add(deliver);
                
                // sets a time step for running the codelets to avoid heating too much your machine
                for (Codelet c : this.getCodeRack().getAllCodelets())
                    c.setTimeStep(200);
                
//                innerSense.setProfiling(true);
//                ad.setProfiling(true);
		
		// Start Cognitive Cycle
		start(); 
    }             
    
    private Idea initializeInnerSenseIdea(){
        Idea innerSense = new Idea("InnerSense");
        
        innerSense.add(new Idea("position", new WorldPoint(), 1));
        innerSense.add(new Idea("pitch", null, 1));
        innerSense.add(new Idea("fuel", null, 1));
        innerSense.add(new Idea("fov", null, 1));
        
        return innerSense;
    }
    
}
