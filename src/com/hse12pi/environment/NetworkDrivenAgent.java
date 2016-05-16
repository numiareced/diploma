package com.hse12pi.environment;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import com.hse12pi.decisiontree.DecisionTree;
import com.hse12pi.decisiontree.UnknownDecisionException;
import com.hse12pi.geneticApproach.neuralnetwork.GeneticTrainedNetwork;
import com.hse12pi.geneticApproach.neuralnetwork.NeuralNetwork;
import com.hse12pi.geneticApproach.neuralnetwork.Threshold;




public class NetworkDrivenAgent extends Agent{

	private static final double maxSpeed = 4;

	private static final double maxDeltaAngle = 1;

	protected static final double maxAgentsDistance = 5;

	private static final double AGENT = -10;

	private static final double EMPTY = 0;

	private static final double FOOD = 10;
	
	//neural network needed vars
	private volatile NeuralNetwork brain;
	
	public NetworkDrivenAgent(double x, double y, double angle) {
		super(x, y, angle);
		initializeRandomSpeed();
		// TODO Auto-generated constructor stub
	}
	
	public synchronized void setBrain(NeuralNetwork brain) {
		this.brain = brain;
	}
	public synchronized void interact(AgentsEnvironment env) {
		
		List<Double> nnInputs = this.createNnInputs(env);

		this.activateNeuralNetwork(nnInputs);

		int neuronsCount = this.brain.getNeuronsCount();
		double deltaAngle = this.brain.getAfterActivationSignal(neuronsCount - 2);
		
		double deltaSpeed = this.brain.getAfterActivationSignal(neuronsCount - 1);
		
		deltaSpeed = this.avoidNaNAndInfinity(deltaSpeed);
		deltaAngle = this.avoidNaNAndInfinity(deltaAngle);

		double newSpeed = this.normalizeSpeed(this.getSpeed() + deltaSpeed);
		double newAngle = this.getAngle() + this.normalizeDeltaAngle(deltaAngle);

		this.setAngle(newAngle);
		this.setSpeed(newSpeed);

		this.move();
	}
	
	private void activateNeuralNetwork(List<Double> nnInputs) {
		for (int i = 0; i < nnInputs.size(); i++) {
			this.brain.putSignalToNeuron(i, nnInputs.get(i));
		}
		this.brain.activate();
	}
	
	public void initializeRandomSpeed() {
		Random rand = new Random();
		double randomSpeed = rand.nextDouble() * 4;
		this.setSpeed(randomSpeed);

	}
	
/*	public static GeneticTrainedNetwork randomNeuralNetworkBrain() {
		GeneticTrainedNetwork nn = new GeneticTrainedNetwork(15);
		for (int i = 0; i < 15; i++) {
			Threshold f = Threshold.getRandomFunction();
			nn.setNeuronFunction(i, f, f.getDefaultParams());
		}
		for (int i = 0; i < 6; i++) {
			nn.setNeuronFunction(i, Threshold.LINEAR, Threshold.LINEAR.getDefaultParams());
		}
		for (int i = 0; i < 6; i++) {
			for (int j = 6; j < 15; j++) {
				nn.addLink(i, j, Math.random());
			}
		}
		for (int i = 6; i < 15; i++) {
			for (int j = 6; j < 15; j++) {
				if (i < j) {
					nn.addLink(i, j, Math.random());
				}
			}
		}
		return nn;
	}*/
	
	public static GeneticTrainedNetwork randomNeuralNetworkBrain() {
		GeneticTrainedNetwork nn = new GeneticTrainedNetwork(11);
		for (int i = 0; i < 11; i++) {
			Threshold f = Threshold.getRandomFunction();
			nn.setNeuronFunction(i, f, f.getDefaultParams());
		}
		for (int i = 0; i < 4; i++) {
			nn.setNeuronFunction(i, Threshold.LINEAR, Threshold.LINEAR.getDefaultParams());
		}
		for (int i = 0; i < 4; i++) {
			for (int j = 4; j < 11; j++) {
				nn.addLink(i, j, Math.random());
			}
		}
		for (int i = 4; i < 11; i++) {
			for (int j = 4; j < 11; j++) {
				if (i < j) {
					nn.addLink(i, j, Math.random());
				}
			}
		}
		return nn;
	}
	
/*	protected List<Double> createNnInputs(AgentsEnvironment environment) {
		// Find nearest food
		Food nearestFood = null;
		double nearestFoodDist = Double.MAX_VALUE;

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

		// Find nearest agent
		Agent nearestAgent = null;
		double nearestAgentDist = maxAgentsDistance;

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
		

		List<Double> nnInputs = new LinkedList<Double>();

		double rx = this.getRx();
		double ry = this.getRy();

		double x = this.getX();
		double y = this.getY();

		if (nearestFood != null) {
			double foodDirectionVectorX = nearestFood.getX() - x;
			double foodDirectionVectorY = nearestFood.getY() - y;

			// left/right cos
			double foodDirectionCosTeta =
					Math.signum(this.pseudoScalarProduct(rx, ry, foodDirectionVectorX, foodDirectionVectorY))
							* this.cosTeta(rx, ry, foodDirectionVectorX, foodDirectionVectorY);
			double nearestEnemyDist = calculateNearestEnemy(environment, nearestFood);

			nnInputs.add(FOOD);
			nnInputs.add(nearestFoodDist);
			nnInputs.add(foodDirectionCosTeta);

		} else {
			nnInputs.add(EMPTY);
			nnInputs.add(0.0);
			nnInputs.add(0.0);
		}

		if (nearestAgent != null) {
			double agentDirectionVectorX = nearestAgent.getX() - x;
			double agentDirectionVectorY = nearestAgent.getY() - y;

			// left/right cos
			double agentDirectionCosTeta =
					Math.signum(this.pseudoScalarProduct(rx, ry, agentDirectionVectorX, agentDirectionVectorY))
							* this.cosTeta(rx, ry, agentDirectionVectorX, agentDirectionVectorY);

			nnInputs.add(AGENT);
			nnInputs.add(nearestAgentDist);
			nnInputs.add(agentDirectionCosTeta);

		} else {
			nnInputs.add(EMPTY);
			nnInputs.add(0.0);
			nnInputs.add(0.0);
		}
		return nnInputs;
	}*/
	
	
	protected List<Double> createNnInputs(AgentsEnvironment environment) {
		// Find nearest food
		Food nearestFood = null;
		double nearestFoodDist = Double.MAX_VALUE;

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

		Agent nearestAgent = null;
		double nearestAgentDist = maxAgentsDistance;
		
		List<Double> nnInputs = new LinkedList<Double>();

		double rx = this.getRx();
		double ry = this.getRy();

		double x = this.getX();
		double y = this.getY();

		if (nearestFood != null) {
			double foodDirectionVectorX = nearestFood.getX() - x;
			double foodDirectionVectorY = nearestFood.getY() - y;
			for (Agent agent : environment.filter(Agent.class)) {
				double currEnemyDist = distanceTo(agent, nearestFood);
				if (currEnemyDist <= nearestAgentDist) {
					nearestAgent = agent;
					nearestAgentDist = currEnemyDist;
				}
			}

			// left/right cos
			double foodDirectionCosTeta =
					Math.signum(this.pseudoScalarProduct(rx, ry, foodDirectionVectorX, foodDirectionVectorY))
							* this.cosTeta(rx, ry, foodDirectionVectorX, foodDirectionVectorY);
			
			

			nnInputs.add(nearestFoodDist);
			nnInputs.add(foodDirectionCosTeta);
			if (nearestAgent != null) {
				double agentDirectionVectorX = nearestAgent.getX() - x;
				double agentDirectionVectorY = nearestAgent.getY() - y;

				// left/right cos
				double agentDirectionCosTeta =
						Math.signum(this.pseudoScalarProduct(rx, ry, agentDirectionVectorX, agentDirectionVectorY))
								* this.cosTeta(rx, ry, agentDirectionVectorX, agentDirectionVectorY);

				nnInputs.add(nearestAgentDist);
				nnInputs.add(agentDirectionCosTeta);

			} else {
				nnInputs.add(0.0);
				nnInputs.add(0.0);
			}


		} else {
			nnInputs.add(0.0);
			nnInputs.add(0.0);
			nnInputs.add(0.0);
			nnInputs.add(0.0);
		}

		
		return nnInputs;
	}
	private double calculateNearestEnemy(AgentsEnvironment env, Food f) {
		Agent nearestAgent = null;
		double nearestAgentDist = Double.MAX_VALUE;
		for (Agent agent : env.filter(Agent.class)) {
			double currEnemyDist = distanceTo(agent, f);
			if (currEnemyDist <= nearestAgentDist) {
				nearestAgent = agent;
				nearestAgentDist = currEnemyDist;
			}
		}
		return  nearestAgentDist;
	}
	protected double distanceTo(AbstractAgent food, AbstractAgent agent) {
		return module(agent.getX() - food.getX(), agent.getY() - food.getY());
	}

}
