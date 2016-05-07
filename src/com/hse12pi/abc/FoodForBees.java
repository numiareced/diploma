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

	private int trials;
	private double fitness;
	private double selectionProbability;
	private int conflicts; // conflicts
	private double speed;
	private AgentsEnvironment environment;
	public ABCDrivenAgent currentAgent;
	private Agent nearestEnemy;
	private Food currFood;
	private double foodCos;

	public FoodForBees(int size, AgentsEnvironment e, ABCDrivenAgent currAgent) {
		this.environment = e;
		this.currentAgent = currAgent;
		this.FOOD_SIZE = size;
		this.conflicts = 0;
		this.trials = 0;
		this.fitness = 0.0;
		this.selectionProbability = 0.0;
		this.speed = 0.0;
		this.foodCos = 0.0;
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

	public Agent getNearestEnemy() {
		return this.nearestEnemy;
	}

	public void setNearestEnemy(Agent en) {
		this.nearestEnemy = en;
	}

	public int getConflicts() {
		return this.conflicts;
	}

	public double getSpeed() {
		return this.speed;
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

	public double getFoodCos() {
		return this.foodCos;
	}

	public HashMap<String, Double> getDecision() {
		HashMap<String, Double> output = new HashMap<String, Double>();
		double out_speed = this.speed;
		double out_angle = this.foodCos;
		output.put("Speed", out_speed);
		output.put("Angle", out_angle);
		return output;

	}

	public void calculateConflicts() {
		int currConflict = 0;
		if (this.currentAgent.inSight(this.currFood)) {
			double rx = this.currentAgent.getRx();
			double ry = this.currentAgent.getRy();
			double x = this.currentAgent.getX();
			double y = this.currentAgent.getY();
			double foodDirectionVectorX = currFood.getX() - x;
			double foodDirectionVectorY = currFood.getY() - y;
			double foodDirectionCosTeta = Math
					.signum(pseudoScalarProduct(rx, ry, foodDirectionVectorX, foodDirectionVectorY))
					* this.cosTeta(rx, ry, foodDirectionVectorX, foodDirectionVectorY);
			this.foodCos = foodDirectionCosTeta;
			if (this.nearestEnemy.inSight(this.currFood)) {
				if (canReachFood(this.currentAgent, this.nearestEnemy, this.currFood)) {
					currConflict += 1;
				}
			}
		} else {
			currConflict += 2;
			this.foodCos = -2;
		}
		this.conflicts = currConflict;
		this.speed = this.currentAgent.getSpeed();
		if (this.speed == 0){
			System.out.println("zero");
		}
	}

	protected double pseudoScalarProduct(double vx1, double vy1, double vx2, double vy2) {
		return (vx1 * vy2) - (vy1 * vx2);
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

	private boolean canReachFood(ABCDrivenAgent currentAgent, Agent nearestAgent, Food food) {
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
