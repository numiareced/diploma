package com.hse12pi.environment;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.hse12pi.abc.ArtificialBeeColony;
import com.hse12pi.decisiontree.BadDecisionException;

public class ABCDrivenAgent extends Agent {

	private static final double maxSpeed = 4;

	private static final double maxDeltaAngle = 1;

	protected static final double maxAgentsDistance = 5;

	private static final double FOOD = 10;
	private ArtificialBeeColony ABC;

	public ABCDrivenAgent(double x, double y, double angle) {
		super(x, y, angle);
		ABC = new ArtificialBeeColony((int) FOOD);
		initializeRandomSpeed(); // random direction for start speed + cos
	}

	public synchronized void interact(AgentsEnvironment env) {

		Map<String, Double> output = new HashMap<String, Double>();
		ABC.setEnvironment(env);
		ABC.setCurrentAgent(this);
		output = ABC.optimizeDirection();
		double deltaAngle = output.get("Angle");
		double deltaSpeed = output.get("Speed");

		deltaSpeed = this.avoidNaNAndInfinity(deltaSpeed);
		deltaAngle = this.avoidNaNAndInfinity(deltaAngle);

		double newSpeed = this.normalizeSpeed(this.getSpeed() + deltaSpeed);
		double newAngle = this.getAngle() + this.normalizeDeltaAngle(deltaAngle);
		if (newSpeed == 0) {
			Random rand = new Random();
			newSpeed = rand.nextDouble() * 4;
		}
		this.setAngle(newAngle);
		this.setSpeed(newSpeed);
		this.move();
	}

	public void initializeRandomSpeed() {
		Random rand = new Random();
		double randomSpeed = rand.nextDouble() * 4;
		this.setSpeed(randomSpeed);

	}

}
