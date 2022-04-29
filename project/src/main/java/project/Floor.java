package project;

import project.communication.Connection;
import project.communication.MessageQueue;
import project.messages.MessageCreator;
import project.messages.Direction;
import project.messages.ElevatorArrived;
import project.messages.Message;
import project.messages.RequestElevator;

import java.io.IOException;
import java.util.Date;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Floor is the class for the floor thread.
 */
public class Floor implements Runnable {

	/**
	 * messageQueue: Used to send request to scheduler.
	 * number: Floor number
	 * shouldDie: Determines if Floor thread should end.
	 */
	private MessageQueue messageQueue;
	private boolean shouldDie;
	private boolean downLamp;
	private boolean upLamp;

	//private Connection connection;
	private Connection con;
	protected int setup_num;
	
	private Date eventTimeStamp;
	private Direction direction;
	private Passenger passenger;
	private ConcurrentLinkedQueue<ButtonPress> buttonPresses;

	/**
	 * Class constructor specifying a message queue. Populates class variables such
	 * as floor number, and if the thread should stay alive.
	 */
	public Floor(int port) {
		this.shouldDie = false;
		this.downLamp = false;
		this.upLamp = false;
		this.setup_num = -1;
		this.passenger = null;

	    this.con = new Connection("",port);
	    this.buttonPresses = new ConcurrentLinkedQueue<ButtonPress>();
	}

	/**
	 * Will only be used in unit tests for now to simulate the button being pressed
	 * on a floor. Puts it in a buffer that will be used by the scheduler.
	 * 
	 * @param direction   the direction of the pushed button
	 * @param passenger   the passenger being passed in
	 * @param eventTimestamp timestamp for the current event press
	 */
	public void pushButton(Direction direction, Date eventTimeStamp, Passenger passenger) { // used in junit tests
		Util.log(direction.name() + " pushed button");
		// create a new request elevator message
		
		Message message = new RequestElevator(this.setup_num, eventTimeStamp, passenger, Direction.toBool(direction));
		//this.messageQueue.send(message);
		try {
			this.con.sendMessage(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Util.info("Request elevator to floor: " + this.setup_num);
		if (direction == Direction.DOWN) {
			this.downLamp = true;
		} else {
			this.upLamp = true;
		}
	}
	
	/**
	 * simulate a button press with the given destination and timeStamp
	 * @param eventTimeStamp: button press timeStamp
	 * @param direction: The directional button that was pressed
	 */
	public void simulatedPress(Direction direction, Date eventTimeStamp, Passenger passenger) {
		this.buttonPresses.add(new ButtonPress(direction, eventTimeStamp, passenger));
	}
	
	/**
	 * check if a button has been pressed/button press simulated
	 */
	private void checkButtonPress() {
		ButtonPress buttonPress = this.buttonPresses.poll();
		if(buttonPress == null) {
			return;
		}
		pushButton(buttonPress.direction, buttonPress.eventTimeStamp, buttonPress.passenger);
	}

	/**
	 * Signifies to the scheduler that the Floor is there and ready to receive
	 * requests
	 */
	public void ready() {

	    try {
	        this.setup_num = con.register(MessageCreator.FLOOR);
	    } catch (IOException e) {
	    	e.printStackTrace();
	        Util.log("Failed to register");
	    }

	    Thread.currentThread().setName("Floor:"+ setup_num);
	    Util.log("Setup number: "+setup_num);

	}

	/**
	 * Request the elevator. Sends a message to the scheduler to request an elevator
	 * to move to this floor.
	 */
	public void requestElevator() {
		Util.log("requestElevator");
		Message message = new RequestElevator(this.setup_num, new Date(), this.passenger, false);
		messageQueue.send(message);
	}
	
	/**
	 * Fetches the current floor number
	 * @return the current Floors number
	 */
	public int getNum() {
		return setup_num;
	}

	/**
	 * Checks the scheduler receive queue for messages from the elevator
	 */
	private void checkIfConfirmed() {
		Message message = null;
		try {
			message = this.con.pollMessages(MessageCreator.FLOOR, setup_num);
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (message == null) {
			return;
		} else {
			ElevatorArrived arrivedAtFloor = (ElevatorArrived) message;
			Util.info("Got confirmation that elevator received our request to move to floor "
					+ arrivedAtFloor.currentFloor);
			if (arrivedAtFloor.direction == Direction.UP) {
				this.upLamp = false;
			} else {
				this.downLamp = false;
			}
		}

	}

	/**
	 * Run method for the floor. When the thread starts this method will run.
	 */
	@Override
	public void run() {
		Util.log("running");
		this.con.init();
		ready();
		try {
			while (!this.shouldDie) {
				// handleRequest();
				if(setup_num != -1) {
					this.checkIfConfirmed();
					checkButtonPress();
				}
				Thread.sleep(1000);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Util.log("exiting");
	}

	/**
	 * Method used to end thread.
	 */
	public void kill() {
		Util.debug("--Floor received kill--");
		this.shouldDie = true;
		this.con.close();
	}
	
	
	public static void main(String[] args) {
		//all floors have the same port number thanks to our connection class
				Floor f1 = new Floor(5076);
				Thread ft1 = new Thread(f1, "Floor:n");
				ft1.start();
				Floor f2 = new Floor(5076);
				Thread ft2 = new Thread(f1, "Floor:n");
				ft2.start();
	}
}
