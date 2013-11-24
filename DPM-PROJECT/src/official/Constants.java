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
	
	/*
	 * NXT COMMAND CODES
	 */
	/**
	 * order slave to open clamp
	 */
	public static final int CODE_OPEN_CLAMP = 49;
	/**
	 * order slave to close clamp
	 */
	public static final int CODE_CLOSE_CLAMP = 51;
	/**
	 * order slave to raise to IDLE
	 */
	public static final int CODE_RAISE_IDLE = 53;
	/**
	 * order Slave to lower to MIN
	 */
	public static final int CODE_LOWER_MIN = 55;
	/**
	 * order Slave to raise to MAX
	 */
	public static final int CODE_RAISE_MAX = 57;
	/**
	 * order Slave to raise by BLOCK HEIGHT
	 */
	public static final int CODE_RAISE_BLOCK_HEIGHT = 59;
	/**
	 * order Slave to lower by BLOCK HEIGHT
	 */
	public static final int CODE_LOWER_BLOCK_HEIGHT = 61;
	/**
	 * order Slave to exit program
	 */
	public static final int CODE_EXIT = 99;
	
	
	// polling periods
	/**
	 * polling period for Master's SensorController
	 */
	public static final int M_PERIOD = 30;
	/**
	 * polling period for Master's SensorController
	 */
	public static final int S_PERIOD = 20;
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
	public static final double leftRadius = 2.055;
	/**
	 * right wheel radius (centimeters)
	 */
	public static final double rightRadius = 2.055;
	/**
	 * wheelbase width (centimeters)
	 */
	public static final double width = 15.65;
	/**
	 * distance between back light sensor and center of wheel base (in
	 * centimeters)
	 */
	public static final double BACK_SENSOR_DIST = 11.1;
	
	// port connections
	/**
	 * left motor port
	 */
	public static MotorPort leftMotorPort = MotorPort.A;
	/**
	 * right motor port
	 */
	public static MotorPort rightMotorPort = MotorPort.B;
	/**
	 * right motor port
	 */
	public static MotorPort sensorMotorPort = MotorPort.C;
	/**
	 * back light sensor's port
	 */
	public static SensorPort backSensorPort = SensorPort.S1;
	/**
	 * bottom us sensor's port
	 */
	public static SensorPort bottomSensorPort = SensorPort.S2;
	/**
	 * top us sensor's port
	 */
	public static SensorPort topSensorPort = SensorPort.S3;
	/**
	 * clamp motor port
	 */
	public static MotorPort clampMotorPort = MotorPort.A;
	/**
	 * lift motor port
	 */
	public static MotorPort liftMotorPort = MotorPort.B;
	
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
	 * bottom us sensor sample size
	 */
	public static final int BOTT_SAMPLE = 9;
	/**
	 * top us sensor sample size
	 */
	public static final int TOP_SAMPLE = 9;
	/**
	 * left light sensor sample size
	 */
	public static final int LEFT_SAMPLE = 7;
	/**
	 * right light sensor sample size
	 */
	public static final int RIGHT_SAMPLE = 7;
	/**
	 * back light sensor sample size
	 */
	public static final int BACK_SAMPLE = 7;
	
	/*
	 * sensors' derivative sample sizes
	 */
	/**
	 * bottom us sensor derivative sample size
	 */
	public static final int BOTT_DIFF = 6;
	/**
	 * top us sensor derivative sample size
	 */
	public static final int TOP_DIFF = 6;
	/**
	 * left light sensor derivative sample size
	 */
	public static final int LEFT_DIFF = 6;
	/**
	 * right light sensor derivative sample size
	 */
	public static final int RIGHT_DIFF = 6;
	/**
	 * back light sensor derivative sample size
	 */
	public static final int BACK_DIFF = 2;
	
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
	public static final int ANG_TWEAK = 0;
	/**
	 * tweaking value to compensate for line detection lag
	 */
	public static final int ANG_DELAY = -5;
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
	public static final int LIGHT_LOC_SPEED = 250;
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
	public static final int RE_TWEAK = 16;
	
	
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
	 * BLOCK PICK-UP CONSTANTS
	 */
	/**
	 * lift motor speed
	 */
	public static final int LIFT_SPEED = 200;
	/**
	 * clamp motor speed
	 */
	public static final int CLAMP_SPEED = 100;
	/**
	 * motor acceleration
	 */
	public static final int LIFT_ACC = 2000;
	/**
	 * maximum block capacity
	 */
	public static final int MAX_BLOCK = 3;
	/**
	 * max allowed height for lift
	 */
	public static final int MAX_HEIGHT = 1500;
	/**
	 * min allowed height for lift
	 */
	public static final int MIN_HEIGHT = 0;
	/**
	 * height at which to keep lift while this class is not in use. (the given
	 * value is in degrees of motor rotation that translate in vertical
	 * displacement of the lift)
	 */
	public static final int IDLE = 1400;
	/**
	 * incremental height that corresponds to the height of one styrofoam block.
	 * (the given value is in degrees of motor rotation that translate in
	 * vertical displacement of the lift)
	 */
	public static final int BLOCK_HEIGHT = 720;
	/**
	 * limit angle of clamp motor considered as the open position
	 */
	public static final int OPEN_POS = 0;
	/**
	 * limit angle of clamp motor considered as the closed position
	 */
	public static final int CLOSED_POS = 50;
	
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
