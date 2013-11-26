package testing;

import java.io.IOException;

import official.BlockPickUp;
import lejos.nxt.Button;
import lejos.nxt.LCD;


/**
 * basic code for lift and clamp testing
 * @author Francois
 *
 */
public class LiftClampTesting {

	public static void main(String[] args) throws IOException{
		
		BlockPickUp.init();
		
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
					BlockPickUp.closeClamp();
					foo1 = false;
				} else {
					BlockPickUp.openClamp();
					foo1 = true;
				}
			}

			if(button == Button.ID_RIGHT){
				if (foo2) {
					BlockPickUp.raiseTo(BlockPickUp.IDLE);
					foo2 = false;
				} else {
					LCD.drawString("foooo", 0, 7);
					BlockPickUp.raiseTo(BlockPickUp.MIN_HEIGHT);
					foo2 = true;
				}
			}
			
			if(button == Button.ID_ESCAPE){
				System.exit(0);
			}
			
			Button.waitForAnyPress();
		}
	}

}
