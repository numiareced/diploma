package com.hse12pi.environment;
import java.awt.BorderLayout;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.prefs.Preferences;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import com.hse12pi.geneticApproach.geneticAlgorithm.Fitness;
import com.hse12pi.geneticApproach.geneticAlgorithm.GeneticAlgorithm;
import com.hse12pi.geneticApproach.geneticAlgorithm.IterartionListener;
import com.hse12pi.geneticApproach.geneticAlgorithm.Population;
import com.hse12pi.geneticApproach.neuralnetwork.GeneticTrainedNetwork;
import com.hse12pi.geneticApproach.neuralnetwork.NeuralNetwork;

public class Main {
	
	private static AgentsEnvironment environment;
	private static GeneticAlgorithm<GeneticTrainedNetwork, Double> ga;
	
	private static Random random = new Random();

	private static int populationNumber = 0;

	private static volatile boolean play = true;

	private static volatile boolean staticFood = true;

	private static volatile boolean regenerateFood = true;
	
	private static int eatenFoodCount = 0; 
	
	private static int count =0 ; 

	// UI

	private static JFrame appFrame;

	private static JPanel environmentPanel;

	private static JPanel controlsPanel;

	private static JTextField populationTextField;
	
	private static JTextField foodNumber; 
	
	private static JRadioButton staticF;
	
	private static JRadioButton dinamicF; 

	private static JRadioButton geneticAlgorithm;
	
	private static JRadioButton beesAlgorithm; 
	
	private static JRadioButton decicionTree; 
	
	private static JTextField timer;
	
	private static JButton startButton;


	private static JButton resetButton;

	//private static JRadioButton staticFoodRadioButton;

//	private static JRadioButton dynamicFoodRadioButton;

	//private static ButtonGroup foodTypeButtonGroup;

	//private static JCheckBox regenerateFoodCheckbox;

	private static JLabel populationInfoLabel;

	private static BufferedImage displayEnvironmentBufferedImage;

	private static Graphics2D displayEnvironmentCanvas;

	public static void main(String[] args) throws Exception {
		// TODO maybe, add ability to define these parameters as environment
		// constants
		int environmentWidth = 600;
		int environmentHeight = 400;
		int agentsCount = 15;
		int foodCount = 10;
		int gaPopulationSize = 5;
		int parentalChromosomesSurviveCount = 1;
		initializeGeneticAlgorithm(gaPopulationSize, parentalChromosomesSurviveCount, null);

		initializeEnvironment(environmentWidth, environmentHeight, agentsCount, foodCount);

		initializeCanvas(environmentWidth, environmentHeight);

		initializeUI(environmentWidth, environmentHeight);

		//initializeAddingFoodFunctionality();

		//initializeResetButtonFunctionality();

		displayUI();

		mainEnvironmentLoop();
	}
	
	private static void initializeCanvas(int environmentWidth, int environmentHeight) {
		displayEnvironmentBufferedImage = new BufferedImage(environmentWidth, environmentHeight, BufferedImage.TYPE_INT_RGB);
		displayEnvironmentCanvas = (Graphics2D) displayEnvironmentBufferedImage.getGraphics();
		displayEnvironmentCanvas.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	}
	private static void displayUI() {
		// put application frame to the center of screen
		appFrame.setLocationRelativeTo(null);
		appFrame.setVisible(true);
	}

	private static void initializeUI(int environmentWidth, int environmentHeight) {
		appFrame = new JFrame("Neuro-Evolutionarry algorithms driven agents");
		appFrame.setSize(environmentWidth + 130, environmentHeight + 50);
		appFrame.setResizable(false);
		appFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		appFrame.setLayout(new BorderLayout());

		environmentPanel = new JPanel();
		environmentPanel.setSize(environmentWidth, environmentHeight);
		appFrame.add(environmentPanel, BorderLayout.CENTER);

		controlsPanel = new JPanel();
		appFrame.add(controlsPanel, BorderLayout.EAST);
		controlsPanel.setLayout(new GridLayout(11, 1, 5, 5));

/*		evolveTextField = new JTextField("10");
		controlsPanel.add(evolveTextField);

		playPauseButton = new JButton("pause");
		controlsPanel.add(playPauseButton);*/

		resetButton = new JButton("reset");
		controlsPanel.add(resetButton);
		populationInfoLabel = new JLabel("Eaten food: " + eatenFoodCount, SwingConstants.CENTER);
		appFrame.add(populationInfoLabel, BorderLayout.NORTH);
		

	}
	
	private static void initializeEnvironment(int environmentWidth, int environmentHeight, int agentsCount, int foodCount) {
		environment = new AgentsEnvironment(environmentWidth, environmentHeight);
		environment.addListener(new EatenFoodObserver() {
			protected void addRandomPieceOfFood(AgentsEnvironment env) {
				if (regenerateFood) {
					eatenFoodCount++; 
					populationInfoLabel.setText(Integer.toString(eatenFoodCount));
					Food food = createRandomFood(env.getWidth(), env.getHeight());
					env.addAgent(food);
				}
			}
		});
		NeuralNetwork brain = ga.getBest();
		initializeAgents(brain, agentsCount);
		initializeFood(foodCount);
	}
	
	private static void evolvePopulation() {
		ga.evolve();
		NeuralNetwork newBrain = ga.getBest();
		setAgentBrains(newBrain);
		
	}
	private static void mainEnvironmentLoop() throws InterruptedException {
		for (;;) {
			Thread.sleep(50);
			if (play) {
				environment.timeStep();
				count ++;
				if ((eatenFoodCount == 5 )|| (count == 100)){
					System.out.println("need to envolve");
					evolvePopulation();
					eatenFoodCount =0;
					count =0; 
					
				}
			}
			Drawing.paintEnvironment(displayEnvironmentCanvas, environment);
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					environmentPanel.getGraphics().drawImage(displayEnvironmentBufferedImage, 0, 0, null);
				}
			});
		}
	}
	
	private static void initializeAgents( NeuralNetwork brain, int agentsCount) {
		int environmentWidth = environment.getWidth();
		int environmentHeight = environment.getHeight();

		for (int i = 0; i < agentsCount; i++) {
			int x = random.nextInt(environmentWidth);
			int y = random.nextInt(environmentHeight);
			double direction = random.nextDouble() * 2 * Math.PI;
			NetworkDrivenAgent agent = new NetworkDrivenAgent(x,y,direction);
			agent.setBrain(brain);
			environment.addAgent(agent);
		}
	}

	private static void initializeFood(int foodCount) {
		int environmentWidth = environment.getWidth();
		int environmentHeight = environment.getHeight();

		for (int i = 0; i < foodCount; i++) {
			Food food = createRandomFood(environmentWidth, environmentHeight);
			environment.addAgent(food);
		}
	}
	
	private static Food createRandomFood(int width, int height) {
		int x = random.nextInt(width);
		int y = random.nextInt(height);
		Food food = new Food(x, y);
		return food;
	}
	
	private static void initializeGeneticAlgorithm(
			int populationSize,
			int parentalChromosomesSurviveCount,
			GeneticTrainedNetwork baseNeuralNetwork) {
		Population<GeneticTrainedNetwork> brains = new Population<GeneticTrainedNetwork>();

		for (int i = 0; i < (populationSize - 1); i++) {
			if (baseNeuralNetwork == null) {
				brains.addChromosome(NetworkDrivenAgent.randomNeuralNetworkBrain());
			} else {
				brains.addChromosome(baseNeuralNetwork.mutate());
			}
		}
		if (baseNeuralNetwork != null) {
			brains.addChromosome(baseNeuralNetwork);
		} else {
			brains.addChromosome(NetworkDrivenAgent.randomNeuralNetworkBrain());
		}

		Fitness<GeneticTrainedNetwork, Double> fit = new GeneticEnvironmentFitness();

		ga = new GeneticAlgorithm<GeneticTrainedNetwork, Double>(brains, fit);

		addGASystemOutIterationListener();

		ga.setParentChromosomesSurviveCount(parentalChromosomesSurviveCount);
	}

	private static void addGASystemOutIterationListener() {
		ga.addIterationListener(new IterartionListener<GeneticTrainedNetwork, Double>() {
			@Override
			public void update(GeneticAlgorithm<GeneticTrainedNetwork, Double> ga) {
				GeneticTrainedNetwork bestBrain = ga.getBest();
				Double fit = ga.fitness(bestBrain);
				System.out.println(ga.getIteration() + "\t" + fit);

				ga.clearCache();
			}
		});
	}

	private static void setAgentBrains(NeuralNetwork newBrain) {
		for (NetworkDrivenAgent agent : environment.filter(NetworkDrivenAgent.class)) {
			agent.setBrain(newBrain.clone());
		}
	}
	
	
	//TODO: evolve ! 
	

}
