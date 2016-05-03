package com.hse12pi.abc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;

import com.hse12pi.environment.ABCDrivenAgent;
import com.hse12pi.environment.AbstractAgent;
import com.hse12pi.environment.Agent;
import com.hse12pi.environment.AgentsEnvironment;
import com.hse12pi.environment.Food;

public class ArtificialBeeColony {

	public int MAX_LENGTH; /*
							 * The number of parameters of the problem to be
							 * optimized
							 */
	public int FOOD_NUMBER; /*
							 * The number of food sources equals the half of the
							 * colony size
							 */
	public int MAX_EPOCH; /*
							 * The number of cycles for foraging {a stopping
							 * criteria}
							 */
	HashMap<String, Double> decision;
	public Random rand;
	public ArrayList<FoodForBees> foodSources;
	public FoodForBees gBest;
	public int epoch;
	AgentsEnvironment environment;
	ABCDrivenAgent currAgent;

	public ArtificialBeeColony(int n) {
		FOOD_NUMBER = n;
		MAX_EPOCH = 5;
		gBest = null;
		epoch = 0;
		decision = new HashMap<String, Double>();
		environment = null;
		currAgent = null;
	}

	public void setEnvironment(AgentsEnvironment e) {
		this.environment = e;
	}

	public void setCurrentAgent(ABCDrivenAgent agent) {
		this.currAgent = agent;
	}

	public HashMap<String, Double> optimizeDirection() {
		foodSources = new ArrayList<FoodForBees>();
		HashMap<String, Double> output = new HashMap<String, Double>();
		rand = new Random();
		boolean done = false;
		boolean noSolution = false;
		epoch = 0;

		initialize();
		memorizeBestFoodSource();

		while (!done) {
			if (epoch < MAX_EPOCH) {
				if (gBest.getConflicts() == 0) { // if min time exists
					done = true;
					break;
				}
				sendEmployedBees();
				getFitness();
				calculateProbabilities();
				sendOnlookerBees();
				memorizeBestFoodSource();
				sendScoutBees();
				epoch++;
				// This is here simply to show the runtime status.
				System.out.println("Epoch: " + epoch);
			} else {
				done = true;
			}

		}

		if (epoch == MAX_EPOCH) {
			System.out.println("No Solution found");
			done = false;
			noSolution = true;
		}
		if (noSolution) {
			Random random = new Random();
			double deltaAngle = random.nextDouble() * 2 * Math.PI;
			double deltaSpeed = random.nextDouble() * 4;
			output.put("Speed", deltaSpeed);
			output.put("Angle", deltaAngle);
			return output;
		} else {
			return gBest.getDecision(); // return speed and dir
		}
	}

	public void initialize() {
		if ((environment != null) && (currAgent != null)) {
			ArrayList<Food> allfood = new ArrayList<Food>();
			for (Food currFood : environment.filter(Food.class)) {
				allfood.add(currFood);
			}
			for (int i = 0; i < FOOD_NUMBER; i++) {
				FoodForBees newHoney = new FoodForBees(FOOD_NUMBER, environment, currAgent);
				foodSources.add(newHoney);
				foodSources.get(i).setFood(allfood.get(i));
				foodSources.get(i).setNearestEnemy(calculateNearestEnemy(environment, allfood.get(i)));
				foodSources.get(i).calculateConflicts(); //
			}
		} else {
			System.out.println("model is not initialized!");
		}
	}

	private Agent calculateNearestEnemy(AgentsEnvironment env, Food f) {
		Agent nearestAgent = null;
		double nearestAgentDist = Double.MAX_VALUE;
		for (Agent agent : environment.filter(Agent.class)) {
			double currEnemyDist = distanceTo(agent, f);
			if (currEnemyDist <= nearestAgentDist) {
				nearestAgent = agent;
				nearestAgentDist = currEnemyDist;
			}
		}
		return  nearestAgent;
	}

	protected double distanceTo(AbstractAgent food, AbstractAgent agent) {
		return module(agent.getX() - food.getX(), agent.getY() - food.getY());
	}

	protected double module(double vx1, double vy1) {
		return Math.sqrt((vx1 * vx1) + (vy1 * vy1));
	}

	public int getRandomNumber(int low, int high) {
		return (int) Math.round((high - low) * rand.nextDouble() + low);
	}

	public void memorizeBestFoodSource() {
		gBest = Collections.min(foodSources);
	}

	public void sendEmployedBees() {
		int neighborBeeIndex = 0;
		FoodForBees currentBee = null;
		FoodForBees neighborBee = null;
		for (int i = 0; i < FOOD_NUMBER; i++) {
			// A randomly chosen solution is used in producing a mutant solution
			// of the solution i
			neighborBeeIndex = getExclusiveRandomNumber(FOOD_NUMBER - 1, i);
			currentBee = foodSources.get(i);
			neighborBee = foodSources.get(neighborBeeIndex);
			sendToWork(currentBee, neighborBee);
		}
	}

	public int getExclusiveRandomNumber(int high, int except) {
		boolean done = false;
		int getRand = 0;

		while (!done) {
			getRand = rand.nextInt(high);
			if (getRand != except) {
				done = true;
			}
		}

		return getRand;
	}

	public void sendToWork(FoodForBees currentBee, FoodForBees neighborBee) {

		double currConflict = currentBee.getConflicts();
		double neightborSpeed = 0;
		double currentSpeed = currentBee.getSpeed();
		double newSpeed = 0;
		if (currConflict != 2) { // we see food and have angle for it, need just
									// to mutate speed
			if (neighborBee.getNearestEnemy() != null) {
				neightborSpeed = neighborBee.getNearestEnemy().getSpeed();
				if (neightborSpeed != 0) {
					newSpeed = currentSpeed + (0.5 * neightborSpeed * rand.nextDouble());
				} else {
					newSpeed = currentSpeed + (0.5 * rand.nextDouble());
				}
			} else {
				System.out.println("error during..");
			}
			if (newSpeed > 4.0) {
				newSpeed = 4.0;
			}
			if (newSpeed < -4.0) {
				newSpeed = -4.0;
			}
			if (newSpeed != 0) {
				currentBee.getCurrAgent().setSpeed(newSpeed);
				currentBee.calculateConflicts();
				int newConflicts = currentBee.getConflicts();
				if (currConflict <= newConflicts) {
					// no improvement
					currentBee.getCurrAgent().setSpeed(currentSpeed);
					currentBee.calculateConflicts();
					currentBee.setTrials(currentBee.getTrials() + 1);
				} else {
					currentBee.setTrials(0);
				}
			} else {
				currentBee.setTrials(currentBee.getTrials() + 1);
				// no speed
			}
		} else {
			// we dont see this food, no worth trying until we change the angle
			currentBee.setTrials(currentBee.getTrials() + 1);
			System.out.println("dont see food");
		}

	}

	public void getFitness() {
		// calculate best scores
		// Lowest errors = 100%, Highest errors = 0%
		FoodForBees thisFood = null;
		double bestScore = 0.0;
		double worstScore = 0.0;
		// The worst score would be the one with the highest energy, best would
		// be lowest.
		worstScore = Collections.max(foodSources).getConflicts();
		// Convert to a weighted percentage.
		bestScore = worstScore - Collections.min(foodSources).getConflicts();
		for (int i = 0; i < FOOD_NUMBER; i++) {
			thisFood = foodSources.get(i);
			thisFood.setFitness((worstScore - thisFood.getConflicts()) * 100.0 / bestScore);
		}
		System.out.println("getting fitness");
	}

	public void calculateProbabilities() {
		// calculate probability that this food will be chosen to go
		FoodForBees thisFood = null;
		double maxfit = foodSources.get(0).getFitness();

		for (int i = 1; i < FOOD_NUMBER; i++) {
			thisFood = foodSources.get(i);
			if (thisFood.getFitness() > maxfit) {
				maxfit = thisFood.getFitness();
			}
		}
		for (int j = 0; j < FOOD_NUMBER; j++) {
			thisFood = foodSources.get(j);
			thisFood.setSelectionProbability((0.9 * (thisFood.getFitness() / maxfit)) + 0.1);
		}
		System.out.println("calc probs");
	}

	/*
	 * Sends the onlooker bees to optimize the solution. Onlooker bees work on
	 * the best solutions from the employed bees. best solutions have high
	 * selection probability.
	 *
	 */
	public void sendOnlookerBees() {
		// check solution and optimize it
		int i = 0;
		int neighborBeeIndex = 0;
		FoodForBees currentBee = null;
		FoodForBees neighborBee = null;
		System.out.println("onlookerzz");
		for (int j = 0; j < FOOD_NUMBER; j++) {
			currentBee = foodSources.get(j);
			if (currentBee.getSelectionProbability() < 0.7) {
				neighborBeeIndex = getExclusiveRandomNumber(FOOD_NUMBER - 1, i);
				neighborBee = foodSources.get(neighborBeeIndex);
				System.out.println("onlooker");
				sendToWork(currentBee, neighborBee);
				i++;
			}
		}
	}

	/*
	 * Finds food sources which have been abandoned/reached the limit. Scout
	 * bees will generate a totally random solution from the existing and it
	 * will also reset its trials back to zero.
	 *
	 */
	public void sendScoutBees() {
		FoodForBees currentBee = null;
		for (int i = 0; i < FOOD_NUMBER; i++) {
			currentBee = foodSources.get(i);
			if (currentBee.getTrials() > 0) {
				System.out.println("sending scout");
				Random random = new Random();
				double deltaAngle = random.nextDouble() * 2 * Math.PI;
				double deltaSpeed = random.nextDouble() * 4;
				currentBee.getCurrAgent().setAngle(deltaAngle);
				currentBee.getCurrAgent().setSpeed(deltaSpeed);
				currentBee.calculateConflicts();
				currentBee.setTrials(0);
			}
		}
	}

}
