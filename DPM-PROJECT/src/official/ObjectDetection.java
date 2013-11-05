package official;

public class ObjectDetection extends Thread{
	
	// class variables
	private LightPoller[] lp = new LightPoller[3];
	private USPoller[] up = new USPoller[2];
	
	/**
	 * detection status booleans
	 */
	public static boolean newObjectDetected;
	public boolean objectDetected, sameObject;
	
	/**
	 * identification status booleans
	 */
	public static boolean isBlock, isIdentifying;
	
	/**
	 * booleans that confirm presence of object at specified sensors
	 */
	private boolean bottom, top, left, right;
	
	/**
	 * strings used in LCDInfo
	 */
	private final String[] objectType = { "Block" , "Not Block" };
	
	
	// constructor
	public ObjectDetection(LightPoller[] lp, USPoller[] up){
		
		this.lp[0] = lp[0]; // back
		this.lp[1] = lp[1]; // left
		this.lp[2] = lp[2]; // right
		
		this.up[0] = up[0]; // bottom
		this.up[1] = up[1]; // top
	
	}
	
	/**
	 * main thread
	 */
	public void run(){
		
		// is objectDetected()
		
		// if yes, whichSensor()
		
		// go identify object
		
	}
	
	/**
	 * determines if an object is detected
	 * @return true or false
	 */
	private boolean isObjectDetected(){
		
		// check data from every sensor to see if an object is detected
		
		return  true;
	}
	
	/**
	 * set object detection status booleans according to
	 * which sensors have detected an object
	 * @return
	 */
	private void whichSensor(){
		
	}
	
	
	/**
	 * identify the detected object. Method of identification
	 * will vary according to the sensor that detected the object
	 * and the position of the object relative to the robot
	 */
	private void identifyObject(){
		
		
	}
	
	/**
	 * make robot avoid obstacle while staying on track
	 * to its destination
	 */
	private void avoidObstacle(){
		
	}
	

}
