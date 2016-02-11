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

public class Main {
	
	private static AgentsEnvironment environment;
	
	private static Random random = new Random();

	private static int populationNumber = 0;

	private static volatile boolean play = true;

	private static volatile boolean staticFood = true;

	private static volatile boolean regenerateFood = true;
	
	

	// UI

	private static JFrame appFrame;

	private static JPanel environmentPanel;

	private static JPanel controlsPanel;

	private static JTextField evolveTextField;

	private static JButton evolveButton;

	private static JButton playPauseButton;

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

		evolveTextField = new JTextField("10");
		controlsPanel.add(evolveTextField);

		playPauseButton = new JButton("pause");
		controlsPanel.add(playPauseButton);

		resetButton = new JButton("reset");
		controlsPanel.add(resetButton);

		populationInfoLabel = new JLabel("Population: " + populationNumber, SwingConstants.CENTER);
		appFrame.add(populationInfoLabel, BorderLayout.NORTH);

	}
	
	private static void initializeEnvironment(int environmentWidth, int environmentHeight, int agentsCount, int foodCount) {
		environment = new AgentsEnvironment(environmentWidth, environmentHeight);
		environment.addListener(new EatenFoodObserver() {
			@Override
			protected void addRandomPieceOfFood(AgentsEnvironment env) {
				if (regenerateFood) {
					Food food = createRandomFood(env.getWidth(), env.getHeight());
					env.addAgent(food);
				}
			}
		});
		initializeAgents(agentsCount);
		initializeFood(foodCount);
	}
	private static void mainEnvironmentLoop() throws InterruptedException {
		for (;;) {
			Thread.sleep(50);
			if (play) {
				environment.timeStep();
			}
			Drawing.paintEnvironment(displayEnvironmentCanvas, environment);
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					environmentPanel.getGraphics().drawImage(displayEnvironmentBufferedImage, 0, 0, null);
				}
			});
		}
	}
	
	private static void initializeAgents( int agentsCount) {
		int environmentWidth = environment.getWidth();
		int environmentHeight = environment.getHeight();

		for (int i = 0; i < agentsCount; i++) {
			int x = random.nextInt(environmentWidth);
			int y = random.nextInt(environmentHeight);
			double direction = random.nextDouble() * 2 * Math.PI;
			NetworkDrivenAgent agent = new NetworkDrivenAgent(x,y,direction);
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
	
	
	

}
