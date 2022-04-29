package project.messages;

import java.util.Date;

/**
 * Ready message class will be used when a floor or elevator wants to assign itself to the scheduler
 */
public class Ready extends Message {

	public final boolean sentByElevator;
	/**
	 * Constructor that specifies the ready floor, as well as the time this event
	 * was created.
	 * 
	 * @param c the class creating this message
	 * @param eventTimeStamp the event time stamp
	 */
	public Ready(MessageCreator c, Date eventTimeStamp) {
		super(eventTimeStamp);
		if(c == MessageCreator.ELEVATOR)
			this.sentByElevator = true;
		else
			this.sentByElevator = false;
	}

}
