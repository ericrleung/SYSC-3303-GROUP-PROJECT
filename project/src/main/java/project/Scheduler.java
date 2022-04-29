package project;

import project.communication.Connection;
import project.messages.*;

import java.io.IOException;
import java.net.DatagramPacket;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * Scheduler is the class for the scheduler thread.
 */
public class Scheduler implements Runnable {
	public static final int DEFAULT_MAIN_FLOOR = 1;
	/**
	 * shouldDie: Determines if scheduler thread should end.
	 * state: Current state
	 * sendQueue: Used for Elevator to receive messages.
	 * receiveQueue: Used to receive messages from Floor.
	 * floorStates: Keeps track of which floor is ready.
	 * floorQueues: An arraylist of Message queues where a new queue is created when a new floor is assigned
	 * elevatorQueues: An arraylist of Message queues where a new queue is created when a new elevator is assigned
	 */
	private boolean shouldDie;
	private SchedulerState state;


	private ArrayList<byte[]> signatures;

	private Connection con;
	private int floorCounter;
	private int elevatorCounter;

	private ArrayList<ConcurrentLinkedDeque<Message>> floorMessages;
	private ArrayList<ConcurrentLinkedDeque<Message>> elevatorMessages;


	private ConcurrentLinkedDeque<Message> receiveQueue;

	private Map<Integer, ElevatorInfo> elevators;
	private long holdNanoTime;
	private int processes;

	/**
	 * Class constructor. Populates internal variables.
	 */
	public Scheduler(int port) {
		this.con = new Connection(port);
		this.signatures = new ArrayList<>();
		this.elevators = new HashMap<>();

		// Default state.
		this.state = SchedulerState.RECEIVING;

		//initialize our message queues
		this.floorMessages = new ArrayList<ConcurrentLinkedDeque<Message>>();
		this.elevatorMessages = new ArrayList<ConcurrentLinkedDeque<Message>>();
		this.receiveQueue = new ConcurrentLinkedDeque<>();

		floorCounter = 0;
		elevatorCounter = 0;
		holdNanoTime = 0;
		processes = 0;
	}

	/**
	 * Sets the holdNanoTime variable to the current system nanoTime.
	 */
	public void setNanoTime(int processes) {
		this.processes = processes;
		this.holdNanoTime = System.nanoTime();
	}
	
	/**
	 * Run method to execute when thread starts.
	 */
	@Override
	public void run() {
		Util.log("running");
		con.init();
		try {
			while (!this.shouldDie) {
				//check for UDP messages

				Util.debug("Current state: " + state.toString());
				if (state == SchedulerState.PROCESSING) {
					processing();
				} else if(state == SchedulerState.RECEIVING) {
					getNextPacket();
				}
				else if (state == SchedulerState.SLEEPING) {
					Thread.sleep(50);
				} else {
					throw new Exception("Unexpected State");
				}
				nextState();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		Util.log("exiting");
	}

	/**
	 * Get next packet method used to retrieve next packet for the scheduler.
	 * @throws IOException
	 */
	private void getNextPacket() throws IOException {
		DatagramPacket packet = con.receiveWithTimeout();

		// If there was no packet in the OS queue.
		if (packet == null) {
			return;
		}

		// Get signature and send ack.
		byte[] sign = con.getSignature(packet.getData());

		for (byte[] array : signatures) {
			if (Arrays.equals(array, sign)) {
				//we already have that signature so no need to assign again
				return;
			}
		}

		signatures.add(sign);

		Message m = con.deserializeMessage(packet.getData());

		// Synchronously reply to ready and poll messages.
		if (m instanceof Ready) {
			processReadyMessage(m, sign);
		}
		else if (m instanceof Poll) {
			pollLogic((Poll) m, sign);
		} else {

			// We got an async RPC , add it to the receive queue
			this.con.acknowledge(sign);
			this.receiveQueue.add(m);
		}

	}

	/**
	 * Function that determines next state based on the state machine diagram.
	 * @throws Exception
	 */
	private void nextState() throws Exception {
		if (state == SchedulerState.RECEIVING) {
			state = SchedulerState.PROCESSING;
		}
		else if (state == SchedulerState.PROCESSING) {
			state = SchedulerState.SLEEPING;
		} else if (state == SchedulerState.SLEEPING) {
			state = SchedulerState.RECEIVING;
		} else {
			throw new Exception("Unexpected State");
		}
	}

	/**
	 * Returns the first free elevator
	 * @return
	 */
	public int selectElevator() throws Exception {
		// Iterate over all the elevators.
		for (Map.Entry<Integer, ElevatorInfo> entry : this.elevators.entrySet()) {
			ElevatorInfo info = entry.getValue();

			if (info.isFree() && !info.stuck) {
				// Return the id.
				return entry.getKey();
			}
		}
		throw new Exception("No free elevators");
	}

	/**
	 * A private helper function to check if we are finished.
	 */
	private void checkFinished() {
		// Boolean to check if all elevators are free.
		boolean allFree = true;
		for (Map.Entry<Integer, ElevatorInfo> entry : this.elevators.entrySet()) {
			ElevatorInfo info = entry.getValue();
			
			if(info.stuck) {
				break;	
			}
			else if (!info.isFree()) {
				// If at least one elevator is not free, set allFree to false.
				allFree = false;
				break;
			}
		}
		if (allFree && this.processes <= 0) {
			Util.log("Total processing time " + (System.nanoTime() - this.holdNanoTime));
		}
	}
	
	/**
	 * Tries to receive our connection packet
	 * Different logic depending on the message type
	 */
	public void processing() {
		if (this.receiveQueue.isEmpty()) {
			return;
		}

		Util.debug("receiveQueue.size() = " + this.receiveQueue.size());

		Message m = this.receiveQueue.poll();

		try {
			// register any unregistered elevator/floor
			if (m instanceof RequestElevator) {
				RequestElevator requestElevator = (RequestElevator) m;
				

				try {
					int selectedElevator = this.selectElevator();

					// Queue message for elevator.
					elevators.get(selectedElevator).addDestination(requestElevator.currentFloor, requestElevator.passenger);

					this.moveElevatorToNextDestination(selectedElevator);
					Util.info("Floor button request direction: " + requestElevator.currentFloor + " " + requestElevator.direction);
				} catch(Exception e){
					// Elevators are all busy.
					// Add the message back for later processing.
					this.receiveQueue.add(m);
				}

			} else if (m instanceof ArrivedAtFloor) {
				ArrivedAtFloor arrivedAtFloor = (ArrivedAtFloor) m;
				int floor = arrivedAtFloor.currentFloor;
				int elevatorId = arrivedAtFloor.elevatorId;
				Passenger passenger = arrivedAtFloor.passenger;

				// Queue message for floor.
				Util.info("Elevator " + elevatorId + " has arrived on floor "
						+ floor);

				// Track that we have arrived here.
				if (elevators.get(elevatorId).removeDestinationIfArrived(floor, passenger)) {
					// Only if the elevator arrived at the dest, and not an intermediate.
					Direction direction = elevators.get(elevatorId).computeDirection();

					Message elevatorArrived = new ElevatorArrived(arrivedAtFloor.currentFloor, arrivedAtFloor.eventTimeStamp, direction);
					floorMessages.get(arrivedAtFloor.currentFloor - 1).add(elevatorArrived);
					this.moveElevatorToNextDestination(elevatorId);
				}
				this.checkFinished();
			} else if (m instanceof RequestCarButton) {
				RequestCarButton requestCarButton = (RequestCarButton) m;
				// Decrement the number of processes on every CarButton request.
				// This is done for measurement to check when we are done with all requests.
				this.processes--;
				
				// Queue message for elevator.
				elevators.get(requestCarButton.elevatorId).addDestination(requestCarButton.requestedFloor, null);
				Util.log("Received car button request from elevatorId= "+ requestCarButton.elevatorId + " for floor= " + requestCarButton.requestedFloor);
				
			} else if (m instanceof ElevatorDoorsStuck) {
				ElevatorDoorsStuck elevatorDoorsStuck = (ElevatorDoorsStuck) m;
				Util.log("Type 1 Fault Elevator Doors Stuck ID: " + elevatorDoorsStuck.elevatorId + " Floor number: " + elevatorDoorsStuck.currentFloor);
			} else if (m instanceof ElevatorStuck) {
				ElevatorStuck elevatorStuck = (ElevatorStuck) m;

				// Transfer current destination floors to other elevators
				for (Message msg : elevatorMessages.get(elevatorStuck.elevatorId - 1)) {
					if (msg instanceof MoveTo) {
						MoveTo moveTo = (MoveTo) msg;

						// if passenger isn't null then this must be a floor request
						if (moveTo.passenger != null) {
							// add this floor message to the queue to let other elevators handle this
							RequestElevator request = new RequestElevator(moveTo.destinationFloor, moveTo.eventTimeStamp, moveTo.passenger,
									Direction.toBool(elevators.get(elevatorStuck.elevatorId).computeDirection()));

							// Add the message back for later processing.
							this.receiveQueue.add(request);
						}
					}
					
				}
				
				// Fill elevator with junk data to ensure scheduler does not send requests to this elevator
//				for (int i = 0; i < ElevatorInfo.MAX_PASSENGERS; i++) {
//					elevators.get(elevatorStuck.elevatorId).addDestination(i, null);
//				}
				elevators.get(elevatorStuck.elevatorId).stuck = true;
				//elevators.remove(elevatorStuck.elevatorId);
				Util.log("Type 2 Fault Elevator Got Stuck ID: " + elevatorStuck.elevatorId + " Floor number: " + elevatorStuck.currentFloor);
				this.processes--;
			} else {
				Util.log("Got unsupported message of type: " + m.getClass().getName());
				throw new Exception("Unknown message type.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			Util.log("Generic Message Exception");
		}
	}

	/**
	 * Sends move message to next destination.
	 * @param selectedElevator
	 */
	private void moveElevatorToNextDestination(int selectedElevator) {
		// Get the next destination.
		//Integer destination = elevators.get(selectedElevator).computeNextDestination().destination;
		// We have no where to go, we already at the floor.
		
		ElevatorInfoContent infoContent = elevators.get(selectedElevator).computeNextDestination();

		if (infoContent == null) { return; }

		Message moveTo = new MoveTo(infoContent.destination, new Date(), infoContent.passenger);

		// Move the elevator.
		elevatorMessages.get(selectedElevator - 1).add(moveTo);
		Util.info("Scheduler is sending elevator " +  selectedElevator + " to the floor " + infoContent.destination);
	}

	/**
	 * Method to get the next elevator ID
	 * @return
	 */
	private int getNextElevatorId() {
		elevatorCounter++;
		return elevatorCounter;
	}

    /**
     *  Logic for processing Ready messages
     * @param message : our Ready message
     * @param sign : sign of the message
     */
    private void processReadyMessage(Message message, byte[] sign) {
    	try {
        	if(message instanceof Ready &&
					((Ready) message).sentByElevator)
			{ // is an elevator
				int elevatorId = getNextElevatorId();

        		// add another queue to contain their messages
        		elevatorMessages.add(new ConcurrentLinkedDeque<>());

				ElevatorInfo info = new ElevatorInfo(elevatorId, DEFAULT_MAIN_FLOOR);
        		elevators.put(elevatorId, info);

        		Util.log("number of e queue: "+ elevatorMessages.size());
                con.acknowledge(sign,new Setup(new Date(),elevatorCounter));
        	}
        	else { // only other option is a scheduler
        		floorCounter++;
        		//add another queue to contain their messages
        		floorMessages.add(new ConcurrentLinkedDeque<>());
        		Util.log("number of f queue: "+ floorMessages.size());
                con.acknowledge(sign,new Setup(new Date(),floorCounter));
        	}
    	}
    	catch (Exception e) {
    		e.printStackTrace();
    	}

    	
    }

    /**
     * logic for handling the poll message
     * @param m: poll message that was received
     */
    private void pollLogic(Poll m, byte[] sign) {
    	if(m.isElevator) {
			try {
				ElevatorInfo info = elevators.get(m.number);

//				info.isFree = elevatorMessages.get(m.number-1).isEmpty();

				this.con.acknowledge(sign, elevatorMessages.get(m.number-1).poll());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else {
			try {
				this.con.acknowledge(sign, floorMessages.get(m.number-1).poll());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
    }

	/**
	 * If both queues are empty, this method returns true. Used to check if
	 * scheduler has any requests in it.
	 * 
	 * @return a boolean returning true if both queues are empty; false otherwise
	 */
	public boolean isFree() {
		return this.elevatorMessages.isEmpty() && this.floorMessages.isEmpty();
	}

	/**
	 * Method used to end thread.
	 */
	public void kill() {
		Util.debug("--Scheduler received kill--");
		this.con.close();
		this.shouldDie = true;
	}

	/**
	 * Returns an enum representing the state of the scheduler.
	 * @return an enum of the scheduler state
	 */
	public SchedulerState getState() {
		return this.state;
	}
	
	/**
	 * main method for testing Scheduler
	 * @param args
	 */
	public static void main(String[] args) {
		Scheduler s = new Scheduler(5076);

		Thread t1 = new Thread(s, "Scheduler");
		t1.start();
	}
}
