package official;

/**
 * tower building class.
 * go deposit stacked blocks to the construction zone
 * @author François Lemay
 *
 */
public class TowerBuilding {
	
	// class variables
	/**
	 * robot's navigation class
	 */
	private Navigation nav;
	
	public TowerBuilding(Navigation nav){
		this.nav = nav;
		
	}
	
	/**
	 * go deposit stacked blocks to the construction zone
	 */
	public void deliverTower(){
		
		// return to construction zone
		
		// position for deposit
		
		// tell Slave to deposit blocks
		// write to DOS
		NXTComm.write(9);
		
	}

}
