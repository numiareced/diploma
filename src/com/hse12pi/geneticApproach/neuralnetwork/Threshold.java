package com.hse12pi.geneticApproach.neuralnetwork;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public enum Threshold {
	LINEAR {
		@Override
		public double calculate(double value, List<Double> params) {
			double a = params.get(0);
			double b = params.get(1);
			return (a * value) + b;
		};

		@Override
		public List<Double> getDefaultParams() {
			double a = 1;
			double b = 0;
			List<Double> ret = new LinkedList<Double>();
			ret.add(a);
			ret.add(b);
			return ret;
		}
	},
	SIGN {
		@Override
		public double calculate(double value, List<Double> params) {
			double threshold = params.get(0);
			if (value > threshold) {
				return 1;
			} else {
				return 0;
			}
		};

		@Override
		public List<Double> getDefaultParams() {
			double threshold = 0;
			List<Double> ret = new LinkedList<Double>();
			ret.add(threshold);
			return ret;
		}
	},
	SIGMA {
		@Override
		public double calculate(double value, List<Double> params) {
			double a = params.get(0);
			double b = params.get(1);
			double c = params.get(2);
			return a / (b + Math.expm1(-value * c) + 1);
		}

		@Override
		public List<Double> getDefaultParams() {
			double a = 1;
			double b = 1;
			double c = 1;
			List<Double> ret = new ArrayList<Double>(3);
			ret.add(a);
			ret.add(b);
			ret.add(c);
			return ret;
		}
	};

	private static final Random random = new Random();

	public static Threshold getRandomFunction() {
		Threshold[] allFunctions = values();
		return allFunctions[random.nextInt(allFunctions.length)];
	}

	public double calculate(double value, List<Double> params) {
		// Stub
		return 0;
	}

	public List<Double> getDefaultParams() {
		// Stub
		return Collections.emptyList();
	}
}
