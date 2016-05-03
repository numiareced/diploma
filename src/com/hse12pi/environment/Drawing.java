package com.hse12pi.environment;
import java.awt.Color;
import java.awt.Graphics2D;

public class Drawing {
	
	private static int agentRadius = 7;
	
	private static int foodRadius = 4; 

	public static void paintEnvironment(Graphics2D canvas, AgentsEnvironment environment) {
		canvas.clearRect(0, 0, environment.getWidth(), environment.getHeight());
		canvas.setBackground(new Color(0, 102, 102));

		canvas.setColor(new Color(200, 30, 70));
		for (Food food : environment.filter(Food.class)) {
			int x = (int) food.getX();
			int y = (int) food.getY();

			canvas.fillOval(x - foodRadius, y - foodRadius, foodRadius * 2, foodRadius * 2);
		}
//drawing each of the agents with it's own color 
		//nn agent
		canvas.setColor(Color.GREEN);
		for (NetworkDrivenAgent agent : environment.filter(NetworkDrivenAgent.class)) {
			int x = (int) agent.getX();
			int y = (int) agent.getY();
			int rx = (int) ((agent.getRx() * (agentRadius + 4)) + x);
			int ry = (int) ((agent.getRy() * (agentRadius + 4)) + y);
			canvas.drawOval(x - agentRadius, y - agentRadius, agentRadius * 2, agentRadius * 2);
			canvas.drawLine(x, y, rx, ry);
			canvas.fillOval(x - agentRadius, y - agentRadius, agentRadius * 2, agentRadius * 2);
		}

		
		//dt agent
		canvas.setColor(Color.YELLOW);
		for (TreeDrivenAgent agent : environment.filter(TreeDrivenAgent.class)) {
			int x = (int) agent.getX();
			int y = (int) agent.getY();
			int rx = (int) ((agent.getRx() * (agentRadius + 4)) + x);
			int ry = (int) ((agent.getRy() * (agentRadius + 4)) + y);
			canvas.drawOval(x - agentRadius, y - agentRadius, agentRadius * 2, agentRadius * 2);
			canvas.drawLine(x, y, rx, ry);
			canvas.fillOval(x - agentRadius, y - agentRadius, agentRadius * 2, agentRadius * 2);
		}
		//abc alg
		
		canvas.setColor(Color.MAGENTA);
		for (ABCDrivenAgent agent : environment.filter(ABCDrivenAgent.class)) {
			int x = (int) agent.getX();
			int y = (int) agent.getY();
			int rx = (int) ((agent.getRx() * (agentRadius + 4)) + x);
			int ry = (int) ((agent.getRy() * (agentRadius + 4)) + y);
			canvas.drawOval(x - agentRadius, y - agentRadius, agentRadius * 2, agentRadius * 2);
			canvas.drawLine(x, y, rx, ry);
			canvas.fillOval(x - agentRadius, y - agentRadius, agentRadius * 2, agentRadius * 2);
		}
		

	
	}

}
