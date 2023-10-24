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

package codelets.perception;



import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.Memory;
import br.unicamp.cst.core.entities.MemoryObject;
import br.unicamp.cst.representation.idea.Idea;
import java.util.Collections;
import memory.CreatureInnerSense;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import ws3dproxy.model.Thing;
import ws3dproxy.model.WorldPoint;

/**
 * @author klaus
 *
 */
public class ClosestFoodDetector extends Codelet {

	private Memory knownMO;
	private Memory closestFoodMO;
	private Memory innerSenseMO;
	
        private List<Thing> known;

	public ClosestFoodDetector() {
            this.name = "ClosestAppleDetector";
	}


	@Override
	public void accessMemoryObjects() {
		this.knownMO=(MemoryObject)this.getInput("KNOWN_FOODS");
		this.innerSenseMO=(MemoryObject)this.getInput("INNER");
		this.closestFoodMO=(MemoryObject)this.getOutput("CLOSEST_FOOD");	
	}
	@Override
	public void proc() {
                Thing closest_food=null;
                known = Collections.synchronizedList((List<Thing>) knownMO.getI());
                Idea cis = (Idea) innerSenseMO.getI();
                synchronized(known) {
		   if(known.size() != 0){
			//Iterate over objects in vision, looking for the closest apple
                        CopyOnWriteArrayList<Thing> myknown = new CopyOnWriteArrayList<>(known);
                        for (Thing t : myknown) {
				String objectName=t.getName();
				if(objectName.contains("PFood") || objectName.contains("NPFood")){ //Then, it is a food
                                        if(closest_food == null){    
                                                closest_food = t;
					}
                                        else {
                                                double px = ((WorldPoint) cis.get("position").getValue()).getX();
                                                double py = ((WorldPoint) cis.get("position").getValue()).getY();
						double Dnew = calculateDistance(t.getX1(), t.getY1(), px, py);
                                                double Dclosest= calculateDistance(closest_food.getX1(), closest_food.getY1(), px, py);
						if(Dnew<Dclosest){
                                                        closest_food = t;
						}
					}
				}
			}
                        
                        if(closest_food!=null){    
				if(closestFoodMO.getI() == null || !closestFoodMO.getI().equals(closest_food)){
                                      closestFoodMO.setI(closest_food);
				}
				
			}else{
				//couldn't find any nearby apples
                                closest_food = null;
                                closestFoodMO.setI(closest_food);
			}
		   }
                   else  { // if there are no known apples closest_apple must be null
                        closest_food = null;
                        closestFoodMO.setI(closest_food);
		   }
                }
	}//end proc

@Override
        public void calculateActivation() {
        
        }
        
        private double calculateDistance(double x1, double y1, double x2, double y2) {
            return(Math.sqrt(Math.pow(x1-x2, 2)+Math.pow(y1-y2, 2)));
        }

}
