package project.messages;

import java.util.Date;

public class ReadFile extends Message{

	public final String eventsFile;
	
	/**
	 * 
	 * @param eventTimeStamp
	 */
	public ReadFile(Date eventTimeStamp, String eventsFile) {
		super(eventTimeStamp);
		this.eventsFile = eventsFile;
	}
	

}
