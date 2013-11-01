/* Group 22
 * François Lemay  260 465 492
 * Dong Hee Kim    260 474 918
 * 
 * DESCRIPTION
 * 
 * Approaches an object to identify it once ObjecDectection
 * sets ObjectDetection.newObjectDetected to true.
 * 
 * Only the light sensor is used to distinguish a wooden block
 * from a styrofoam block.
 * 
 * An attempt was made to identify blocks using the us sensor,
 * however this part of the code was commented out due to its
 * poor reliability.
 * 
 * 
 */

package main;

import lejos.nxt.ColorSensor;
import lejos.nxt.ColorSensor.Color;
import lejos.nxt.Sound;
import lejos.nxt.comm.RConsole;
import lejos.util.Timer;
import lejos.util.TimerListener;


public class ObjectIdentification implements TimerListener{
	
	private Odometer odo;
	private Navigation navigator;
	private USPoller usPoller;
	private ColorSensor cs;
	
	private Timer timer;
	private final int PERIOD = 500;
		
	private static boolean isBlock;
	private static boolean isIdentifying;
	
	private final String[] objectType = { "Block" , "Not Block" };
	
	private final double SENSOR_DIST = 13.5; // dist of us sensor from wheel base

	/* NOT USED
	// parameters used for profileMatch()
	private final int SIDE_LENGTH = 10; // length of the sides
	private final int LENGTH_ERROR = 1; // permitted error on measurement of length
										// of the side of a block (using us Sensor)
	private final int SWEEP_ANGLE = 70; // angle on which the us sensor will scan the object
	
	private final int NEG_THRESHOLD = -10; // thresholds of discrete derivatives
	private final int POS_THRESHOLD = 10;  // to detect edges of an object
	
	private final int ANG_CORR = 0;  // correction value for lag in edge detection
	*/
	
	// parameters for colorMatch()
	private final int SAMPLE_SIZE = 3; // size of color samples
	private final double RATIO_THRESH = .80;  // threshold for blue:red color ratio
	private final int DIST_FROM_OBJECT = 25; // distance robot and object
	
	// parameters for fineApproach()
	private final int LIGHT_THRESH = 30; // change in ambient light (discrete derivative)
	

	// constructor
	public ObjectIdentification(Odometer odo, Navigation navigator, USPoller usPoller , ColorSensor cs){
		
		this.odo = odo;
		this.navigator = navigator;
		this.usPoller = usPoller;
		this.cs = cs;
		
		isBlock = false;
		isIdentifying = false;
		
		//set up timer
		timer = new Timer(PERIOD,this);
		startTimer();
	}
	
	
	// identify an object by scanning of the us sensor
	// and by identification of color with the light sensor
	public void timedOut() {

		if (ObjectDetection.isNewObjectDetected()) {

			// stop timer (prevents calling of timedOut() while in use)
			stopTimer();

			// set isIdentifying to true
			setIsIdentifying(true);
			
			// move forward by SENSOR_DIST for compensation
			navigator.moveForwardBy(SENSOR_DIST);
			
			// record position before moving out of search path
			ObjectSearching.setLastPos(new double[] { odo.getX(),
					odo.getY() });
			
			// turn robot towards object (i.e. north)
			navigator.turnTo(90);			

			// go identify object by color
			if (doesColorMatch()) {
				isBlock = true;
			}

			// set isIdentifying to false
			setIsIdentifying(false);

			// re-start timer
			startTimer();
		}
		
	}
	
	
	/*
	 * Check for match between object's color
	 * and the styrofoam block's color
	 */
	private boolean doesColorMatch(){
		
		double red = 0;
		double blue = 0;
		
	// ------------------------------------------------------------//
		RConsole.println("Begin COARSE Approach");
	// -----------------------------------------------------------//	
		
		// approach robot within DIST_FROM_OBJECT cm of object (COARSE approach)
		coarseApproach();
		
		// turn off US poller to save processing power
		usPoller.stopPolling();
					
	// ------------------------------------------------------------//
		RConsole.println("Begin FINE Approach");
	// -----------------------------------------------------------//	
		
		// begin FINE approach
		fineApproach();
		
	// ------------------------------------------------------------//
		RConsole.println("Begin color identification");
	// -----------------------------------------------------------//	
		
		// set floodlight and get color values
		cs.setFloodlight(true);
		
		for(int i=0;i<SAMPLE_SIZE; i++){
			Color color = cs.getRawColor();
			red += color.getRed();
			blue += color.getBlue();
		}
		// compute average readings
		red = red / SAMPLE_SIZE;
		blue = blue / SAMPLE_SIZE;
		
		// compute ratio
		double ratio = blue/red;
		
	// ------------------------------------------------------------//
		RConsole.println("red = "+red);
		RConsole.println("blue = "+blue);
		RConsole.println("ratio "+ ratio);
		RConsole.println("End of color reading");		
	// -----------------------------------------------------------//

		// turn US Poller back ON
		usPoller.startPolling();
		
		// determine if blue
		if(ratio > RATIO_THRESH){
			Sound.beep();
			
		// turn off floodlight
		cs.setFloodlight(false);
			
		// TODO: start styrofoam block capture
		// BlockCapture.start();
			
			return true;
		}
		
		Sound.twoBeeps();
		return false;
	}
	
	/*
	 * Approach object until the us sensor reaches its threshold
	 * of accuracy (i.e. DIST_FROM_OBJECT)
	 */
	private void coarseApproach(){
		
		// slowly move forward
		navigator.setSpeeds(Navigation.SLOW,Navigation.SLOW);
		
		while(true){
			if(usPoller.getLatestMedian() < DIST_FROM_OBJECT){
				navigator.setSpeeds(0, 0);
				break;
			}
		}
	}
	
	
	
	/*
	 * Detect high level of reflected light as being
	 * close to an object 
	 */
	private void fineApproach(){
		
		int light;
		
		// read calibrated light value
		light = cs.getColor().getBlue();	

		if (light > LIGHT_THRESH) {
			navigator.setSpeeds(0, 0);
		} else {

			// move forward very slowly
			navigator.setSpeeds(Navigation.SUPER_SLOW, Navigation.SUPER_SLOW);

			// stop robot once it is sufficiently close to object
			while (true) {

				// read calibrated light value
				light = cs.getColor().getBlue();
				
				// ---------------------------------------------//
				RConsole.println("" + light);
				// --------------------------------------------//

				if (light > LIGHT_THRESH) {
					navigator.setSpeeds(0, 0);
					break;
				}
			}
		}
	}
	
	
	
	
	/* NOT USED (it doesn't work properly)
	 *  
	 * acquire profile of object and see if it matches the profile
	 * of the styrofoam block.
	 * NOTE: the detection of falling edges (in the derivative of
	 *       the us sensor readings is quicker to detect, hence that's 
	 *			what this method detects
	 */
	
/*	
	private boolean doesProfileMatch(){
		
		boolean edgeDetected = false;
		double[] edge1 = {0,0};
		double[] edge2 = {0,0};
		double length = 0;
		
		// record initial heading
		double initialTheta = odo.getAng();
		
		// rotate robot by SWEEP_ANGLE  degrees counterclockwise
		navigator.rotateBy(SWEEP_ANGLE, false);  // blocking rotation
				
		// then rotate back clockwise and begin scanning
		navigator.rotateBy(-SWEEP_ANGLE, true); // non-blocking rotation
		
		RConsole.println("start of scan");
		
		while(!edgeDetected){
						
			// detect a decrease in distance (i.e. edge of block)
			if(usPoller.searchForSmallerDerivative(NEG_THRESHOLD)){
				edgeDetected = true;
				
				// latch distance of the edge of the block
				edge1[0] = usPoller.getLatestMedian();
				RConsole.println("distance to edge 1 = "+edge1[0]);

				// latch heading of robot
				edge1[1] = odo.getAng();
				RConsole.println("heading1 = "+edge1[1]);

			}
		}
		
		edgeDetected = false;
		
		// turn to initial heading
		navigator.turnTo(initialTheta);
		
		// rotate robot by SWEEP_ANGLE  degrees clockwise
		navigator.rotateBy(-SWEEP_ANGLE, false);  // blocking rotation
				
		// then rotate back clockwise and begin scanning
		navigator.rotateBy(SWEEP_ANGLE, true); // non-blocking rotation
		
		
		while(!edgeDetected){
			
			// detect an increase in distance (i.e. other edge of block)
			if(usPoller.searchForSmallerDerivative(NEG_THRESHOLD)){
				edgeDetected = true;
				
				// latch distance of the edge of the block
				edge2[0] = usPoller.getLatestMedian();
				RConsole.println("distance to edge 2 = "+edge2[0]);
				
				// latch heading of robot
				edge2[1] = odo.getAng();
				RConsole.println("heading2 = "+edge2[1]);
				
			}
		}
		
		// turn to original orientation
		navigator.turnTo(initialTheta);
		
		// stop rotation even if rotateBy() has not
		// completed
		navigator.setSpeeds(0,0);
		
		// calculate magnitude of difference in heading
		double deltaTheta = edge1[1] - edge2[1];
		if(deltaTheta < 0){
			deltaTheta  = deltaTheta + 360;
		}
		
		// correct for lag in edge detection
		deltaTheta = deltaTheta - ANG_CORR;
		
		
		RConsole.println("deltaTheta = "+deltaTheta);
		
		// calculate squared length of the block (using cosine rule)
		length = edge1[0]*edge1[0] + edge2[0]*edge2[0] -2*edge1[0]*edge2[0]*Math.cos(Math.toRadians(deltaTheta));
		
		// calculate length of the block
		length = Math.sqrt(length);
		
		RConsole.println("block length = "+ length);
		
		// decide if it is a styrofoam block
		if(Math.abs(length - SIDE_LENGTH) <= LENGTH_ERROR){
			return true;
		}

		return false;
	}
*/	
	
	
	
	//---- accessors -----//
	
	public boolean isNewObjectDetected(){
		return ObjectDetection.isNewObjectDetected();
	}	
	public static boolean isBlock(){
		return isBlock;
	}
	
	// return whether or not the robot is identifying
	// an object
	public static boolean isIdentifying() {
		return isIdentifying;
	}
	
	// return String for type of object detected
	// Return 'null' if identification is in process
	// (used by LCD Info)
	public String getStatus(){
		if(isBlock && !isIdentifying()){
			return objectType[0];								 
		}
		else if(!isIdentifying()){
			return objectType[1];
		}
		return "                      ";  // return null if no object detected 
	}
	
	
	//  ---- mutators ----//
	
	// set value of isIdentifying
	private void setIsIdentifying(boolean foo){
			isIdentifying = foo;
	}
	
	// start timer
	private void startTimer() {
		timer.start();
	}

	// stop timer
	private void stopTimer() {
		timer.stop();
	}
	
	
}
