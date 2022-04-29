package project;

import java.util.Date;

import project.messages.Direction;

/**
 * This is a container for simulated button presses used by the Floor class.
 *
 */
public class ButtonPress {
	public final Direction direction;
	public final Date eventTimeStamp;
	public final Passenger passenger;
	
	public ButtonPress(Direction direction, Date eventTimeStamp, Passenger passenger) {
		this.direction = direction;
		this.eventTimeStamp = eventTimeStamp;
		this.passenger = passenger;
	}
}
