package project.messages;

import java.util.Date;

public class EditElevatorTimes extends Message{

	public final int moveTime;
	public final int doorTime;
	
	/**
	 * 
	 * @param eventTimeStamp
	 */
	public EditElevatorTimes(Date eventTimeStamp, int moveTime, int doorTime) {
		super(eventTimeStamp);
		this.moveTime = moveTime;
		this.doorTime = doorTime;
	}
	

}
