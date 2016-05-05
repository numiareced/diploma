package com.hse12pi.environment;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.hse12pi.decisiontree.BadDecisionException;
import com.hse12pi.decisiontree.DecisionTree;
import com.hse12pi.decisiontree.UnknownDecisionException;

public class TreeDrivenAgent extends Agent {
	
private static Random random = new Random();

	protected static final double maxAgentsDistance = 5;
	private double rotation;
	private double initialAngle = 0; 
	private double accSpeed = 0; 
	private boolean needToSpeedUp = false;
	private double angle; 
	private int eatenFoodCount = 0; 
	DecisionTree tree;
	public TreeDrivenAgent(double x, double y, double angle) {
 		super(x, y, angle);
		tree = makeFoodTree();
		tree.compile();
	}
public synchronized void interact(AgentsEnvironment env) {
		Map<String, Double> output = new HashMap<String, Double>();
		try {
			output = decide(env);
			double deltaAngle = output.get("Angle");
			double deltaSpeed = output.get("Speed");
			//System.out.println("speed is:" + deltaSpeed);

			deltaSpeed = this.avoidNaNAndInfinity(deltaSpeed);
			deltaAngle = this.avoidNaNAndInfinity(deltaAngle);

			double newSpeed = this.normalizeSpeed(this.getSpeed() + deltaSpeed);
			double newAngle = this.getAngle() + this.normalizeDeltaAngle(deltaAngle);
			System.out.println("speed is:" + newSpeed );
			this.setAngle(newAngle);
			this.setSpeed(newSpeed);

			this.move();
		} catch (BadDecisionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

public Map<String,Double> decide(AgentsEnvironment environment) throws BadDecisionException {
	
	Food nearestFood = null;
	double nearestFoodDist = Double.MAX_VALUE;
	Agent nearestAgent = null;
	double nearestAgentDist = maxAgentsDistance;
	Map<String, String> inputForDecision = new HashMap<String, String>();
	 Map<String,Double> output = new HashMap<String, Double>();
	 double calculatedSpeed = 0;
	 double calculatedAngle = 0; 
	
	for (Food currFood : environment.filter(Food.class)) {
		// agent can see only ahead
		if (this.inSight(currFood)) {
			double currFoodDist = this.distanceTo(currFood);
			if ((nearestFood == null) || (currFoodDist <= nearestFoodDist)) {
				nearestFood = currFood;
				nearestFoodDist = currFoodDist;
			}
		}
	}
	if (nearestFood != null){
		for (Agent currAgent : environment.filter(Agent.class)) {
			// agent can see only ahead
			if ((this != currAgent) && (this.inSight(currAgent))) {
				double currAgentDist = this.distanceTo(currAgent);
				if (currAgentDist <= nearestAgentDist) {
					nearestAgent = currAgent;
					nearestAgentDist = currAgentDist;
				}
			}
		}
		if (nearestAgent !=null){
			if (canSee(nearestAgent,nearestFood)){
					if (canReachFood(this, nearestAgent, nearestFood)){
						inputForDecision.put("Food", "true");
						inputForDecision.put("Agent", "true");
						inputForDecision.put("seeFood", "true");
						inputForDecision.put("canReach", "true");
						if (canSpeedUp(this, nearestAgent, nearestFood)){
							inputForDecision.put("CanSpeedUp", "true");
							needToSpeedUp = true; 
							
					    }
						else {
							inputForDecision.put("CanSpeedUp", "false");
							needToSpeedUp = false; 
						}
					}
					else {
						inputForDecision.put("Food", "true");
						inputForDecision.put("Agent", "true");
						inputForDecision.put("seeFood", "true");
						inputForDecision.put("canReach", "false");
						inputForDecision.put("CanSpeedUp", "false");
					}
				}	
			else {
				inputForDecision.put("Food", "true");
				inputForDecision.put("Agent", "true");
				inputForDecision.put("seeFood", "false");
				inputForDecision.put("canReach", "false");
				inputForDecision.put("CanSpeedUp", "false");
				
			}
		}
		else {
			System.out.println("no agent");
			inputForDecision.put("Food", "true");
			inputForDecision.put("Agent", "false");
			inputForDecision.put("seeFood", "false");
			inputForDecision.put("canReach", "false");
			inputForDecision.put("CanSpeedUp", "false");
		}
	}
	else {
		inputForDecision.put("Food", "false");
		inputForDecision.put("Agent", "false");
		inputForDecision.put("seeFood", "false");
		inputForDecision.put("canReach", "false");
		inputForDecision.put("CanSpeedUp", "false");
		System.out.println("no food");
	}
	try {
	
	 boolean decision = tree.apply(inputForDecision);
	 if (decision){
		 System.out.println("going to the food");
			double rx = this.getRx();
			double ry = this.getRy();

			double x = this.getX();
			double y = this.getY();
		 //eat food
		 double foodDirectionVectorX = nearestFood.getX() - x;
	     double foodDirectionVectorY = nearestFood.getY() - y;
	     calculatedSpeed = random.nextDouble()* 4;
      // left/right cos
			double foodDirectionCosTeta =
					Math.signum(this.pseudoScalarProduct(rx, ry, foodDirectionVectorX, foodDirectionVectorY))
							* this.cosTeta(rx, ry, foodDirectionVectorX, foodDirectionVectorY);
			calculatedAngle = foodDirectionCosTeta;
		 if (needToSpeedUp){
			 calculatedSpeed = accSpeed;
		 }
		 
		 output.put("Speed", calculatedSpeed);
		 output.put("Angle", calculatedAngle);
		 return output;
	 }
	 else {
		 System.out.println("finding another");
		 //no food, trying to find new 
		 //calculatedSpeed = randomspeed;
		 //calculatedAngle = randomangle; 
		 Random rand = new Random(); 
		 if (angle != 360){
			 angle = angle + 10; 
			 calculatedSpeed = 0; 
		 }
		 else {
			 //already turned around but no food 
			 //moving random direction
			 angle =  random.nextDouble() * 2 * Math.PI;
			 calculatedSpeed = random.nextDouble()* 4;
		 }
		 
		 output.put("Speed", calculatedSpeed);
		 output.put("Angle", calculatedAngle);
		 return output;
	 }
	}
	catch (BadDecisionException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	return output;
}

private boolean canSee(Agent agent, Food food){
	double crossProduct = cosTeta(agent.getRx(), agent.getRy(), food.getX() - agent.getX(), food.getY() - agent.getY());
	return (crossProduct > 0);
}

private boolean canReachFood(TreeDrivenAgent currentAgent, Agent nearestAgent, Food food){
 if ( (currentAgent.distanceTo(food)/currentAgent.getSpeed()) <= (nearestAgent.distanceTo(food)/nearestAgent.getSpeed()) ){
	 //if your time is better, other agent cant reach food
	 return false; 
 }
 else {
	 return true; 
 }
}

private boolean canSpeedUp( TreeDrivenAgent currentAgent, Agent nearestAgent, Food food ){
	int acceleration = 1; 
	Random rand = new Random(); 
	double newSpeed = currentAgent.getSpeed() + acceleration * rand.nextDouble(); 
	if( ( newSpeed <= 4 ) ) {
		if (currentAgent.distanceTo(food)/newSpeed <= (nearestAgent.distanceTo(food)/nearestAgent.getSpeed())){
			accSpeed = newSpeed; 
			return true; 
		}
	}
	return false; 
}


	private DecisionTree makeOne() {
	    return new DecisionTree();
	  }
	
	public void makeDecisionTree(){
		
	}
	
 private  DecisionTree makeFoodTree() {
	 try {
	    	return makeOne().setAttributes(new String[]{"Food", "Agent", "seeFood", "canReach", "CanSpeedUp"})
                         .addExample(   new String[]{"true", "True",  "True",  "False", "False"}, true)
                         .addExample(   new String[]{"true", "true",  "true",  "true", "true" }, true)
                         .addExample(   new String[]{"true", "true",  "true",  "true", "false" }, false)
                         .addExample(   new String[]{"true" , "true",  "true",  "true", "true"}, true)
                         .addExample(   new String[]{"true" ,"true",  "False",  "false", "false"}, true)
                         .addExample(   new String[]{"true", "false", "false",  "false",  "false"}, true)
   				         .addExample(   new String[]{"false", "false","false","false","false"}, false);
	    } catch ( UnknownDecisionException e ) {
	    	System.out.println("unknown decision exception");
	      return makeOne(); // this is here to shut up compiler.
	    }
	  }

}
