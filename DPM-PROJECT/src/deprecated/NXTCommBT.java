package deprecated;

import java.io.*;

import javax.bluetooth.RemoteDevice;

import lejos.nxt.*;
import lejos.nxt.comm.*;

/**
 * class used to facilitate communication between Master and Slave bricks
 * 
 * @author Francois
 * @deprecated
 * 
 */
public class NXTCommBT {

	// class variables
	/**
	 * data input stream
	 */
	private static DataInputStream dis;
	/**
	 * data output stream
	 */
	private static DataOutputStream dos;
	/**
	 * object to connect NXT via bluetooth
	 */
	private static BTConnection connection;

	/**
	 * connect to other NXT brick
	 * 
	 * @param name
	 *            - remote NXT's friendly name
	 * @param isMaster
	 *            - boolean identifying if the NXT is the initiator(master) or
	 *            receiver(slave)
	 */
	public static void connect(String receiver, boolean isInitiator) {

		LCD.drawString("Connecting...", 0, 0);

		/*
		 * set up connection to other NXT brick via a bluetooth connection.
		 * Connection I/0 mode is PACKET. (recommended by lejos for inter-brick
		 * comm) The master will initiate the connection while the slave will
		 * wait for the connection to be instantiated
		 */
		if (isInitiator) {
			RemoteDevice btrd = Bluetooth.getKnownDevice(receiver);

			// check for connection error
			if (btrd == null) {
				LCD.clear();
				LCD.drawString("No such device", 0, 0);
				Button.waitForAnyPress();
				System.exit(1);
			}
			// initiate connection
		    connection = Bluetooth.connect(btrd);
		    
		    // check if connection has succeeded
		    if (connection == null) {
		        LCD.clear();
		        LCD.drawString("Connect fail", 0, 0);
		        Button.waitForAnyPress();
		        System.exit(1);
		      }
		    
		    LCD.clear();
		    LCD.drawString("Connected", 0, 0);
			
		} else {
			connection = Bluetooth.waitForConnection();
			
			LCD.clear();
		    LCD.drawString("Connected", 0, 0);
		}

		// get data i/o streams and open them
		dis = connection.openDataInputStream();
		dos = connection.openDataOutputStream();

		LCD.clear();
	}

	/**
	 * write onto output stream
	 * 
	 * @param data
	 *            - integer to be written
	 */
	public static void write(int data) {
		try {
			LCD.clear();
			LCD.drawString("Writing...", 0, 2);
			dos.writeInt(data);
			dos.flush();
		} catch (IOException ioe) {
			LCD.drawString("Write Exception", 0, 3);
		}
	}

	/**
	 * read from input stream
	 * 
	 * @return data
	 */
	public static int read() {

		int data = 0;

		try {
			LCD.clear();
			LCD.drawString("Reading...", 0, 3);
			data = dis.readInt();
		} catch (IOException ioe) {
			LCD.drawString("Read Exception ", 0, 4);
		}
		return data;
	}

	/**
	 * terminate communication with the remote NXT
	 */
	public static void disconnect() {
		// close data streams and connection
		try {
			LCD.clear();
			LCD.drawString("Closing... ", 0, 5);
			dis.close();
			dos.close();
			connection.close();
		} catch (IOException ioe) {
			LCD.drawString("Close Exception", 0, 6);
		}
	}
}
