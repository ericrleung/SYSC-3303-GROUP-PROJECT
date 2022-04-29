package project.messages;

import java.util.Date;

/**
 * ElevatorArrived message class that will be used to send messages from the
 * Elevator to the Scheduler. Created by the Elevator when it reaches the
 * destination floor.
 */
public class ElevatorArrived extends Message {
	public final int currentFloor;
	public final Direction direction;

	/**
	 * Constructor that specifies what floor the Elevator arrived at, and what time
	 * this event was created.
	 * 
	 * @param currentFloor   the current floor of the elevator
	 * @param eventTimeStamp the event time stamp
	 */
	public ElevatorArrived(int currentFloor, Date eventTimeStamp, Direction direction) {
		super(eventTimeStamp);
		this.currentFloor = currentFloor;
		this.direction = direction;
	}

	@Override
	public String toString() {
		String str = super.toString();
		str += " elevatorArrived at :" + currentFloor;
		return str;
	}
}
