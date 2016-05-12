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
import java.text.NumberFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.prefs.Preferences;

import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.text.NumberFormatter;

import com.hse12pi.geneticApproach.geneticAlgorithm.Fitness;
import com.hse12pi.geneticApproach.geneticAlgorithm.GeneticAlgorithm;
import com.hse12pi.geneticApproach.geneticAlgorithm.Population;
import com.hse12pi.geneticApproach.neuralnetwork.GeneticTrainedNetwork;
import com.hse12pi.geneticApproach.neuralnetwork.NeuralNetwork;

public class Main {

	private static AgentsEnvironment environment;
	private static GeneticAlgorithm<GeneticTrainedNetwork, Double> ga;

	private static Random random = new Random();

	private static int populationNumber = 0;

	private static volatile boolean play = false;
	
	private static volatile boolean resume = false;

	private static volatile boolean regenerateFood = true;
	
	private static int foodCount = 0; 

	private static int eatenFoodCount = 0;

	private static int count = 0;

	// UI
	// panels
	private static JFrame appFrame;
	private static JPanel environmentPanel;
	private static JPanel controlsPanel;
	private static JPanel testsPanel;

	// labels
	private static JLabel totalFood_label;
	private static JLabel totalTime_label;
	private static JLabel foodnum_label;
	private static JLabel agentsnum_label;
	private static JLabel abc_label;
	private static JLabel nn_label;
	private static JLabel dt_label;
	private static JLabel testSettings_label;
	private static JLabel settings_label;
	private static JLabel timeval_label;
	private static JLabel aver_label;
	private static JLabel percent_label;

	// text fields
	private static JFormattedTextField foodNum_field;
	private static JTextField agentNum_field;
	private static JTextField timerValue_field;
	public static JTextArea test_area;

	// buttons
	private static JButton startEnv_but;
	private static JButton stopEnv_but;
	private static JButton resetEnv_but;
	private static JButton runTest_but;
	private static JButton stopTest_but;

	// check boxes
	public static JCheckBox abcAgents_check;
	public static JCheckBox genAgents_check;
	public static JCheckBox dtAgents_check;
	public static JCheckBox agentsAverage_check;
	public static JCheckBox percent_check;

	// separators
	private static JSeparator sep1;
	private static JSeparator sep2;

	private static BufferedImage displayEnvironmentBufferedImage;

	private static Graphics2D displayEnvironmentCanvas;

	private static boolean evolving = false; 
	static Timer timer;

	public static void main(String[] args) throws Exception {
		int environmentWidth = 600;
		int environmentHeight = 400;
		initializeUI(environmentWidth, environmentHeight);
		displayUI();
	}

	private static void initializeCanvas(int environmentWidth, int environmentHeight) {
		displayEnvironmentBufferedImage = new BufferedImage(environmentWidth, environmentHeight,
				BufferedImage.TYPE_INT_RGB);
		displayEnvironmentCanvas = (Graphics2D) displayEnvironmentBufferedImage.getGraphics();
		displayEnvironmentCanvas.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	}

	private static void displayUI() {
		// put application frame to the center of screen
		appFrame.setLocationRelativeTo(null);
		appFrame.setVisible(true);
	}

	private static void initializeUI(int environmentWidth, int environmentHeight) {
		appFrame = new JFrame("Evolutionarry algorithms driven agents");
		NumberFormat amountFormat = NumberFormat.getNumberInstance();
		NumberFormatter numberFormatter = new NumberFormatter(amountFormat);
		numberFormatter.setValueClass(Long.class); // optional, ensures you will
													// always get a long value
		numberFormatter.setAllowsInvalid(false); // this is the key!!
		numberFormatter.setMinimum(0l);
		environmentPanel = new JPanel();
		controlsPanel = new JPanel();
		testsPanel = new JPanel();
		// labels
		totalFood_label = new JLabel();
		totalTime_label = new JLabel();
		foodnum_label = new JLabel();
		agentsnum_label = new JLabel();
		abc_label = new JLabel();
		nn_label = new JLabel();
		dt_label = new JLabel();
		testSettings_label = new JLabel();
		settings_label = new JLabel();
		timeval_label = new JLabel();
		aver_label = new JLabel();
		percent_label = new JLabel();
		// text fields
		foodNum_field = new JFormattedTextField(numberFormatter);
		agentNum_field = new JTextField();
		timerValue_field = new JTextField();
		test_area = new JTextArea(); 
		// checkboxes
		abcAgents_check = new JCheckBox();
		genAgents_check = new JCheckBox();
		dtAgents_check = new JCheckBox();
		agentsAverage_check = new JCheckBox();
		percent_check = new JCheckBox();
		// buttons
		startEnv_but = new JButton();
		stopEnv_but = new JButton();
		resetEnv_but = new JButton();
		runTest_but = new JButton();
		stopTest_but = new JButton();
		// separators
		sep1 = new JSeparator();
		sep2 = new JSeparator();

		// initializing:

		appFrame.setSize(845, 703);
		appFrame.setResizable(false);
		appFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		appFrame.setLayout(new BorderLayout());

		environmentPanel.setBackground(new java.awt.Color(0, 102, 102));
		environmentPanel.setPreferredSize(new java.awt.Dimension(environmentWidth, environmentHeight));
		environmentPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
		GroupLayout environmentPanelLayout = new javax.swing.GroupLayout(environmentPanel);
		environmentPanel.setLayout(environmentPanelLayout);
		environmentPanelLayout.setHorizontalGroup(environmentPanelLayout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 596, Short.MAX_VALUE));
		environmentPanelLayout.setVerticalGroup(environmentPanelLayout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 0, Short.MAX_VALUE));
		appFrame.add(environmentPanel, BorderLayout.CENTER);

		controlsPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
		appFrame.add(controlsPanel, BorderLayout.EAST);
		// controlsPanel.setLayout(new GridLayout(11, 1, 5, 5));

		testsPanel.setSize(environmentWidth, 300);
		testsPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
		test_area.setSize(testsPanel.getWidth(), testsPanel.getHeight());
		testsPanel.add(test_area);
		appFrame.add(testsPanel, BorderLayout.SOUTH);

		//totalFood_label = new JLabel("Eaten food total amount: " + eatenFoodCount, SwingConstants.LEFT);
		totalTime_label = new JLabel("Time: ", SwingConstants.LEFT);
		appFrame.add(totalTime_label, BorderLayout.NORTH);

		controlsPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

		settings_label.setText("Settings");

		foodNum_field.setText("10");
		foodNum_field.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				// foodNum_fieldActionPerformed(evt);
			}
		});

		foodnum_label.setText("Food number");

		agentsnum_label.setText("Agent number");

		agentNum_field.setText("10");
		/*
		 * agentNum_field.addActionListener(new java.awt.event.ActionListener()
		 * { public void actionPerformed(java.awt.event.ActionEvent evt) {
		 * //agentNum_fieldActionPerformed(evt); } });
		 */

		abcAgents_check.setBackground(new java.awt.Color(255, 0, 255));
		abcAgents_check.setText("ABC Driven Agents");
		abcAgents_check.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				// abcAgents_checkActionPerformed(evt);
			}
		});

		genAgents_check.setBackground(new java.awt.Color(102, 255, 0));
		genAgents_check.setText("NN + Genetic Driven Agents");
		genAgents_check.setActionCommand("");
		genAgents_check.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				// genAgents_checkActionPerformed(evt);
			}
		});

		dtAgents_check.setBackground(new java.awt.Color(255, 255, 0));
		dtAgents_check.setText("Decision Tree Driven Agents");
		dtAgents_check.setActionCommand("");
		dtAgents_check.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				// dtAgents_checkActionPerformed(evt);
			}
		});

		startEnv_but.setText("Start");
		startEnv_but.addActionListener(new ActionListener() {
		      public void actionPerformed(ActionEvent e) {
		        Thread thr = new Thread(){
		          @Override
		          public void run(){
		            try {
		              Thread.sleep(100);
		              startEnv_butActionPerformed(e);
		              System.out.println("Done");
		            } catch (Exception e) {
		              e.printStackTrace();
		            }
		            
		          }
		        };
		        thr.start();
		        
		      }
		    });

		resetEnv_but.setText("Reset");
		resetEnv_but.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				 Thread thr = new Thread(){
			          @Override
			          public void run(){
			            try {
			              Thread.sleep(100);
			              resetEnv_butActionPerformed(evt);
			              System.out.println("Done");
			            } catch (Exception e) {
			              e.printStackTrace();
			            }
			            
			          }
			        };
			        thr.start();
			}
		});

		stopEnv_but.setText("Stop");
		stopEnv_but.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				play = false;
				resume = true; 
				startEnv_but.setText("Resume");
			}
		});

		testSettings_label.setText("Test Settings");

		timerValue_field.setText("5");

		timeval_label.setText("Timer value ");

		agentsAverage_check.setText("Agent's average");

		percent_check.setText("% of food ");

		runTest_but.setText("Run Test");
		 runTest_but.addActionListener(new java.awt.event.ActionListener() {
	            public void actionPerformed(java.awt.event.ActionEvent evt) {
	            	Thread thr = new Thread(){
				          @Override
				          public void run(){
				            try {
				              Thread.sleep(100);
				              runTest_butActionPerformed(evt);
				              System.out.println("Done");
				            } catch (Exception e) {
				              e.printStackTrace();
				            }
				            
				          }
				        };
				        thr.start();
	                
	            }
	     });

		stopTest_but.setText("Stop Test");
		stopTest_but.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
            	Thread thr = new Thread(){
			          @Override
			          public void run(){
			            try {
			              Thread.sleep(100);
			              stopTest_butActionPerformed(evt);
			              System.out.println("Done");
			            } catch (Exception e) {
			              e.printStackTrace();
			            }
			            
			          }
			        };
			        thr.start();
                
            }
     });

		javax.swing.GroupLayout controlsPanelLayout = new javax.swing.GroupLayout(controlsPanel);
		controlsPanel.setLayout(controlsPanelLayout);
		controlsPanelLayout.setHorizontalGroup(controlsPanelLayout
				.createParallelGroup(
						javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(controlsPanelLayout.createSequentialGroup().addGroup(controlsPanelLayout
						.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(javax.swing.GroupLayout.Alignment.TRAILING, controlsPanelLayout
								.createSequentialGroup().addContainerGap()
								.addGroup(controlsPanelLayout
										.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
										.addComponent(sep1)
										.addGroup(javax.swing.GroupLayout.Alignment.LEADING, controlsPanelLayout
												.createSequentialGroup().addComponent(startEnv_but).addGap(8, 8, 8)
												.addComponent(stopEnv_but)
												.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 12,
														Short.MAX_VALUE)
												.addComponent(resetEnv_but))
										.addGroup(controlsPanelLayout.createSequentialGroup().addGap(5, 5, 5)
												.addComponent(sep2))))
						.addGroup(controlsPanelLayout.createSequentialGroup()
								.addGroup(controlsPanelLayout
										.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
												controlsPanelLayout.createSequentialGroup().addGap(78, 78, 78)
														.addComponent(settings_label))
										.addGroup(controlsPanelLayout.createSequentialGroup().addContainerGap()
												.addComponent(foodNum_field, javax.swing.GroupLayout.PREFERRED_SIZE, 26,
														javax.swing.GroupLayout.PREFERRED_SIZE)
												.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
												.addComponent(foodnum_label))
										.addGroup(controlsPanelLayout.createSequentialGroup().addContainerGap()
												.addComponent(agentNum_field, javax.swing.GroupLayout.PREFERRED_SIZE,
														26, javax.swing.GroupLayout.PREFERRED_SIZE)
												.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
												.addComponent(agentsnum_label))
										.addGroup(controlsPanelLayout.createSequentialGroup().addContainerGap()
												.addComponent(abcAgents_check))
										.addGroup(controlsPanelLayout.createSequentialGroup().addContainerGap()
												.addComponent(genAgents_check))
										.addGroup(controlsPanelLayout.createSequentialGroup().addContainerGap()
												.addComponent(dtAgents_check))
										.addGroup(controlsPanelLayout.createSequentialGroup().addGap(71, 71, 71)
												.addComponent(testSettings_label))
										.addGroup(controlsPanelLayout.createSequentialGroup().addContainerGap()
												.addGroup(controlsPanelLayout
														.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
														.addComponent(agentsAverage_check)
														.addGroup(controlsPanelLayout.createSequentialGroup()
																.addComponent(timerValue_field,
																		javax.swing.GroupLayout.PREFERRED_SIZE, 26,
																		javax.swing.GroupLayout.PREFERRED_SIZE)
																.addPreferredGap(
																		javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																.addComponent(timeval_label))
														.addComponent(percent_check))))
								.addGap(0, 0, Short.MAX_VALUE)))
						.addContainerGap())
				.addGroup(controlsPanelLayout.createSequentialGroup().addContainerGap().addComponent(runTest_but)
						.addGap(26, 26, 26).addComponent(stopTest_but)));
		controlsPanelLayout.setVerticalGroup(controlsPanelLayout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(controlsPanelLayout.createSequentialGroup().addComponent(settings_label)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(sep1, javax.swing.GroupLayout.PREFERRED_SIZE, 10,
								javax.swing.GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(controlsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(foodNum_field, javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(foodnum_label))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
						.addGroup(controlsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(agentNum_field, javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(agentsnum_label))
						.addGap(18, 18, 18).addComponent(abcAgents_check)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
						.addComponent(genAgents_check)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
						.addComponent(dtAgents_check).addGap(18, 18, 18)
						.addGroup(controlsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(startEnv_but).addComponent(resetEnv_but).addComponent(stopEnv_but))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(sep2, javax.swing.GroupLayout.PREFERRED_SIZE,
								javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(testSettings_label)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
						.addGroup(controlsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(timerValue_field, javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(timeval_label))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
						.addComponent(agentsAverage_check)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(percent_check)
						.addGap(18, 18, 18)
						.addGroup(controlsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(runTest_but).addComponent(stopTest_but))
						.addGap(0, 22, Short.MAX_VALUE)));

		javax.swing.GroupLayout TestsPanelLayout = new javax.swing.GroupLayout(testsPanel);
		testsPanel.setLayout(TestsPanelLayout);
		TestsPanelLayout.setHorizontalGroup(TestsPanelLayout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 0, Short.MAX_VALUE));
		TestsPanelLayout.setVerticalGroup(TestsPanelLayout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 253, Short.MAX_VALUE));

	}

	private static void startEnv_butActionPerformed(ActionEvent evt) throws Exception {
		// start button clicked, run agents
		if (! resume ){
		int environmentWidth = 600;
		int environmentHeight = 400;
		System.out.println("start");
		String agentNum = agentNum_field.getText();
		String foodNum = foodNum_field.getText();
		System.out.println(agentNum + foodNum);
		if (agentNum.isEmpty() || foodNum.isEmpty()) {
			System.out.println("Please fill all the fields before start!");
			// TODO: alert window on this one
		}else {
		int agentsCount = Integer.parseInt(agentNum);
		foodCount =  Integer.parseInt(foodNum);
		System.out.println("counts: " + agentsCount + " " + foodCount);
		boolean abcInit = abcAgents_check.isSelected();
		boolean nnInit = genAgents_check.isSelected();
		boolean dtInit = dtAgents_check.isSelected();
		if (!abcInit && !nnInit && !dtInit){
			System.out.println("please select brain!");
			// TODO: alert window on this one
		}else {
		initializeEnvironment(environmentWidth, environmentHeight, agentsCount, foodCount, abcInit, nnInit, dtInit);
		initializeCanvas(environmentWidth, environmentHeight);
		play = true; 
		mainEnvironmentLoop();
		}
		}
		}
		else {
			resume = false; 
			play = true; 
			startEnv_but.setText("Start");
		}

	}
	private static void resetEnv_butActionPerformed(ActionEvent evt){
		play = false;
		resume = true; 
		evolving = false;
		int environmentWidth = 600;
		int environmentHeight = 400;
		for (Agent agents : environment.filter(Agent.class)) {
			environment.removeAgent(agents);
		}
		for (Food food : environment.filter(Food.class)) {
			environment.removeAgent(food);
		}
		String agentNum = agentNum_field.getText();
		String foodNum = foodNum_field.getText();
		System.out.println(agentNum + foodNum);
		if (agentNum.isEmpty() || foodNum.isEmpty()) {
			System.out.println("Please fill all the fields before start!");
			// TODO: alert window on this one
		}else {
		int agentsCount = Integer.parseInt(agentNum);
		int foodCount =  Integer.parseInt(foodNum);
		
		System.out.println("counts: " + agentsCount + " " + foodCount);
		boolean abcInit = abcAgents_check.isSelected();
		boolean nnInit = genAgents_check.isSelected();
		boolean dtInit = dtAgents_check.isSelected();
		initializeEnvironment(environmentWidth, environmentHeight, agentsCount, foodCount, abcInit, nnInit, dtInit);
		}
		
	}
	
	 private static void runTest_butActionPerformed(java.awt.event.ActionEvent evt) {                                            
	        // TODO add your handling code here:
		    String timeField = timerValue_field.getText();
		    if (timeField.isEmpty()){
		    	System.out.println("please enter time value in minutes");
		    	//TODO alert message
		    }
		    int timeValue = Integer.parseInt(timeField);
		    if (timeValue !=0) {
		    	timer = new Timer();
		    	EatenFoodObserver.runTest = true; 
		    	resetCounters();
		    	timer.schedule(new TestRunner(), timeValue*1000*60); 
		    }
		    
		    
		    
	    }   
	 
	 private static void stopTest_butActionPerformed(ActionEvent evt) {
		  if (timer != null ){
			  timer.cancel();
			  EatenFoodObserver.runTest = false; 
			  System.out.println("Timer canceled!");
			  timer = null; 
		  }
	 }

	private static int stringToInt(String text) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	private static void resetCounters(){
		EatenFoodObserver.resetFoodCounts();
		for ( Agent agent: environment.filter(Agent.class)){
			agent.setEatenFoodCount(0);
		}
	}

	private static void initializeEnvironment(int environmentWidth, int environmentHeight, int agentsCount,
			int foodCount, boolean abcInit, boolean nnInit, boolean dtInit) {
		environment = new AgentsEnvironment(environmentWidth, environmentHeight);
		environment.addListener(new EatenFoodObserver() {
			protected void addRandomPieceOfFood(AgentsEnvironment env) {
				if (regenerateFood) {
					Food food = createRandomFood(env.getWidth(), env.getHeight());
					env.addAgent(food);
				}
			}
		});
		if (abcInit) {
			initializeAgents(null, agentsCount, 1);
			System.out.println("abc add");
		}
		if (nnInit) {
			int gaPopulationSize = 5;
			int parentalChromosomesSurviveCount = 1;
			evolving = true; 
			initializeGeneticAlgorithm(gaPopulationSize, parentalChromosomesSurviveCount);
			NeuralNetwork brain = ga.getBest();
			initializeAgents(brain, agentsCount, 2);
		}
		if (dtInit) {
			initializeAgents(null, agentsCount, 3);
			
		}
		initializeFood(foodCount);
	}

	private static void evolvePopulation() {
		ga.evolve();
		NeuralNetwork newBrain = ga.getBest();
		setAgentBrains(newBrain);

	}

	private static  void mainEnvironmentLoop() throws InterruptedException {
		for (;;) {
			Thread.sleep(50);
			if (evolving) {
				if (play) {
					environment.timeStep();
					count++;
					if ((count == 100)) {
						System.out.println("need to envolve");
						evolvePopulation();
						count = 0;

					}
				}
			} else {
				if (play) {
					environment.timeStep();
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

	private static void initializeAgents(NeuralNetwork brain, int agentsCount, int typeCount) {
		int environmentWidth = environment.getWidth();
		int environmentHeight = environment.getHeight();
		switch (typeCount) {
		case 1: { // abc
			System.out.println("add abc");
			for (int i = 0; i < agentsCount; i++) {
				int x = random.nextInt(environmentWidth);
				int y = random.nextInt(environmentHeight);
				double direction = random.nextDouble() * 2 * Math.PI;
				ABCDrivenAgent agent = new ABCDrivenAgent(x, y, direction, foodCount);
				environment.addAgent(agent);
			}
			System.out.println("added");
			break;

		}
		case 2: { // nn
			for (int i = 0; i < agentsCount; i++) {
				int x = random.nextInt(environmentWidth);
				int y = random.nextInt(environmentHeight);
				double direction = random.nextDouble() * 2 * Math.PI;
				NetworkDrivenAgent agent = new NetworkDrivenAgent(x, y, direction);
				agent.setBrain(brain);
				environment.addAgent(agent);
			}
			break;
		}
		case 3: { // dt
			for (int i = 0; i < agentsCount; i++) {
				int x = random.nextInt(environmentWidth);
				int y = random.nextInt(environmentHeight);
				double direction = random.nextDouble() * 2 * Math.PI;
				TreeDrivenAgent agent = new TreeDrivenAgent(x, y, direction);
				environment.addAgent(agent);
			}
			break;
		}
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

	private static void initializeGeneticAlgorithm(int populationSize, int parentalChromosomesSurviveCount) {
		Population<GeneticTrainedNetwork> brains = new Population<GeneticTrainedNetwork>();

		for (int i = 0; i < (populationSize - 1); i++) {
				brains.addChromosome(NetworkDrivenAgent.randomNeuralNetworkBrain());	
		}
		Fitness<GeneticTrainedNetwork, Double> fit = new GeneticEnvironmentFitness();

		ga = new GeneticAlgorithm<GeneticTrainedNetwork, Double>(brains, fit);
		ga.setParentChromosomesSurviveCount(parentalChromosomesSurviveCount);
	}


	private static void setAgentBrains(NeuralNetwork newBrain) {
		for (NetworkDrivenAgent agent : environment.filter(NetworkDrivenAgent.class)) {
			agent.setBrain(newBrain.clone());
		}
	}

	// TODO: evolve !

}
