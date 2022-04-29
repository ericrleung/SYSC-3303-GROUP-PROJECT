package project.messages;

import java.util.Date;

import project.Passenger;

/**
 * RequestElevator message class that will be used to send messages from the
 * Floor to the Scheduler. Created by the floor when it requests an elevator.
 */
public class RequestElevator extends Message {
	public final int currentFloor;
	public final Passenger passenger;
	public final boolean direction;

	/**
	 * RequestElevator constructor
	 * 
	 * @param currentFloor   the current floor requesting an elevator
	 * @param eventTimeStamp the event time stamp
	 */
	public RequestElevator(int currentFloor, Date eventTimeStamp, Passenger passenger, boolean direction) {
		super(eventTimeStamp);
		this.currentFloor = currentFloor;
		this.passenger = passenger;
		this.direction = direction;
	}

	/**
	 * To string method.
	 */
	@Override
	public String toString() {
		String str = super.toString();
		str += " currentFloor=" + currentFloor;
		return str;
	}
}
