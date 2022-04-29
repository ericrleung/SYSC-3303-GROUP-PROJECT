package project;

import java.io.Serializable;

/**
 * Passenger class used to store information about car button numbers to press as well as fault numbers.
 * Used for communication between events and the elevator.
 *
 */
public class Passenger implements Serializable{
	private int carButtonNum;
	private int faultNum;
	private int floorButtonNum;
	private boolean onElevator;
	private int id;
	
	public Passenger(int bNum, int fNum, int fBNum, boolean onElevator, int id) {
		this.carButtonNum = bNum;
		this.faultNum = fNum;
		this.floorButtonNum = fBNum;
		this.onElevator = onElevator;
		this.id = id;
	}
	
	/**
	 * Returns the passenger's associated car button number.
	 * @return An int representing the car button to be pressed
	 */
	public int getCarButtonNum() {
		return this.carButtonNum;
	}
	
	/**
	 * Returns the passenger's associated fault number.
	 * @return An int representing the fault number
	 */
	public int getFaultNum() {
		return this.faultNum;
	}
	
	/**
	 * Returns the passenger's associated floor button number.
	 * @return An int representing the floor button number
	 */
	public int getFloorButtonNum() {
		return this.floorButtonNum;
	}
	
	/**
	 * Determines if passenger is on elevator.
	 * @return A boolean representing if passenger is on elevator
	 */
	public boolean getOnElevator() {
		return this.onElevator;
	}
	
	/**
	 * Sets the passenger's onElevator boolean.
	 * @param num A boolean representing if passenger is on elevator
	 */
	public void setOnElevator(boolean onElevator) {
		this.onElevator = onElevator;
	}
	
	/**
	 * Sets the passenger's associated fault number.
	 * @param num An int representing a fault number
	 */
	public void setFaultNum(int num) {
		this.faultNum = num;
	}
	
	/**
	 * Returns the id of a passenger.
	 * @return An int representing a passenger id
	 */
	public int getId() {
		return this.id;
	}
}
