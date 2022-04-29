package project.messages;

import java.io.Serializable;
import java.util.Date;

import project.Util;

/**
 * Generic message class.
 *
 */
public abstract class Message implements Serializable {
	// Unix time, seconds
	public final long time;
	public final Date eventTimeStamp;

	/**
	 * Constructor that specifies what time this event was created.
	 * 
	 * @param eventTimeStamp the event time stamp
	 */
	public Message(Date eventTimeStamp) {
		this.time = System.currentTimeMillis() / 1000L;
		this.eventTimeStamp = eventTimeStamp;
	}


	@Override
	public String toString() {
		return "TYPE= " + this.getClass().getName() + " CREATED=" + time + ", EVENTTIMESTAMP="
				+ Util.dateToString(eventTimeStamp);
	}
}
