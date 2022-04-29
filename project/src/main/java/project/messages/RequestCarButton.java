package project.messages;

import java.util.Date;

/**
 * A message request from a specific elevator to inform 
 * the Scheduler that the corresponding elevator must go there
 *
 */
public class RequestCarButton extends Message {
	public final int requestedFloor;
	public final int elevatorId;
	
	/**
	 * 
	 * @param requestedFloor the requested Floor by the corresponding car button
	 * @param eventTimeStamp the event timestamp
	 * @param elevatorId the id of the elevator that sent this message
	 */
	public RequestCarButton(int requestedFloor, Date eventTimeStamp, int elevatorId) {
		super(eventTimeStamp);
		this.requestedFloor = requestedFloor;
		this.elevatorId = elevatorId;
		
	}
	
	/**
	 * To String method
	 */
	@Override
	public String toString() {
		String str = super.toString();
		str += " elevatorId: " + elevatorId + " requestedFloor= " + requestedFloor;
		return str;
	}

}
