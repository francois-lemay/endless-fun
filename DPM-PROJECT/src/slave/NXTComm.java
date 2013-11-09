package slave;

import java.io.*;
import lejos.nxt.*;
import lejos.nxt.comm.*;
/**
 * Class used to facilitate communication between Master and Slave bricks
 * @author Francois
 *
 */
public class NXTComm {

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
	 * object to connect NXT via RS-485
	 */
	private static NXTConnection connection;
	
	
	/**
	 * connect to other NXT brick
	 * @param name - remote NXT's friendly name
	 * @param isMaster - boolean identifying if the NXT is the initiator(master) or receiver(slave)
	 */
	public static void connect(String name, boolean isInitiator) {

		/*
		 * set up connection to other NXT brick via a RS485 connection.
		 * Connection I/0 mode is PACKET. (recommended by lejos for inter-brick
		 * comm) The master will initiate the connection while the slave
		 * will wait for the connection to be instantiated
		 */
		if (isInitiator) {
			connection = RS485.getConnector().connect(name,
					NXTConnection.PACKET);
		} else {
			connection = RS485.getConnector().waitForConnection(0,
					NXTConnection.PACKET);
		}

		// check for connection error
		if (connection == null) {
			LCD.clear();
			LCD.drawString("No such device", 0, 0);
			Button.waitForAnyPress();
			System.exit(1);
		}
		
		// get data i/o streams and open them
		dis = connection.openDataInputStream();
		dos = connection.openDataOutputStream();
	}

	/**
	 * terminate communication with the remote NXT
	 */
	public static void disconnect() {
		// close data streams and connection
		try {
			LCD.drawString("Closing... ", 0, 0);
			dis.close();
			dos.close();
			connection.close();
			LCD.clear();
		} catch (IOException ioe) {
			LCD.drawString("Close Exception", 0, 0);
		}
	}
	
	/**
	 * read from input stream
	 * @return data
	 */
	public static int read(){
		
		int data = 0;
		
		try{
			LCD.drawString("Reading...", 0, 0);
			data = dis.readInt();
			LCD.clear();
		}
		catch(IOException ioe){
	        LCD.drawString("Read Exception ", 0, 0);
		}
		return data;
	}
	
	/**
	 * write onto output stream
	 * @param data - integer to be written
	 */
	public static void write(int data){
		try{
			LCD.drawString("Writing...", 0, 0);
			dos.writeInt(data);
			LCD.clear();
		}
		catch(IOException ioe){
			LCD.drawString("Write Exception", 0, 0);
		}
	}
}
