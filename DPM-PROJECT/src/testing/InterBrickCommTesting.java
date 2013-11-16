package testing;

import java.io.*;
import lejos.nxt.*;
import lejos.nxt.comm.*;


public class InterBrickCommTesting {

	public static void main(String[] args) throws Exception{
		String name = "Master";
		RS485.setName(name);
		name = RS485.getName();
		LCD.drawString(name, 0, 1);
		
		Button.waitForAnyPress();
	}
		
		
		

}
