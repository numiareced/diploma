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

	private double avoidNaNAndInfinity(double x) {
		if ((Double.isNaN(x)) || Double.isInfinite(x)) {
			x = 0;
		}
		return x;
	}

	public boolean inSight(AbstractAgent agent) {
		double crossProduct = this.cosTeta(this.getRx(), this.getRy(), agent.getX() - this.getX(),
				agent.getY() - this.getY());
		return (crossProduct > 0);
	}

	public double distanceTo(AbstractAgent agent) {
		return this.module(agent.getX() - this.getX(), agent.getY() - this.getY());
	}

	public double cosTeta(double vx1, double vy1, double vx2, double vy2) {
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

}
