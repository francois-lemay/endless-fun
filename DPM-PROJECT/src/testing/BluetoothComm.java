package testing;

import lejos.nxt.Button;
import lejos.nxt.LCD;
import official.Constants;
import official.NXTCommBT;

public class BluetoothComm {

public static void main(String[] args) throws Exception{	
		
		// initialize connection
		commInit();
		
		// open clamp
		NXTCommBT.write(Constants.CODE_OPEN_CLAMP);
		Thread.sleep(1000);
		Button.waitForAnyPress();
		// close clamp
		NXTCommBT.write(Constants.CODE_CLOSE_CLAMP);
		Thread.sleep(1000);
		// lift to IDLE
		NXTCommBT.write(Constants.CODE_RAISE_IDLE);
		Thread.sleep(1000);
		// lower to MIN
		NXTCommBT.write(Constants.CODE_LOWER_MIN);
		Thread.sleep(1000);
		// exit
		NXTCommBT.write(Constants.CODE_EXIT);
		Thread.sleep(1000);
	}
		
	/**
	 * initialization of inter-brick communication
	 */
	public static void commInit() {
		String receiver = "Slave";

		LCD.drawString("Press to begin", 0, 0);
		Button.waitForAnyPress();
		LCD.clear();

		// connect with slave brick
		NXTCommBT.connect(receiver, true);
	}
		
		

	
}
