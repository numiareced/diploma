package com.hse12pi.environment;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class EatenFoodObserver implements AgentsEnvironmentObserver{
	
	protected static final double minEatDistance = 5;

	protected static final double maxFishesDistance = 5;

	private Random random = new Random();

	private double score = 0;
	private static int totalFoodCount = 0; 
	private static int abcFoodcount = 0; 
	private static double abcAverage = 0.0; 
	private static int abcPercent = 0 ; 
	
	private static int dtFoodcount = 0; 
	private static double dtAverage = 0.0; 
	private static int dtPercent = 0 ; 
	public static boolean runTest = false; 
	
	public static int getabcFoodcount() {
		return abcFoodcount; 
	}
	
	
	

	@Override
	public void notify(AgentsEnvironment env) {
		List<Food> eatenFood = this.getEatenFood(env);
		this.score += eatenFood.size();

		List<Agent> collidedFishes = this.getCollidedFishes(env);
		this.score -= collidedFishes.size() * 0.5;
		this.removeEatenAndCreateNewFood(env, eatenFood);
	}

	private List<Agent> getCollidedFishes(AgentsEnvironment env) {
		List<Agent> collidedFishes = new LinkedList<Agent>();

		List<Agent> allFishes = this.getFishes(env);
		int fishesCount = allFishes.size();

		for (int i = 0; i < (fishesCount - 1); i++) {
			Agent firstFish = allFishes.get(i);
			for (int j = i + 1; j < fishesCount; j++) {
				Agent secondFish = allFishes.get(j);
				double distanceToSecondFish = this.module(firstFish.getX() - secondFish.getX(), firstFish.getY() - secondFish.getY());
				if (distanceToSecondFish < maxFishesDistance) {
					collidedFishes.add(secondFish);
				}
			}
		}
		return collidedFishes;
	}

	private List<Food> getEatenFood(AgentsEnvironment env) {
		List<Food> eatenFood = new LinkedList<Food>();

		F: for (Food food : this.getFood(env)) {
			for (Agent fish : this.getFishes(env)) {
				double distanceToFood = this.module(food.getX() - fish.getX(), food.getY() - fish.getY());
				if (distanceToFood < minEatDistance) {
					eatenFood.add(food);
					if (runTest){
					totalFoodCount ++; 
					if ( fish.getClass().getName() == ABCDrivenAgent.class.getName()){
						abcFoodcount ++ ;
						fish.setEatenFoodCount((fish.getEatenFoodCount())+1);
						calcucateAverage(env);
						calculatePercent();
						
						
					}
					if ( fish.getClass().getName() == TreeDrivenAgent.class.getName()){
						dtFoodcount ++ ;
						fish.setEatenFoodCount((fish.getEatenFoodCount())+1);
						calcucateAverage(env);
						calculatePercent();
					}
					Main.test_area.setText(" Test information: \n" 
		                       + "ABC Algorithm total food count:"+ abcFoodcount + "\n" 
	    		               + "ABC algorithm average:" + abcAverage + "\n" 
	    		               + "ABC algorithm percent:" + abcPercent + "%" + "\n"
		                       + "dt Algorithm total food count:"+ dtFoodcount + "\n" 
	    		               + "dt algorithm average:" + dtAverage + "\n" 
	    		               + "dt algorithm percent:" + dtPercent + "%" + "\n");
					}
					continue F;
				}
			}

		}
		return eatenFood;
	}
	private void calcucateAverage(AgentsEnvironment env){
		abcAverage =0;
		dtAverage = 0; 
		int dtQuantity = 0; 
		int abcQuantity =0 ; 
		for (ABCDrivenAgent agent:env.filter(ABCDrivenAgent.class)){
			abcAverage += agent.getEatenFoodCount();
			abcQuantity ++; 
		} if (abcAverage!= 0){
		abcAverage = abcAverage / abcQuantity; 
		}
		for (TreeDrivenAgent agent:env.filter(TreeDrivenAgent.class)){
			dtAverage += agent.getEatenFoodCount();
			dtQuantity ++; 
		} if (dtAverage!= 0){
			dtAverage = dtAverage / abcQuantity; 
		}
	}
	
	private void calculatePercent(){
		abcPercent = ( abcFoodcount * 100 )/totalFoodCount; 
		dtPercent =  ( dtFoodcount * 100)/totalFoodCount;
	}

	protected void removeEatenAndCreateNewFood(AgentsEnvironment env, List<Food> eatenFood) {
		for (Food food : eatenFood) {
			env.removeAgent(food);

			this.addRandomPieceOfFood(env);
		}
	}

	protected void addRandomPieceOfFood(AgentsEnvironment env) {
		int x = this.random.nextInt(env.getWidth());
		int y = this.random.nextInt(env.getHeight());
		Food newFood = new Food(x, y);
		env.addAgent(newFood);
	}

	private List<Food> getFood(AgentsEnvironment env) {
		List<Food> food = new ArrayList<Food>();
		for (Food f : env.filter(Food.class)) {
			food.add(f);
		}
		return food;
	}

	private List<Agent> getFishes(AgentsEnvironment env) {
		List<Agent> fishes = new ArrayList<Agent>();
		for (Agent agent : env.filter(Agent.class)) {
			fishes.add(agent);
		}
		return fishes;
	}

	public double getScore() {
		if (this.score < 0) {
			return 0;
		}
		return this.score;
	}

	protected double module(double vx1, double vy1) {
		return Math.sqrt((vx1 * vx1) + (vy1 * vy1));
	}
	
	public static void resetFoodCounts() {
		abcFoodcount = 0; 
		abcAverage = 0; 
		totalFoodCount =0 ; 
		
	}

}
