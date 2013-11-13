package from_Labs_Ahmad;

import lejos.nxt.ColorSensor;
import lejos.nxt.LCD;
import lejos.nxt.LightSensor;
import lejos.nxt.SensorPort;
import lejos.nxt.comm.RConsole;
import lejos.robotics.Color;


public class ObjectDetector extends Thread {
	
	private static int UPDATE_PERIOD = 20;

	static ColorSensor censor = new ColorSensor(SensorPort.S1);
	private int currentSample,numSamples;	



public ObjectDetector(){
	
	censor.setFloodlight(true);
	censor.setFloodlight(Color.WHITE);


	}
	
public void run(){
	RConsole.openUSB(10000);
	long updateStart, update;
	
	boolean objectDetected = false;
	boolean obstacle = false;
	boolean foam = false;
	int buffer = 0;

	
	

		
	
	while (true) {
		
		updateStart = System.currentTimeMillis();
		
		
		ColorSensor.Color vals = censor.getColor();
	    ColorSensor.Color rawVals = censor.getRawColor();
	    int lightValue = vals.getBlue();
	    int rawBlue = rawVals.getBlue();
	    
		RConsole.println("Light Value"+ vals.getBlue());
		RConsole.println("Raw value " + rawVals.getBlue());
		
		if(lightValue > 50){
			buffer++;
			if(buffer == 3){
				objectDetected = true;
			}
			
			if(objectDetected == true){
				
				if(rawBlue >= 450 && rawBlue <= 550){
					RConsole.println("foam");
					
					LCD.drawString("	foam	" + rawBlue, 0, 1);
					
					foam = true;
					
				}else{
					obstacle = true;
					LCD.drawString("	Obstacle	" + rawBlue, 0, 1);
					RConsole.println(" obstacle");
					
				}
				
			}
			
		}else{
			buffer = 0;
			objectDetected = false;
			obstacle = false;
			foam = false;
		}
		
		
		
		
		
		update = System.currentTimeMillis();
		if (update - updateStart < UPDATE_PERIOD) {
			try {
				Thread.sleep(UPDATE_PERIOD
						- (update - updateStart));
			} catch (InterruptedException e) {

			}
		}
	}
	
	
	
}


}