package project;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import project.communication.Connection;
import project.messages.ArrivedAtFloor;
import project.messages.ElevatorDoorsStuck;
import project.messages.ElevatorStuck;
import project.messages.MessageCreator;
import project.messages.Message;
import project.messages.MoveTo;
import project.messages.RequestCarButton;

/**
 * Elevator is the class for the elevator thread.
 */
public class Elevator implements Runnable {

    public static final int ELEVATOR_BUTTON_LAMPS = 22;
    /**
   * currentFloor: Current floor the elevator is on.
   * shouldDie: Determines if elevator thread should end.
   * destinationFloor: Next floor the elevator should go to.
   * shouldMove: Should the floor move next.
   * receiveEventTimestamp: Holds the time stamp of the message received by Scheduler.
   * messageQueue: Used to receive messages from scheduler.
   * state: Current state.
   */
  private int currentFloor;
  private boolean shouldDie;

  private Integer destinationFloor;
  private boolean shouldMove;
  private Date receiveEventTimestamp;

  private ElevatorState state;
  private boolean[] lamps;
  
  private Connection con;
  protected int elevatorId;
  private boolean isStuck;
  private boolean doorStuck;
  private ElevatorState previousState;
  private List<Passenger> passengers;
  private int moveTime;
  private int doorTime;
  /**
   * Class constructor specifying a message queue. Populates internal variables
   */
  public Elevator(int port, int moveTime, int doorTime) {
    this.currentFloor = 1;
    this.shouldDie = false;
    this.destinationFloor = null;
    this.shouldMove = false;
    this.setState(ElevatorState.SLEEPING);
    this.lamps = new boolean[ELEVATOR_BUTTON_LAMPS];
    this.con = new Connection("",port);
    this.isStuck = false;
    this.doorStuck = false;
    this.previousState = ElevatorState.SLEEPING;
    this.passengers = new ArrayList<>();
    this.moveTime = moveTime;
    this.doorTime = doorTime;
  }
  
  /**
   * Method called by ElevatorManager to set the time variables of the elevator.
   * @param moveTime an int representing the moveTime delay of an elevator.
   */
  public void setTimes(int moveTime, int doorTime) {
	  Util.log("SET TIMES " + moveTime + " " + doorTime);
	  this.moveTime = moveTime;
	  this.doorTime = doorTime;
  }
  
  /**
   * Move to method to poll scheduler for message queue. Checks to see if a job is
   * ready to be sent to the elevator.
   */
  private void checkIfMoveTo() {
	try {
	    Util.debug("checkIfMoveTo");
	    Message message = this.con.pollMessages(MessageCreator.ELEVATOR, elevatorId);

	    if (message == null) {
	        return;
        }
        Util.debug("checkIfMoveTo got msg.");

        MoveTo moveTo = (MoveTo) message;
        Util.log("Moving Elevator to Floor" + moveTo.destinationFloor);

        this.shouldMove = true;
        this.destinationFloor = moveTo.destinationFloor;
        this.lamps[this.destinationFloor-1] = true;
        this.receiveEventTimestamp = moveTo.eventTimeStamp;
        //Elevator becomes aware of a passenger and adds their info to its queue
        if (moveTo.passenger != null) {

        	for (Passenger p: this.passengers) {
            	if (p.getId() == moveTo.passenger.getId()) {
            		return;
            	}
            }
        	if (moveTo.destinationFloor == this.currentFloor) {
        		carButtonPress(moveTo.passenger.getCarButtonNum());
        	} else {
        		this.passengers.add(moveTo.passenger);
        	}
        	//activate fault flags if they were specified
        	if(moveTo.passenger.getFaultNum() == 1) {
        		//Door Faulted
        		doorStuck = true;
        		
        	}
        	else if(moveTo.passenger.getFaultNum() == 2) {
        		//Elevator Stuck/Out of Service
        		isStuck = true;
        	}
        	
        	
        }
	} catch (IOException e) {
		e.printStackTrace();
	}
  }
  
  
  /**
   * Send a stop request for the specific Elevator to the Scheduler
   * @param requestedFloor
   */
  public void carButtonPress(int requestedFloor) {
	  RequestCarButton message = new RequestCarButton(requestedFloor, new Date(), elevatorId);
	  //turn on the lamp for the requested floor
	  lamps[requestedFloor-1] = true;
	  try {
		this.con.sendMessage(message);
	} catch (IOException e) {
		e.printStackTrace();
	}
  }

  /**
   * Getter function to see if the elevator is in motion.
   *
   * @return moving.
   */
  public boolean isMoving() {
    return this.state == ElevatorState.MOVING;
  }

  /**
   * Getter function to see what floor the elevator is currently on.
   *
   * @return currentFloor.
   */
  public int getCurrentFloor() {
    return this.currentFloor;
  }

  /**
   * Function to set the state of the elevator.
   * @param state
   */
  private void setState(ElevatorState state) {
    this.state = state;
  }
  
  /**
   * initialize communication for Elevator Thread
   */
  private void ready() {
      Util.debug("ready");

	    con.init();
	    try {
	        this.elevatorId = con.register(MessageCreator.ELEVATOR);
	    } catch (IOException e) {
	        Util.log("Generic Send Error");
	    }

	    Util.log("Setup number: "+ elevatorId);

	    Thread.currentThread().setName("Elevator:"+ elevatorId);
  }

  /**
   * Run function to be called when the thread starts. Will continuously poll the
   * scheduler to see if the elevator should move to another floor.
   */
  @Override
  public void run() {
    Util.log("running");
    ready();
    try {
      while (!this.shouldDie) {

        Util.debug("Current state: " + state.toString());

        if (this.state == ElevatorState.SLEEPING)  {
          // Wait one second to avoid busy loop.
          Thread.sleep(1000);
        } else if (this.state == ElevatorState.MOVING) {

        	if(currentFloor > destinationFloor) {
        		currentFloor--;
        	}
        	else if (currentFloor < destinationFloor) {
        		currentFloor++;
        	}
        	Thread.sleep(moveTime);
        }
        else if (this.state == ElevatorState.MOVING_INTERMEDIATE){
            this.checkIfMoveTo();

            //perform logic of any passengers that got on elevator
            Passenger passenger = null;
            for (Passenger p: this.passengers) {
            	if (p.getFloorButtonNum() == this.currentFloor && !p.getOnElevator()) {
            		p.setOnElevator(true);
            		passenger = p;
            		carButtonPress(p.getCarButtonNum());
            		Util.log("Passenger has pressed car button number " + p.getCarButtonNum());
            		break;
            	}
            }
        	// Let the scheduler know when we've gone to an intermediate floor.
            ArrivedAtFloor arrivedMessage = new ArrivedAtFloor(this.currentFloor, this.receiveEventTimestamp, elevatorId, passenger);
            Util.log("moving intermediate current floor: " + this.currentFloor);
            this.lamps[this.currentFloor-1] = false;

            this.con.sendMessage(arrivedMessage);
        }
        else if (this.state == ElevatorState.CLOSING_DOOR){
            // Wait one second to simulate door closing
        	Util.log("Leaving Floor " + currentFloor + " Closing Elevator Doors");
        	
            Thread.sleep(doorTime);
        } else if (this.state == ElevatorState.OPENING_DOOR){
            // Wait one second to simulate door opening
        	Util.log("Arrived at Floor " + currentFloor + " Opening Elevator Doors");
            Thread.sleep(doorTime);
        } else if (this.state == ElevatorState.PROCESSING_RECEIVE) {
          this.checkIfMoveTo();
        } else if (this.state == ElevatorState.STUCK_DOOR) {
        	ElevatorDoorsStuck message = new ElevatorDoorsStuck(new Date(), elevatorId, currentFloor);
      	  	try {
      		  this.con.sendMessage(message);
	      	} catch (IOException e) {
	      		e.printStackTrace();
	      	}
      	  	Util.log("Elevator doors stuck");
        	// Sleep for 5s if door stuck
        	Thread.sleep(5000);
        	this.doorStuck = false;
        } else if (this.state == ElevatorState.STUCK_ELEVATOR) {
            for (Passenger p: this.passengers) {
            	if (!p.getOnElevator()) {
            		p.setOnElevator(true);
            		carButtonPress(p.getCarButtonNum());
            		Util.log("Passenger has pressed car button number " + p.getCarButtonNum());
            	}
            }
        	ElevatorStuck message = new ElevatorStuck(new Date(), elevatorId, currentFloor);
      	  	try {
      		  this.con.sendMessage(message);
	      	} catch (IOException e) {
	      		e.printStackTrace();
	      	}
        	// Kills the elevator
        	Util.log("Elevator stuck, terminating system");
        	this.shouldDie = true;
        }

        nextState();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    Util.log("exiting");
  }

  /**
   * Function that goes to next state based on event that was passed.
   * @throws Exception
   */
  public void nextState() throws Exception {
	  ElevatorState temp = this.state;
      if (this.isStuck) {
          this.setState(ElevatorState.STUCK_ELEVATOR);
      }
      else if (this.shouldMove) {
          this.shouldMove = false;
          this.setState(ElevatorState.CLOSING_DOOR);
      }
      else if (state == ElevatorState.SLEEPING) {
          this.setState(ElevatorState.PROCESSING_RECEIVE);
      }
      else if (state == ElevatorState.PROCESSING_RECEIVE) {
          this.setState(ElevatorState.SLEEPING);
      }
      else if (state == ElevatorState.MOVING) {
          this.setState(ElevatorState.MOVING_INTERMEDIATE);
      }
      else if (state == ElevatorState.MOVING_INTERMEDIATE) {
          if (currentFloor == destinationFloor) {
              this.setState(ElevatorState.OPENING_DOOR);
          } else {
              this.setState(ElevatorState.MOVING);
          }
      }
      else if (state == ElevatorState.CLOSING_DOOR) {
    	  if (this.doorStuck) {
    		  this.setState(ElevatorState.STUCK_DOOR);
    	  } else {
    		  this.setState(ElevatorState.MOVING);
    	  }
      }
      else if (state == ElevatorState.OPENING_DOOR) {
    	  if (this.doorStuck) {
    		  this.setState(ElevatorState.STUCK_DOOR);
    	  } else {
    		  this.setState(ElevatorState.SLEEPING);
    	  }
      } 
      else if (state == ElevatorState.STUCK_DOOR) {
    	  this.setState(this.previousState);
      } else {
          throw new IllegalArgumentException("Unexpected State: " + state);
      }
      this.previousState = temp;
  }

  /**
   * Method to set the stuck state of an elevator.
   * @param stuck A boolean representing if the elevator is stuck.
   */
  public void setStuck(boolean stuck) {
	  this.isStuck = stuck;
  }
  
  /**
   * Method to set the stuck state of a door.
   * @param stuck A boolean representing if the door is stuck.
   */
  public void setDoorStuck(boolean stuck) {
	  this.doorStuck = stuck;
  }
  /**
   * Method to stop thread.
   */
  public void kill() {
    Util.debug("--Elevator received kill--");
    this.shouldDie = true;
    this.con.close();
  }
  
  public ElevatorState getState() {
	  return this.state;
  }

  public static void main(String[] args) {
	  Util.LOG = true;

	  Elevator e1 = new Elevator(5076, 1000, 1000);
	  Thread et1 = new Thread(e1, "Elevator:n");
	  Elevator e2 = new Elevator(5076, 1000, 1000);
	  Thread et2 = new Thread(e2, "Elevator:n");
	  Elevator e3 = new Elevator(5076, 1000, 1000);
	  Thread et3 = new Thread(e3, "Elevator:n");
	  Elevator e4 = new Elevator(5076, 1000, 1000);
	  Thread et4 = new Thread(e4, "Elevator:n");

	  et1.start();
	  et2.start();
	  et3.start();
	  et4.start();
  }
}
