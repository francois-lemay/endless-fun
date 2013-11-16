package testing;

import official.BlockPickUp;
import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.MotorPort;
import lejos.nxt.NXTRegulatedMotor;

/**
 * main class for lift and clamp testing
 * @author Francois
 *
 */
public class LiftClampTesting {

	public static void main(String[] args) {

		NXTRegulatedMotor lift = new NXTRegulatedMotor(MotorPort.A);
		NXTRegulatedMotor clamp = new NXTRegulatedMotor(MotorPort.B);
		NXTRegulatedMotor[] motors = { lift, clamp };

		BlockPickUp bp = new BlockPickUp(motors);



		int button;
		boolean foo1 = true;
		boolean foo2 = true;
		
		while (true) {
			LCD.clear();
			LCD.drawString("Choose task", 3, 0);
			LCD.drawString("< Left | Right >", 0, 2);
			LCD.drawString("       |        ", 0, 3);
			LCD.drawString(" Clamp | Lift   ", 0, 4);
			LCD.drawString("       |        ", 0, 5);
			
			Button.waitForAnyPress();
			button = Button.readButtons();

			if(button == Button.ID_LEFT){
				if (foo1) {
					bp.closeClamp();
					foo1 = false;
				} else {
					bp.openClamp();
					foo1 = true;
				}
			}

			if(button == Button.ID_RIGHT){
				if (foo2) {
					bp.raiseTo(BlockPickUp.IDLE);
					foo2 = false;
				} else {
					LCD.drawString("foooo", 0, 7);
					bp.raiseTo(BlockPickUp.MIN_HEIGHT);
					foo2 = true;
				}
			}
			
			if(button == Button.ID_ESCAPE){
				System.exit(0);
			}
			
			Button.waitForAnyPress();
		}
		/*
		 * // test raiseTo() LCD.drawString("Press to raise", 0, 0);
		 * LCD.drawString("lift to IDLE", 0, 1); Button.waitForAnyPress();
		 * bp.raiseTo(BlockPickUp.IDLE);
		 * 
		 * // test raiseBy() LCD.clear(); LCD.drawString("Press to raise", 0,
		 * 0); LCD.drawString("lift by HEIGHT", 0, 1); Button.waitForAnyPress();
		 * bp.raiseBy(BlockPickUp.BLOCK_HEIGHT);
		 * 
		 * // test lowerBy() LCD.clear(); LCD.drawString("Press to lower", 0,
		 * 0); LCD.drawString("lift by HEIGHT", 0, 1); Button.waitForAnyPress();
		 * bp.lowerBy(BlockPickUp.BLOCK_HEIGHT);
		 * 
		 * // lower to min height LCD.clear(); LCD.drawString("Press to lower",
		 * 0, 0); LCD.drawString("lift to MIN", 0, 1); Button.waitForAnyPress();
		 * bp.raiseTo(BlockPickUp.MIN_HEIGHT);
		 * 
		 * // raise to max height LCD.clear(); LCD.drawString("Press to raise",
		 * 0, 0); LCD.drawString("lift to MAX", 0, 1); Button.waitForAnyPress();
		 * bp.raiseTo(BlockPickUp.MAX_HEIGHT);
		 * 
		 * // test close clamp() LCD.clear(); LCD.drawString("Press to close",
		 * 0, 0); LCD.drawString("clamp", 0, 1); Button.waitForAnyPress();
		 * bp.closeClamp();
		 * 
		 * // test open clamp() LCD.clear(); LCD.drawString("Press to open", 0,
		 * 0); LCD.drawString("clamp", 0, 1); Button.waitForAnyPress();
		 * bp.openClamp();
		 * 
		 * // test close clamp() LCD.clear(); LCD.drawString("Press to close",
		 * 0, 0); LCD.drawString("clamp", 0, 1); Button.waitForAnyPress();
		 * bp.closeClamp();
		 * 
		 * while(true){ button = Button.readButtons(); if(button ==
		 * Button.ID_ESCAPE){ System.exit(0); } }
		 */
	}

}
