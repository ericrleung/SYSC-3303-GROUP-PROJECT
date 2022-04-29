package project.communication;

import project.messages.Message;

/**
 * MessageQueue interface that acts as the parent class that the scheduler
 * message queue implements.
 */
public interface MessageQueue {
	/**
	 * Send a message to be received by the scheduler.
	 * 
	 * @param message the message to send to the scheduler
	 */
	public void send(Message message);

	/**
	 * Receive a message that will be sent to the Elevator or Floor.
	 */
	Message receive(Object object);
}
