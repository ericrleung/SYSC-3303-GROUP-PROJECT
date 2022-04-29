package project.messages;

import java.util.Date;

/**
 * ElevatorError message class that will be used to send messages from the
 * Elevator to the Scheduler. Created by the Elevator when doors get stuck
 */
public class ElevatorDoorsStuck extends Message {
	public final int elevatorId;
	public final int currentFloor;

	/**
	 * Constructor that specifies what floor the doors got stuck on, and what time
	 * this event was created.
	 */
	public ElevatorDoorsStuck(Date eventTimeStamp, int elevatorId, int currentFloor) {
		super(eventTimeStamp);
		this.elevatorId = elevatorId;
		this.currentFloor = currentFloor;
	}

	/**
	 * ToString method that makes this event understandable in the console logs.
	 */
	@Override
	public String toString() {
		String str = super.toString();
		str += " Elevator Doors Stuck ID: " + this.elevatorId + " Current Floor: " + this.currentFloor;
		return str;
	}
}
