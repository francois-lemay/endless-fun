/* Group 22
 * François Lemay  260 465 492
 * Dong Hee Kim    260 474 918
 * 
 * 
 * DESCRIPTION
 * 
 * Make the robot go through a systematic sweep of the grid,
 * in search of a styrofoam block.
 * 
 * NOTE: an obvious flaw with this is program is that there is no Avoid Obstacle class implemented yet
 * 		Therefore, if an object is placed along the defined search path, a collision is unavoidable
 * 
 * NOTE 2: We are assuming that we start at position (x,y) = (0,0)
 *  
 */



package main;

import lejos.nxt.comm.RConsole;
import lejos.util.Timer;
import lejos.util.TimerListener;

public class ObjectSearching implements TimerListener{
	
	private Navigation navigator;
	private Odometer odo;
	private Timer timer;
	private final int PERIOD = 500 , WAIT_TIME = 500;
	
	private static double[][] destinations = { {65,0} , {65,30} , {-5,30} , {-5,60} , {65,60} , {65,90} , {-5,90} };
	// these variables hold the current desired destination coordinates
	private double[] currentDst;
	// position of robot just before detecting a new object
	private static double[] lastPos;
	
	private int index; // index used for destinations
	
	// declare whether robot is ready for objectIdentification
	private static boolean isReady;
	// search interrupted status
	private static boolean searchInterrupted;
	
	public ObjectSearching(Odometer odo, Navigation navigator){
		
		this.odo = odo;
		this.navigator = navigator;
		
		currentDst = new double[2];
		lastPos = new double[2];
		
		index = 0;
		
		isReady = false;
		searchInterrupted = false;
		
		
		timer = new Timer(PERIOD,this);
		
	}

	public void timedOut() {

		// pause timer
		stopTimer();
		
		// orient US sensor toward 90deg (north)
		navigator.orientSensorNorth();

		// follow pre-defined search path when no object is detected
		if (!ObjectDetection.isNewObjectDetected()
				&& !Navigation.getIsNavigating()) {

			// if object was wood block, return to search path
			if (searchInterrupted) {
				isReady = false;
				
				// backup by 5cm to avoid collision with object
				navigator.moveForwardBy(-5);
				
				// return to search path exit point or next destination
				// depending on proximity
				if (destinationCloser()) {
					navigator.travelTo(currentDst[0], currentDst[1]);
					index++;
					if (index < destinations.length) {
						currentDst[0] = destinations[index][0];
						currentDst[1] = destinations[index][1];
					}else{
						RConsole.println("All destinations have been reached");
						System.exit(0);
					}
					
				} else {
					navigator.travelTo(lastPos[0], lastPos[1]);
				}
				
				searchInterrupted = false;
			}
			
			// rotate towards Destination
			navigator.turnTowards(currentDst[0], currentDst[1]);
			
			// orient US sensor towards 90deg (north)
			navigator.orientSensorNorth();
			
			// allow us sensor to get good reading
			try{Thread.sleep(WAIT_TIME);}
			catch(InterruptedException e){}
			
			// ready for object detection
			isReady = true;
			
			// travel to next destination
			navigator.travelTo(currentDst[0], currentDst[1]);
			
			// orientSensorNorth
			navigator.orientSensorNorth();

			// set forth on next destination if destination reached
			if (navigator.getDestinationReached()) {
				// increment index
				index++;
				// if not all destinations have been reached
				if (index < destinations.length) {
					currentDst[0] = destinations[index][0];
					currentDst[1] = destinations[index][1];
				}
			}
		} else {
			searchInterrupted = true;
		}

		// if not all destinations have been reached
		if(index < destinations.length){
			// re-start timer
			startTimer();
		} else{
			// search is ended
			RConsole.println("Search is ended");
			System.exit(0);
		}

	}
	
	// prepare robot for object hunting
	public void beginHunt(){
		
		// orient robot towards first destination
		navigator.turnTo(0);
		// orient sensor towards 0deg
		navigator.orientSensorNorth();
		// first destination
		currentDst[0] = destinations[index][0];
		currentDst[1] = destinations[index][1];
		// declare isReady for object detection
		isReady = true;
		// begin search
		startTimer();
	}
	
	// compute if next destination if closer than lastPos
	private boolean destinationCloser() {
		double x0,y0,x1,y1,x2,y2;
		
		// get update robot's current position and heading
		x0 = odo.getX();
		y0 = odo.getY();
		
		x1 = currentDst[0];
		y1 = currentDst[1];
		
		x2 = lastPos[0];
		y2 = lastPos[1];

		// find squared magnitude of displacement vectors
		double forwardError1 = Math
				.sqrt((x0-x1) * (x0-x1)
						+ (y0-y1) * (y0-y1));
		double forwardError2 = Math
				.sqrt((x0-x2) * (x0-x2)
						+ (y0-y2) * (y0-y2));
		
		if(forwardError1 < forwardError2){
			return true;
		}
		return false;
	}
	
	//-----accessors-----//
	
	public static boolean getIsReady(){
		return isReady;
	}
	
	
	//-----mutators-----//
	
	public static void setLastPos(double[] pos){
		lastPos[0] = pos[0];
		lastPos[1] = pos[1];
	}
	
	// only used by lab5 for object guessing
	public void setIsReady(boolean foo){
		isReady = foo;
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
