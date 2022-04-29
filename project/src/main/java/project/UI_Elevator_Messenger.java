package project;

import java.io.IOException;
import java.util.Date;

import project.communication.Connection;
import project.messages.EditElevatorTimes;

public class UI_Elevator_Messenger implements Runnable {
	
	private Connection connection;
	private boolean shouldDie;
	int moveTime;
	int doorTime;
	boolean fileSet;
	
	
	/**
	 * Initializes a messenger client for our UI
	 * @param port the Port number for our connection class
	 */
	public UI_Elevator_Messenger(int port) {
		this.connection = new Connection("", port);
		this.shouldDie = false;
		Util.LOG = true;
		moveTime = -1;
		doorTime = -1;
		fileSet = false;
	}
	/**
	 * Run the connection
	 */
	@Override
	public void run() {
    	connection.init();
		while(!this.shouldDie) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			checkTimeToSend();
		}
	}
	
	
	/**
	 * Checks if we have a file to send
	 */
	public void checkTimeToSend() {
		if(moveTime < 0 || doorTime < 0) {
			return;
		}
    	//send a message instead
    	try {
    		EditElevatorTimes editElevatorTimes = new EditElevatorTimes(new Date(), moveTime, doorTime);
			this.connection.sendMessage(editElevatorTimes);
			Util.log("Times changed");
			moveTime = -1;
			doorTime = -1;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Externally set the time for moving and doors.
	 * @param moveTime: The time to move
	 * @param doorTime: The time to open and close the door
	 */
	public void setTimes(int moveTime, int doorTime) {
		this.moveTime = moveTime;
		this.doorTime = doorTime;
	}
	
	
	public void kill() {
		this.shouldDie = true;
	}

}
