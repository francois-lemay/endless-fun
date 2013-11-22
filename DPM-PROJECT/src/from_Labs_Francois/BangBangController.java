package from_Labs_Francois;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.*;

public class BangBangController implements UltrasonicController{
	private final int bandCenter, bandwith;
	private final int FILTER_OUT;
	private final int /*motorLow,*/ motorHigh;
	private final int motorStraight = 200;
	private final NXTRegulatedMotor leftMotor = Motor.A, rightMotor = Motor.C;
	private int distance;
	
	
	private int error;
	private int filterControl;  // used to count how many times the value 255 was read consecutively	
	
	public BangBangController(int bandCenter, int bandwith, int motorLow, int motorHigh) {
		//Default Constructor
		this.bandCenter = bandCenter;
		this.bandwith = bandwith;
		//this.motorLow = motorLow;    // will not use this variable
		this.motorHigh = motorHigh;
		leftMotor.setSpeed(motorStraight);
		rightMotor.setSpeed(motorStraight);
		leftMotor.forward();
		rightMotor.forward();
		error = 0;
		this.FILTER_OUT = 20;
	}
	
	public void processUSData(int distance) {
		
		// Filter for usSensor readings. i.e remove erroneous 255 values (same as the one used in PController)
		
		// the following if statement filters inconsistent readings of 255
		if (distance == 255 && filterControl < FILTER_OUT) {  
			// bad value, do not set the distance variable, however do increment the filter value
			filterControl ++;
					
		// the distance variable is set to 255 if the 255 value has been read FILTER_OUT times or more
		} else if (distance == 255){
			// true 255, therefore set distance to 255
			this.distance = distance;
		} else {
			// distance went below 255, therefore reset filterControl
			filterControl = 0;
			this.distance = distance;
		}
		//  END OF FILTER
	
		error = bandCenter - this.distance;
		
		// Speeds of motors are varied depending on bias (error)	
		
		if(Math.abs(error) <= bandwith){           //  Within range of error
			leftMotor.forward();                   // Set motors to spin forward
			rightMotor.forward();
			leftMotor.setSpeed(motorStraight);    //  Maintain same speed for
			rightMotor.setSpeed(motorStraight);   //  both motors
		}
		else if(error < 0){                            //  Too far from the wall
			leftMotor.forward();                 // Set motors to spin forward
			rightMotor.forward();
			leftMotor.setSpeed(motorStraight);
			rightMotor.setSpeed(motorHigh);       // Speed up right motor
		}
		else if(error > 0){                       // Too close to the wall
			leftMotor.forward();                  // Set left motor to spin forward
			leftMotor.setSpeed(motorStraight);    // and right motor to backward (for greater compensation
			rightMotor.backward();                // in concave corners)
			rightMotor.setSpeed(50);		
		}
	}

	public int readUSDistance() {        // Method called by Printer thread to display read distance
		return this.distance;
	}
}
