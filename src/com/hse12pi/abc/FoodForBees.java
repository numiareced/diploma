package com.hse12pi.abc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.hse12pi.environment.ABCDrivenAgent;
import com.hse12pi.environment.Agent;
import com.hse12pi.environment.AgentsEnvironment;
import com.hse12pi.environment.Food;
import com.hse12pi.environment.TreeDrivenAgent;

public class FoodForBees implements Comparable<FoodForBees> {

	// solutions
	// all food on the map + nearest enemy position : x, y, cos
	// getters, setters for all parameters + functions to calculate distances
	// and positions of nearest enemies
	protected static final double maxAgentsDistance = 5;
	public int FOOD_SIZE;
	public Map<String, Double> direction = new HashMap<String, Double>();
	public ArrayList<Food> nectar;
	public ArrayList<ABCDrivenAgent> enemies;
	public ABCDrivenAgent currentAgent;
	private int trials;
	private double fitness;
	private double selectionProbability;
	private int conflicts; // conflicts
	private double speed;
	private double angle; 
	private AgentsEnvironment environment;
	private Food currFood; 
	private ABCDrivenAgent nearestEnemy; 

	public FoodForBees(int size, AgentsEnvironment e, ABCDrivenAgent currAgent) {
		this.environment = e;
		this.currentAgent = currAgent;
		this.FOOD_SIZE = size;
		this.nectar = new ArrayList<Food>();
		this.conflicts = 0;
		this.trials = 0;
		this.fitness = 0.0;
		this.selectionProbability = 0.0;
		this.speed = 0.0;
		this.angle = 0.0; 
		currFood = null; 
		nearestEnemy = null; 
	}

	@Override
	public int compareTo(FoodForBees o) {
		return this.conflicts - o.getConflicts();
	}
	
	public Food getFood() {
		return this.currFood;
	}

	public void setFood(Food f) {
		this.currFood = f;
	}
	
	public ABCDrivenAgent getNearestEnemy() {
		return this.nearestEnemy;
	}

	public void setNearestEnemy(ABCDrivenAgent en) {
		this.nearestEnemy = en;
	}

	public int getConflicts() {
		return this.conflicts;
	}

	public void setConflicts(int t) {
		this.conflicts = t;
	}
	
	public ABCDrivenAgent getCurrAgent() {
		return this.currentAgent;
	}

	public void setCurrAgent(ABCDrivenAgent t) {
		this.currentAgent = t;
	}
	
	public AgentsEnvironment getEnvironment() {
		return this.environment;
	}

	public void setEnvironment(AgentsEnvironment env) {
		this.environment = env;
	}
	
	
	
	
	public HashMap<String, Double> getDecision(){
		HashMap<String, Double> output = new HashMap<String, Double>();
		
		speed = currentAgent.getSpeed();
		angle = currentAgent.getAngle();
		//System.out.println("speed & angle is : " + speed + " " +  angle);
		output.put("Speed", speed);
		output.put("Angle", angle);
		return output; 
		
	}

	 public void initNectar(AgentsEnvironment e) {
	 for (Food currFood : environment.filter(Food.class)) {
	 nectar.add(currFood);
	 }
	 }
	
	 public void initEnemies(AgentsEnvironment e) {
	 for (ABCDrivenAgent agent : environment.filter(ABCDrivenAgent.class)) {
	 // agent can see only ahead
	 if (agent != currentAgent) {
	 enemies.add(agent);
	 }
	 }
	 }

	public void calculateConflicts() {
		int currConflict = 0;
		if (!currentAgent.inSight(currFood)){
			currConflict+=3; 
		}else {
			if (nearestEnemy != null){
			if (nearestEnemy.inSight(currFood)){
				if (canReachFood(currentAgent, nearestEnemy, currFood)){
					currConflict+=2; 
				}
			}
			}
		}
		//System.out.println("conflict is:" + conflicts);
		this.conflicts = currConflict; 
	}

	private boolean canSee(Agent agent, Food food) {
		double crossProduct = cosTeta(agent.getRx(), agent.getRy(), food.getX() - agent.getX(),
				food.getY() - agent.getY());
		return (crossProduct > 0);
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

	private boolean canReachFood(ABCDrivenAgent currentAgent, ABCDrivenAgent nearestAgent, Food food) {
		if ((currentAgent.distanceTo(food) / currentAgent.getSpeed()) <= (nearestAgent.distanceTo(food)
				/ nearestAgent.getSpeed())) {
			// if your time is better, other agent cant reach food
			return false;
		} else {
			return true;
		}
	}

	public int getTrials() {
		return trials;
	}

	public void setTrials(int trials) {
		this.trials = trials;
	}

	public double getFitness() {
		return fitness;
	}

	public void setFitness(double mFitness) {
		this.fitness = mFitness;
	}

	public double getSelectionProbability() {
		return selectionProbability;
	}

	public void setSelectionProbability(double mSelectionProbability) {
		this.selectionProbability = mSelectionProbability;
	}
}
