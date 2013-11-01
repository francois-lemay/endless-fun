package data_acquisition;

import lejos.util.Timer;
import lejos.util.TimerListener;

/**
 * control center for all light sensors. Manages polling and filtering
 * of data collected from these sensors.
 * @author Francois
 *
 */
public class LightSensorController implements TimerListener {
	
	// class variables
	private Timer timer;
	private final int PERIOD;
	
	private LightSensor[] ls = new LightSensor[3];
	private DataFilter filter;
	
	
	
	//constructor
	public LightSensorController(LightSensor back, LightSensor left, LightSensor right, int period){
		ls[0] = back;
		ls[1] = left;
		ls[2] = right;
		PERIOD = period;
		
		// set up timer
		timer = new Timer(PERIOD,this);
	}
	
	/**
	 * main thread
	 */
	public void timedOut(){
		
		collectRawData(ls);
		applyMedianFilter(ls);
		
		// apply derivative filter to back sensor only
		applyDerivativeFilter(new LightSensor[] {ls[0]});
		
		//
		
		
	}
	
	
	/**
	 * collect raw data from every light sensor
	 * @param ls - array of LightSensor
	 */
	private void collectRawData(LightSensor[] ls){
		
		for(int i=0; i<3 ;i++){
			ls[i].getRawData();
		}
	}
	
	/**
	 * pass each light sensor's raw data through median filter
	 * and through derivative filter
	 * @param ls - array of LightSensor
	 */
	private void applyMedianFilter(LightSensor[] ls){
		
		for(int i=0; i<3;i++){
			ls[i].filteredReadings = filter.medianFilter(ls[i].rawData);
		}
		
	}
	
	private void applyDerivativeFilter(LightSensor[] ls){
		
		for(int i=0; i<3;i++){
			ls[i].derivatives = filter.derivativeFilter(ls[i].filteredReadings);
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
