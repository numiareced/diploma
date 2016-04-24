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
	public int NP; /*
					 * The number of total bees/colony size. employed + onlookers
					 */
	public int FOOD_NUMBER; /*
							 * The number of food sources equals the half of the
							 * colony size
							 */
	public int LIMIT; /*
						 * A food source which could not be improved through
						 * "limit" trials is abandoned by its employed bee
						 */
	public int MAX_EPOCH; /*
							 * The number of cycles for foraging {a stopping
							 * criteria}
							 */
	public int MIN_SHUFFLE;
	public int MAX_SHUFFLE;
	HashMap<String, Double> decision;
	public Random rand;
	public ArrayList<FoodForBees> foodSources;
	public ArrayList<FoodForBees> solutions;
	public FoodForBees gBest;
	public int epoch;
	AgentsEnvironment environment;
	ABCDrivenAgent currAgent;

	public ArtificialBeeColony(int n) {
		NP = n * 2; // pop size 20 to 40 or even 100
		FOOD_NUMBER = n;
		LIMIT = 5;
		MAX_EPOCH = 10;
		MIN_SHUFFLE = 8;
		MAX_SHUFFLE = 20;
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
		solutions = new ArrayList<FoodForBees>();
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
				//System.out.println("Epoch: " + epoch);
			} else {
				done = true;
			}

		}

		if (epoch == MAX_EPOCH) {
			System.out.println("No Solution found");
			done = false;
			noSolution = true;
		}

		//System.out.println("done.");
		//System.out.println("Completed " + epoch + " epochs.");
		if (noSolution) {
			Random random = new Random();
			double deltaAngle =  random.nextDouble() * 2 * Math.PI;
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
				foodSources.get(i).setNearestEnemy(calculateNearestEnemy(environment,allfood.get(i)));
				foodSources.get(i).calculateConflicts(); //
			}
		} else {
			System.out.println("model is not initialized!");
		}
	}

	
	private ABCDrivenAgent calculateNearestEnemy(AgentsEnvironment env, Food f){
		Agent nearestAgent = null;
		double nearestAgentDist = Double.MAX_VALUE;
		 for (Agent agent : environment.filter(Agent.class)) {
			 double currEnemyDist = distanceTo(agent, f);
			 if (currEnemyDist <= nearestAgentDist) {
					nearestAgent = agent;
					nearestAgentDist = currEnemyDist;
				}
		 }
		return (ABCDrivenAgent)nearestAgent;
	}
	
/*	protected double distanceTo(AbstractAgent food, AbstractAgent agent) {
		return this.module(agent.getX() - food.getX(), agent.getY() - food.getY());
	}*/
	
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
		//System.out.println("best is:" + gBest.getConflicts());
	}

	public void sendEmployedBees() {
		int neighborBeeIndex = 0;
		FoodForBees currentBee = null;
		FoodForBees neighborBee = null;
		for (int i = 0; i < FOOD_NUMBER; i++) {
			// A randomly chosen solution is used in producing a mutant solution
			// of the solution i
			// neighborBee = getRandomNumber(0, Food_Number-1);
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
		// get curr speed & dist
		// get neightbor speed & dist
		// generate new speed & dist
		// set to curr agent
		// calculate conflicts. If this is better - trials (0)
		// else swap?? read about this
//		int newValue = 0;
		//int tempValue = 0;
		double newSpeed  = 0.0; 
		double tempSpeed = 0.0; 
		double tempAngle = 0.0; 
		double newAngle = 0.0; 
	//	int tempIndex = 0;
		int prevConflicts = 0;
		int currConflicts = 0;
/*		int parameterToChange = 0;
		int randEnemy = 0; 
		double speedToChange = 0.0; */
		// get number of conflicts
		prevConflicts = currentBee.getConflicts();
		// The parameter to be changed is determined randomly
		//parameterToChange = getRandomNumber(0, MAX_LENGTH - 1);
		tempSpeed = currentBee.getCurrAgent().getSpeed();
		//System.out.println("got speed" + tempSpeed);
		tempAngle = currentBee.getCurrAgent().getAngle();
		
		if (neighborBee.getNearestEnemy() != null ){
			if (tempSpeed != 0){
			newSpeed =  (tempSpeed - neighborBee.getNearestEnemy().getSpeed()) * ( rand.nextDouble()) ;
			}
			else {
				//System.out.println("mutate speed");
				newSpeed = rand.nextDouble() * 4 ;
	
			}
			//newAngle = tempAngle + (tempAngle - neighborBee.getNearestEnemy().getAngle()) * ( rand.nextDouble() - 0.5)*2;
		}
		else {
			newSpeed =  rand.nextDouble() * 4 ;
			//newAngle =  rand.nextDouble() * 2 * Math.PI; ;
		}
/*		if (newSpeed <= 0 ){
			newSpeed = 0 ; 
		}*/
		if (newSpeed > 4.0 ){
			newSpeed = 4.0; 
		}
		
		currentBee.getCurrAgent().setSpeed(newSpeed);
		//currentBee.getCurrAgent().setAngle(newAngle);
		currentBee.calculateConflicts();
		currConflicts = currentBee.getConflicts();
		if ( prevConflicts < currConflicts){
			currentBee.getCurrAgent().setSpeed(tempSpeed);
			//currentBee.getCurrAgent().setAngle(tempAngle);
			currentBee.calculateConflicts();
			currentBee.setTrials(currentBee.getTrials() + 1);
		}else {
			currentBee.setTrials(0);
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
		int t = 0;
		int neighborBeeIndex = 0;
		FoodForBees currentBee = null;
		FoodForBees neighborBee = null;

		while (t < FOOD_NUMBER) {
			currentBee = foodSources.get(i);
			if (rand.nextDouble() < currentBee.getSelectionProbability()) {
				t++;
				neighborBeeIndex = getExclusiveRandomNumber(FOOD_NUMBER - 1, i);
				neighborBee = foodSources.get(neighborBeeIndex);
				sendToWork(currentBee, neighborBee);
			}
			i++;
			if (i == FOOD_NUMBER) {
				i = 0;
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
		int shuffles = 0;

		for (int i = 0; i < FOOD_NUMBER; i++) {
			currentBee = foodSources.get(i);
			if (currentBee.getTrials() >= LIMIT) {
				shuffles = getRandomNumber(MIN_SHUFFLE, MAX_SHUFFLE);
				for (int j = 0; j < shuffles; j++) {
					Random random = new Random();
					double deltaAngle = random.nextDouble() * 360;
					double deltaSpeed = random.nextDouble() * 4;
					currentBee.getCurrAgent().setAngle(deltaAngle);
					currentBee.getCurrAgent().setSpeed(deltaSpeed);
				}
				currentBee.calculateConflicts();
				currentBee.setTrials(0);
			}
		}
	}

}
