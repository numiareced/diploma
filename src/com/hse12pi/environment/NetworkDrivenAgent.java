package com.hse12pi.environment;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import com.hse12pi.geneticApproach.neuralnetwork.GeneticTrainedNetwork;
import com.hse12pi.geneticApproach.neuralnetwork.NeuralNetwork;
import com.hse12pi.geneticApproach.neuralnetwork.Threshold;

public class NetworkDrivenAgent extends Agent{

	private static Random random = new Random();
	
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
		// TODO Auto-generated constructor stub
	}
	
	public synchronized void setBrain(NeuralNetwork brain) {
		this.brain = brain;
	}
	public synchronized void interact(AgentsEnvironment env) {
		
		//double deltaAngle = random.nextDouble() * 2 * Math.PI;
		double deltaAngle = 0;
		double deltaSpeed = random.nextDouble() * 2 * Math.PI;
		double newSpeed = this.normalizeSpeed(this.getSpeed() + deltaSpeed);
		double newAngle = this.getAngle() + this.normalizeDeltaAngle(deltaAngle);
		this.setAngle(newAngle);
		this.setSpeed(newSpeed);
		this.move();
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
	
	public static GeneticTrainedNetwork randomNeuralNetworkBrain() {
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
	}
	

}
