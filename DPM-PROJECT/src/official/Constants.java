package official;

import lejos.nxt.MotorPort;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.SensorPort;
import bluetooth.PlayerRole;
import bluetooth.StartCorner;

/**
 * class of constants used throughout program
 * @author Francois
 *
 */
public class Constants {
	
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
	public static int[] greenZone;

	/**
	 * red zone is defined by these (bottom-left and top-right) corners:
	 */
	public static int[] redZone;
	
	// robot's physicial parameters
	
	/**
	 * left motor port
	 */
	public static MotorPort leftMotorPort = MotorPort.A;
	/**
	 * right motor port
	 */
	public static MotorPort rightMotorPort = MotorPort.B;
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
	 * distance between back light sensor and center of wheel base (in
	 * centimeters)
	 */
	public static final double BACK_SENSOR_DIST = 10;
	
	

	
	// SensorController polling periods
	/**
	 * polling period for Master's SensorController
	 */
	public static final int M_PERIOD = 20;
	/**
	 * polling period for Master's SensorController
	 */
	public static final int S_PERIOD = 20;
	
	
	// sensors' data sample sizes
	/**
	 * bottom us sensor sample size
	 */
	public static final int BOTT_SAMPLE = 7;
	/**
	 * top us sensor sample size
	 */
	public static final int TOP_SAMPLE = 7;
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
	public static final int BACK_DIFF = 6;
	
	/*
	 * threshold reading values 
	 */
	/**
	 * threshold derivative value used for gridline detection
	 */
	public static final int GRIDLINE_THRES = -50;
	
	
	// playing zone parameters
	
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
	
	
	// light localization parameters
	
	/**
	 * clocking position (x,y)
	 */
	public static final double [] clockingPos = {-6,0};
	
	
	
}
