package objectIDtesting;

import lejos.nxt.LCD;
import lejos.nxt.SensorPort;
import lejos.nxt.UltrasonicSensor;
import lejos.util.Timer;
import lejos.util.TimerListener;

public class Test implements TimerListener{

	// class variables
	
	private Timer timer;
	private final int PERIOD = 20;
	private final int ERROR = 10;
	
	private UltrasonicSensor bottom, top;
	
	
	public Test(){
		
		bottom = new UltrasonicSensor(SensorPort.S1);
		top = new UltrasonicSensor(SensorPort.S2);
		
		timer = new Timer(PERIOD,this);
		timer.start();
	}
	
	public void timedOut(){
		LCD.clear();
		int foo1 = bottom.getDistance();
		int foo2 = top.getDistance();
/*		
		if(Math.abs(foo1-foo2)<= ERROR){
			LCD.drawString("is Block             ", 0, 4);
		}
		else{
			LCD.drawString("is Obstacle          ", 0, 4);
		}
*/		
		
		LCD.drawString("bottom ", 0, 0);
		LCD.drawInt(foo1, 8, 0);
		LCD.drawString("top ",0,1);
		LCD.drawInt(foo2,8,1);
		
	}
}