package testing;

/**
 * send/receive testing between master and slave brick
 * @author Francois
 *
 */
public class InterBrickCommTesting {

	public static void main(String[] args) throws Exception{	
		
		// initialize connection
	//	commInit();
		
	/*	// open clamp
		NXTComm.write(Constants.CODE_OPEN_CLAMP);
		Thread.sleep(1000);
		// close clamp
		NXTComm.write(Constants.CODE_CLOSE_CLAMP);
		Thread.sleep(1000);
		// lift to IDLE
		NXTComm.write(Constants.CODE_RAISE_IDLE);
		Thread.sleep(1000);
		// lower to MIN
		NXTComm.write(Constants.CODE_LOWER_MIN);
		Thread.sleep(1000);
		// exit
		NXTComm.write(Constants.CODE_EXIT);
		Thread.sleep(1000);
		*/
	}
		
	/**
	 * initialization of inter-brick communication
	 */
	public static void commInit() {
	/*	// set master brick's friendly name
		RS485.setName("Master");

		String receiver = "Slave";

		LCD.drawString("Press to begin", 0, 0);
		Button.waitForAnyPress();
		LCD.clear();

		// connect with slave brick
		NXTComm.connect(receiver, true);
		*/
	}
		
		

}
