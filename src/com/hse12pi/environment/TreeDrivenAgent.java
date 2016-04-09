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
	
	private static final double maxSpeed = 4;

	private static final double maxDeltaAngle = 1;

	protected static final double maxAgentsDistance = 5;

	private static final double AGENT = -10;

	private static final double EMPTY = 0;

	private static final double FOOD = 10;
	private double rotation;
	
	private double initialAngle = 0; 
	private boolean needToSpeedUp = false;
	DecisionTree tree;
	public TreeDrivenAgent(double x, double y, double angle) {
		super(x, y, angle);
		DecisionTree tree = makeFoodTree();
		tree.compile();
	}
public synchronized void interact(AgentsEnvironment env) {
		
		/*List<Double> nnInputs = this.createNnInputs(env);

		this.activateNeuralNetwork(nnInputs);

		int neuronsCount = this.brain.getNeuronsCount();
		double deltaAngle = this.brain.getAfterActivationSignal(neuronsCount - 2);
		
		double deltaSpeed = this.brain.getAfterActivationSignal(neuronsCount - 1);*/
		Map<String, Double> output = new HashMap<String, Double>();
		try {
			output = decide(env);
			double deltaAngle = output.get("Angle");
			double deltaSpeed = output.get("Speed");

			deltaSpeed = this.avoidNaNAndInfinity(deltaSpeed);
			deltaAngle = this.avoidNaNAndInfinity(deltaAngle);

			double newSpeed = this.normalizeSpeed(this.getSpeed() + deltaSpeed);
			double newAngle = this.getAngle() + this.normalizeDeltaAngle(deltaAngle);

			this.setAngle(newAngle);
			this.setSpeed(newSpeed);

			this.move();
		} catch (BadDecisionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

private double avoidNaNAndInfinity(double x) {
	if ((Double.isNaN(x)) || Double.isInfinite(x)) {
		x = 0;
	}
	return x;
}
protected boolean inSight(AbstractAgent agent) {
	double crossProduct = this.cosTeta(this.getRx(), this.getRy(), agent.getX() - this.getX(), agent.getY() - this.getY());
	return (crossProduct > 0);
}

protected double distanceTo(AbstractAgent agent) {
	return this.module(agent.getX() - this.getX(), agent.getY() - this.getY());
}

protected double cosTeta(double vx1, double vy1, double vx2, double vy2) {
	double v1 = this.module(vx1, vy1);
	double v2 = this.module(vx2, vy2);
	if (v1 == 0) {
		v1 = 1E-5;
	}
	if (v2 == 0) {
		v2 = 1E-5;
	}
	double ret = ((vx1 * vx2) + (vy1 * vy2)) / (v1 * v2);
	return ret;
}

protected double module(double vx1, double vy1) {
	return Math.sqrt((vx1 * vx1) + (vy1 * vy1));
}

protected double pseudoScalarProduct(double vx1, double vy1, double vx2, double vy2) {
	return (vx1 * vy2) - (vy1 * vx2);
}

private double normalizeSpeed(double speed) {
	double abs = Math.abs(speed);
	if (abs > maxSpeed) {
		double sign = Math.signum(speed);
		speed = sign * (abs - (Math.floor(abs / maxSpeed) * maxSpeed));
	}
	return speed;
}

private double normalizeDeltaAngle(double angle) {
	double abs = Math.abs(angle);
	if (abs > maxDeltaAngle) {
		double sign = Math.signum(angle);
		angle = sign * (abs - (Math.floor(abs / maxDeltaAngle) * maxDeltaAngle));
	}
	return angle;
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
				if (nearestAgent.getSpeed() > this.getSpeed() ) {
					if (canSpeedup((Agent)this, nearestAgent)){
						needToSpeedUp = true; 
						inputForDecision.put("Food", "true");
						inputForDecision.put("Agent", "true");
						inputForDecision.put("SeeFood", "true");
						inputForDecision.put("isFaster", "true");
						inputForDecision.put("CanSpeedUp", "true");
					}
					else {
						needToSpeedUp = false; 
						inputForDecision.put("Food", "true");
						inputForDecision.put("Agent", "true");
						inputForDecision.put("SeeFood", "true");
						inputForDecision.put("isFaster", "true");
						inputForDecision.put("CanSpeedUp", "false");
					}
				}
				else {
					inputForDecision.put("Food", "true");
					inputForDecision.put("Agent", "true");
					inputForDecision.put("SeeFood", "true");
					inputForDecision.put("isFaster", "false");
					inputForDecision.put("CanSpeedUp", "false");
				}
				
			}
			else {
				inputForDecision.put("Food", "true");
				inputForDecision.put("Agent", "true");
				inputForDecision.put("SeeFood", "false");
				inputForDecision.put("isFaster", "false");
				inputForDecision.put("CanSpeedUp", "false");
				
			}
		}
		else {
			inputForDecision.put("Food", "true");
			inputForDecision.put("Agent", "false");
			inputForDecision.put("SeeFood", "false");
			inputForDecision.put("isFaster", "false");
			inputForDecision.put("CanSpeedUp", "false");
		}
		
	}
	else {
		inputForDecision.put("Food", "false");
		inputForDecision.put("Agent", "false");
		inputForDecision.put("SeeFood", "false");
		inputForDecision.put("isFaster", "false");
		inputForDecision.put("CanSpeedUp", "false");
	}
	
	 boolean decision = tree.apply(inputForDecision);
	 if (decision){
			double rx = this.getRx();
			double ry = this.getRy();

			double x = this.getX();
			double y = this.getY();
		 //eat food
		 double foodDirectionVectorX = nearestFood.getX() - x;
	     double foodDirectionVectorY = nearestFood.getY() - y;
         // left/right cos
			double foodDirectionCosTeta =
					Math.signum(this.pseudoScalarProduct(rx, ry, foodDirectionVectorX, foodDirectionVectorY))
							* this.cosTeta(rx, ry, foodDirectionVectorX, foodDirectionVectorY);
			calculatedAngle = foodDirectionCosTeta;
		 if (needToSpeedUp){
			 calculatedSpeed = speedUp(this.getSpeed());
		 }
		 output.put("Speed", calculatedSpeed);
		 output.put("Angle", calculatedAngle);
		 return output;
	 }
	 else {
		 //no food, trying to find new 
		 //calculatedSpeed = randomspeed;
		 //calculatedAngle = randomangle; 
		 output.put("Speed", calculatedSpeed);
		 output.put("Angle", calculatedAngle);
		 return output;
	 }
	 
}

private boolean canSee(Agent agent, Food food){
	double crossProduct = cosTeta(agent.getRx(), agent.getRy(), food.getX() - agent.getX(), food.getY() - agent.getY());
	return (crossProduct > 0);
}

private boolean canSpeedup(Agent currentAgent, Agent nearestAgent){
	//if this.speed() != maxSpeed && nearestAgent.speed() != maxSpeed;
	// if this.speed() + acceleration > nearestAgent.speed() && <=maxSpeed
	return true;
	// else return false; 
}

private double speedUp(double currentSpeed){
	int acceleration = 3; 
	double randomValue = 0.1 ;//random 
	double newSpeed = currentSpeed + acceleration * randomValue; 
	return newSpeed; 
}

private Map<String,Double> calculateDirection(){
	
	return null;
	
}
	
	private DecisionTree makeOne() {
	    return new DecisionTree();
	  }
	
	public void makeDecisionTree(){
		
	}
	
 private  DecisionTree makeFoodTree() {
	    try {
	      return makeOne().setAttributes(new String[]{"Agent", "seeFood", "isFaster", "CanSpeedUp"})
	                      .addExample(   new String[]{"True",  "True",  "False", "False", "False"}, true)
	                      .addExample(   new String[]{"True",  "True",  "True", "True", "True" }, true)
	                      .addExample(   new String[]{"True",  "True",  "True", "False", "False" }, true)
	                      .addExample(   new String[]{"True",  "True",  "True", "True", "False" }, false)
	                      .addExample(   new String[]{"True",  "False",  "False", "False", "False" }, true)
          .addExample(   new String[]{"False",   "False"}, true);
	    } catch ( UnknownDecisionException e ) {
	    	System.out.println("unknown decision exception");
	      return makeOne(); // this is here to shut up compiler.
	    }
	  }

}
