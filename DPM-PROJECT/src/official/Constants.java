package official;

import lejos.nxt.MotorPort;
import lejos.nxt.SensorPort;
import bluetooth.PlayerRole;
import bluetooth.StartCorner;

/**
 * class of constants used throughout program
 * @author François Lemay
 *
 */
public class Constants {
	
	// theLock
	public static class theLock extends Object {}
	/**
	 * global lock object
	 */
	public static theLock lockObject = new theLock();
	
	
	// constants provided prior to start of round
	/**
	 * starting corner
	 */
	public static StartCorner corner;
	/**
	 * player role
	 */
	public static PlayerRole role;
	/**
	 * green zone is defined by these (bottom-left and top-right)
	 * corners
	 */
	public static int[] goodZone;

	/**
	 * red zone is defined by these (bottom-left and top-right) corners:
	 */
	public static int[] badZone;
	
	/**
	 * robot's current destination (x,y)
	 */
	public static double[] robotDest = new double[2];
	
	// polling periods
	/**
	 * polling period for Master's SensorController
	 */
	public static final int M_PERIOD = 30;
	/**
	 * odometer's update period (in miliseconds)
	 */
	public static final int ODOMETER_PERIOD = 25;
	/**
	 * object detection period
	 */
	public static final int OBJ_DETECT_PERIOD = 200;
	
	
	/*
	 * ROBOT PHYSICAL PARAMETERS
	 */
	/**
	 * left wheel radius (centimeters)
	 */
	public static final double leftRadius = 2.11;
	/**
	 * right wheel radius (centimeters)
	 */
	public static final double rightRadius = 2.11;
	/**
	 * wheelbase width (centimeters)
	 */
	public static final double width = 15.65;
	/**
	 * distance between back light sensor and center of wheel base (in
	 * centimeters)
	 */
	public static final double BACK_SENSOR_DIST = 17.5;
	
	// port connections
	/**
	 * left motor port
	 */
	public static MotorPort leftMotorPort = MotorPort.C;
	/**
	 * right motor port
	 */
	public static MotorPort rightMotorPort = MotorPort.B;
	/**
	 * right motor port
	 */
	public static MotorPort sensorMotorPort = MotorPort.A;
	/**
	 * back light sensor's port
	 */
	public static SensorPort backSensorPort = SensorPort.S1;
	/**
	 * bottom us sensor's port
	 */
	public static SensorPort frontLightSensorPort = SensorPort.S4;
	/**
	 * left us sensor's port
	 */
	public static SensorPort leftSensorPort = SensorPort.S3;
	/**
	 * right us sensor's port
	 */
	public static SensorPort rightSensorPort = SensorPort.S2;


	
	// data indexing
	/**
	 * position of bottom light sensor in USPoller[] (in MASTER)
	 */
	public static final int bottomUSPollerIndex = 0;
	/**
	 * position of top light sensor in USPoller[] (in MASTER)
	 */
	public static final int topUSPollerIndex = 1;
	/**
	 * position of top light sensor in LightPoller[] (in SLAVE)
	 */
	public static final int leftLightPollerIndex = 0;
	/**
	 * position of top light sensor in LightPoller[] (in SLAVE)
	 */
	public static final int rightLightPollerIndex = 1;
	
	// sensors' data sample sizes
	/**
	 * us sensor number of samples in data linkedlist
	 */
	public static final int US_SAMPLE = 11;

	/**
	 * back light sensor sample size
	 */
	public static final int LIGHT_SAMPLE = 7;
	
	/*
	 * LOCALIZATION PARAMETERS
	 */
	
	/**
	 * clocking position (x,y)
	 */
	public static final double [] clockingPos = {-10,0};
	/**
	 * tweaking value (in degrees) for final
	 * correction in heading
	 */
	public static final int ANG_TWEAK = 5;
	/**
	 * tweaking value to compensate for line detection lag
	 */
	public static final int ANG_DELAY = -20;
	/**
	 * threshold derivative value used for gridline detection
	 */
	public static final int GRIDLINE_THRES = -30;
	/**
	 * robot's rotation speed used during us localization
	 */
	public static final int US_LOC_SPEED = 300;
	/**
	 * robot's rotation speed used during light localization
	 */
	public static final int LIGHT_LOC_SPEED = 200;
	/**
	 * distance considered as 'no wall present' (centimeters)
	 */
	public static final int NO_WALL = 60;
	/**
	 * distance considered as 'wall detected' (centimeters)
	 */
	public static final int WALL = 40;
	/**
	 * size of noise margin
	 */
	public static final int NOISE_MARGIN = 10;
	/**
	 * tweaking value (in degrees) for deltaTheta (in Falling Edge)
	 */
	public static final int FE_TWEAK = 0;
	/**
	 * tweaking value (in degrees) for deltaTheta (in Rising Edge)
	 */
	public static final int RE_TWEAK = 22;
	
	
	/*
	 * PLAYING ZONE PARAMETERS
	 */
	
	/**
	 * grid width (i.e. the number of squares along x-axis)
	 */
	public static int GRID_WIDTH = 12;

	/**
	 * grid length (i.e. the number of squares along y-axis)
	 */
	public static int GRID_LENGTH = 12;
	
	/**
	 * length of one square (in centimeters)
	 */
	public static final int SQUARE_LENGTH = 30;
	
	
	
	/*
	 * object detection threshold values
	 */
	
	/**
	 * distance read by us sensor at which an object is considered to be detected (in centimeters)
	 */
	public static final int US_OBJECT_THRESH = 30;

	/**
	 * light value read by light sensor at which an object is considered to be detected
	 */
	public static final int LIGHT_OBJECT_THRESH = 30;
	/**
	 * dist used for fine approach towards styro block
	 */
	public static final int FINE_APPROACH = 10;
	/**
	 * dist used for obstacle dodging
	 */
	public static final int OBSTACLE_PRESENT = 60;
	
	/*
	 * odometry correction parameters
	 */
	
	/**
	 * allowed bandwidth for odometry correction close to gridline intersections
	 */
	public static final double LINE_CROSS_BW = 0.3;
	
	
}
