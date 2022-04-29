package project.messages;

import java.util.Date;

import project.Passenger;

/**
 * ArrivedAtFloor message class that will be used to send messages from the
 * Elevator to the Floor. Created by the Elevator when it reaches the
 * destination floor.
 */
public class ArrivedAtFloor extends Message {
	public final int currentFloor;
	public final int elevatorId;
	public final Passenger passenger;

	/**
	 * Constructor that specifies what floor the Elevator moved to, and what time
	 * this event was created.
	 */
	public ArrivedAtFloor(int currentFloor, Date eventTimeStamp, int elevatorId, Passenger passenger) {
		super(eventTimeStamp);
		this.currentFloor = currentFloor;
		this.elevatorId = elevatorId;
		this.passenger = passenger;
	}

	/**
	 * ToString method that makes this event understandable in the console logs.
	 */
	@Override
	public String toString() {
		String str = super.toString();
		str += " ArrivedAtFloor =" + currentFloor;
		return str;
	}

}
