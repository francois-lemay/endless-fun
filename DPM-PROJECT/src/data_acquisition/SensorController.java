package data_acquisition;

import lejos.util.Timer;
import lejos.util.TimerListener;

/**
 * control center for light sensor and us sensors used in ObstacleAvoidance
 * and OdometryCorrection. Manages polling and filtering
 * of data collected from these sensors.
 * @author Francois
 *
 */
public class SensorController implements TimerListener {
	
	// class variables
	private Timer timer;
	private final int PERIOD;
	
	private LightSensor[] ls = new LightSensor[3];
	private USSensor[] us = new USSensor[1];
	private DataFilter filter;
	
	
	
	//constructor
	public SensorController(LightSensor back, USSensor left, LightSensor right, int period){
		ls[0] = back;
		ls[1] = right;
		us[0] = left;
		PERIOD = period;
		
		// set up timer
		timer = new Timer(PERIOD,this);
	}
	
	/**
	 * main thread
	 */
	public void timedOut(){
		
		collectRawData(ls,us);
		applyMedianFilter(ls, us);
		
		// apply derivative filter to back sensor and us sensor only
		applyDerivativeFilter(new LightSensor[] {ls[0]}, us);
		
		// call OdometryCorrection if back sensor detected a line
		
		
	}
	
	
	/**
	 * collect raw data from every sensor
	 * @param ls - array of LightSensor
	 */
	private void collectRawData(LightSensor[] ls, USSensor[] us){
		
		for(int i=0; i<ls.length ;i++){
			ls[i].getRawData();
		}
		
		for(int i=0; i<us.length ;i++){
			us[i].getRawData();
		}
	}
	
	/**
	 * pass every sensor's raw data through median filter
	 * @param ls - array of LightSensor
	 */
	private void applyMedianFilter(LightSensor[] ls, USSensor[] us){
		
		for(int i=0; i<ls.length;i++){
			ls[i].filteredReadings = filter.medianFilter(ls[i].rawData);
		}
		
		for(int i=0; i<us.length;i++){
			us[i].filteredReadings = filter.medianFilter(us[i].rawData);
		}
	}
	
	/**
	 * pass every sensor's raw data through a
	 * derivative filter	 
	 * @param ls - light sensor
	 */
	private void applyDerivativeFilter(LightSensor[] ls, USSensor[] us){
		
		for(int i=0; i<ls.length;i++){
			ls[i].derivatives = filter.derivativeFilter(ls[i].filteredReadings);
		}
		
		for(int i=0; i<us.length;i++){
			us[i].derivatives = filter.derivativeFilter(us[i].filteredReadings);
		}
	}
	
	
	// helper methods
	
	/**
	 * start main thread
	 */
	public void startPolling(){
		timer.start();
	}
	/**
	 * stop main thread
	 */
	public void stopPolling(){
		timer.stop();
	}
}
