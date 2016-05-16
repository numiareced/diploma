package com.hse12pi.environment;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import javax.swing.Timer;

public class EatenFoodObserver implements AgentsEnvironmentObserver {

	protected static final double minEatDistance = 5;

	protected static final double maxFishesDistance = 5;

	private Random random = new Random();

	private double score = 0;
	private static int totalFoodCount = 0;
	private static int abcFoodcount = 0;
	private static double abcAverage = 0.0;
	private static int abcPercent = 0;

	private static int dtFoodcount = 0;
	private static double dtAverage = 0.0;
	private static int dtPercent = 0;
	
	private static int nnFoodcount = 0; 
	private static double nnAverage = 0.0; 
	private static int nnPercent = 0; 
	public static boolean runTest = false;

	private static double  abcAverageSpeed = 0;
	private static double  dtAverageSpeed = 0;
	private static double  nnAverageSpeed = 0; 
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
				double distanceToSecondFish = this.module(firstFish.getX() - secondFish.getX(),
						firstFish.getY() - secondFish.getY());
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
					if (runTest) {
						totalFoodCount++;
						Main.totalFoodCount ++;
						if (fish.getClass().getName() == ABCDrivenAgent.class.getName()) {
							abcFoodcount++;
							Main.abcFoodcount ++;
							fish.setEatenFoodCount((fish.getEatenFoodCount()) + 1);
							calcucateAverage(env);
							calculateAverageSpeed(env);
							calculatePercent();

						}
						if (fish.getClass().getName() == TreeDrivenAgent.class.getName()) {
							dtFoodcount++;
							Main.dtFoodcount ++;
							fish.setEatenFoodCount((fish.getEatenFoodCount()) + 1);
							calcucateAverage(env);
							calculateAverageSpeed(env);
							calculatePercent();
						}
						if (fish.getClass().getName() == NetworkDrivenAgent.class.getName()) {
							nnFoodcount++;
							Main.nnFoodcount ++;
							fish.setEatenFoodCount((fish.getEatenFoodCount()) + 1);
							calcucateAverage(env);
							calculatePercent();
							calculateAverageSpeed(env);
						}
						Main.test_area.setText(" Test information: \n");
						Main.test_area.append(" Total food count:" + totalFoodCount +" \n");
						if (Main.abcAgents_check.isSelected()) {
							Main.test_area.append("ABC Algorithm total food count:" + abcFoodcount + "\n");
						}
						if (Main.dtAgents_check.isSelected()) {
							Main.test_area.append("dt Algorithm total food count:" + dtFoodcount + "\n");
						}
						if (Main.genAgents_check.isSelected()){
							Main.test_area.append("Genetic Algorithm total food count:" + nnFoodcount + "\n");
						}
						if (Main.agentsAverage_check.isSelected()){
							Main.test_area.append("ABC algorithm average:" + abcAverage + "\n");
							Main.test_area.append("DT algorithm average:" + dtAverage + "\n");
							Main.test_area.append("GEN+NN algorithm average:" + nnAverage + "\n");
							Main.test_area.append("ABC average speed:" + abcAverageSpeed + "\n");
							Main.test_area.append("DT average speed:" + dtAverageSpeed + "\n");
							Main.test_area.append("GEN+NN average speed:" + nnAverageSpeed + "\n");
							Main.calculateAverage(env);
							Main.calculatePercent();
							Main.calculateAverageSpeed(env);
							
						}
						if (Main.percent_check.isSelected()){
							Main.test_area.append("ABC algorithm percent:" + abcPercent + "%" + "\n");
							Main.test_area.append("DT algorithm  percent:" + dtPercent + "%" + "\n");
							Main.test_area.append("GEN+NN algorithm  percent:" + nnPercent + "%" + "\n");
							Main.abcPercent = abcPercent; 
							Main.dtPercent = dtPercent; 
							Main.nnPercent = nnPercent; 
						}

					}
					continue F;
				}
			}

		}
		return eatenFood;
	}

	private void calcucateAverage(AgentsEnvironment env) {
		abcAverage = 0;
		dtAverage = 0;
		nnAverage = 0; 
		int dtQuantity = 0;
		int abcQuantity = 0;
		int nnQuantity = 0; 
		for (ABCDrivenAgent agent : env.filter(ABCDrivenAgent.class)) {
			abcAverage += agent.getEatenFoodCount();
			abcQuantity++;
		}
		if (abcAverage != 0) {
			abcAverage = abcAverage / abcQuantity;
		}
		for (TreeDrivenAgent agent : env.filter(TreeDrivenAgent.class)) {
			dtAverage += agent.getEatenFoodCount();
			dtQuantity++;
		}
		if (dtAverage != 0) {
			dtAverage = dtAverage / dtQuantity;
		}
		for (NetworkDrivenAgent agent : env.filter(NetworkDrivenAgent.class)) {
			nnAverage += agent.getEatenFoodCount();
			nnQuantity++;
		}
		if (nnAverage != 0) {
			nnAverage = nnAverage / nnQuantity;
		}
	}
	
	private void calculateAverageSpeed(AgentsEnvironment env){
		abcAverageSpeed = 0;
		dtAverageSpeed = 0;
		nnAverageSpeed = 0; 
		int dtQuantity = 0;
		int abcQuantity = 0;
		int nnQuantity = 0; 
		for (ABCDrivenAgent agent : env.filter(ABCDrivenAgent.class)) {
			abcAverageSpeed += agent.getSpeed();
			abcQuantity++;
		}
		if (abcAverageSpeed != 0) {
			abcAverageSpeed = abcAverageSpeed / abcQuantity;
		}
		for (TreeDrivenAgent agent : env.filter(TreeDrivenAgent.class)) {
			dtAverageSpeed += agent.getSpeed();
			dtQuantity++;
		}
		if (dtAverageSpeed != 0) {
			dtAverageSpeed = dtAverageSpeed / dtQuantity;
		}
		for (NetworkDrivenAgent agent : env.filter(NetworkDrivenAgent.class)) {
			nnAverageSpeed += agent.getSpeed();
			nnQuantity++;
		}
		if (nnAverageSpeed != 0) {
			nnAverageSpeed = nnAverageSpeed / nnQuantity;
		}
	}

	private void calculatePercent() {
		abcPercent = (abcFoodcount * 100) / totalFoodCount;
		dtPercent = (dtFoodcount * 100) / totalFoodCount;
		nnPercent = (nnFoodcount *100)/totalFoodCount; 
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
		dtFoodcount = 0;
		dtAverage = 0;
		nnFoodcount = 0;
		nnAverage = 0;
		totalFoodCount = 0;
		

	}

}
