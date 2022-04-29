package project.messages;

import java.util.Date;

import project.Passenger;

/**
 * MoveTo message class that will be used to send messages from the Scheduler to
 * the Elevator. Created by the Scheduler when it sends the Elevator a floor.
 */
public class MoveTo extends Message {
	public final int destinationFloor;
	public final Passenger passenger;

	/**
	 * Constructor that specifies what floor to move to, as well as the time this
	 * event was created.
	 * 
	 * @param destinationFloor the destination floor
	 * @param eventTimeStamp   the event time stamp
	 */
	public MoveTo(int destinationFloor, Date eventTimeStamp, Passenger passenger) {
		super(eventTimeStamp);
		this.destinationFloor = destinationFloor;
		this.passenger = passenger;
	}

	/**
	 * To string method.
	 */
	@Override
	public String toString() {
		String str = super.toString();
		str += " destinationFloor=" + destinationFloor;
		return str;
	}
}
