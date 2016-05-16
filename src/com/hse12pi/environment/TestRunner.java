package com.hse12pi.environment;

import java.util.TimerTask;

public class TestRunner extends TimerTask{
	
@Override
public void run() {
	// TODO Auto-generated method stub
	System.out.println("Timer Finished!");
	EatenFoodObserver.runTest = false; 
	//Main.timer2.stop();
	Main.test_area.append("Test have been finished");
	
	
}

}
