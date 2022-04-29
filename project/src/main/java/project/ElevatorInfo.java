package project;

import project.messages.Direction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * ElevatorInfo class used to keep track of elevator information.
 * Used by Scheduler
 *
 */
public class ElevatorInfo {
	public static final int MAX_PASSENGERS = 3;
	/**
	 * container for elevator info, referenced by the Scheduler
	 * 
	 * isFree : if the elevator's message queue is empty
	 * elevatorId: the number of the elevator
	 * elevatorFloor: the current floor of the elevator
	 * infoContents: a destination and passenger container for what is added to the Elevator
	 * 
	 */
	
	public int elevatorId;
	public int elevatorFloor;
	public boolean stuck;

//	private List<Integer> destinations;
//	private List<Passenger> passengers;
	private List<ElevatorInfoContent> infoContents;
	
	/**
	 * ElevatorInfo constructor passed in an elevatorID and the current floor it is on.
	 * @param elevatorId
	 * @param elevatorFloor
	 */
	public ElevatorInfo(int elevatorId, int elevatorFloor) {
		this.elevatorId = elevatorId;
		this.elevatorFloor = elevatorFloor;
//		this.destinations = new ArrayList<>();
//		this.passengers = new ArrayList<>();
		this.infoContents = new ArrayList<>();
		stuck = false;
	}

	/**
	 * Add a destination which consists of a floor and passenger.
	 * @param destinationFloor  The number of the destination floor
	 * @param passenger			The passenger associated with a destination
	 */
	public void addDestination(int destinationFloor, Passenger passenger) {
		this.infoContents.add(new ElevatorInfoContent(destinationFloor, passenger));
	}

	/**
	 * Return true if we arrived at a destination.
	 * @param currentFloor  The floor currently arrived at
	 * @param passenger		The passenger associated with an arrival at a destination
	 * @return
	 */
	public boolean removeDestinationIfArrived(int currentFloor, Passenger passenger) {
		this.elevatorFloor = currentFloor;

//		if (this.destinations.contains(currentFloor)) {
//			this.destinations.remove((Object) currentFloor);
//			if (passenger != null) {
//				this.passengers.remove(passenger);
//			}
//			return true;
//		}
		
		for(ElevatorInfoContent infoContent : infoContents) {
			if(infoContent.destination == currentFloor) {
				this.infoContents.remove(infoContent);
				return true;
			}
		}

		return false;
	}

	/**
	 * Compute direction method used to get current direction of elevator
	 * @return
	 */
	public Direction computeDirection() {
		//int nextDest = this.destinations.get(0);
		
		if (this.infoContents.size() < 1) {
			return Direction.DOWN;
		}

		int nextDest = this.infoContents.get(0).destination;

		if (elevatorFloor < nextDest) {
			return Direction.UP;
		} else {
			return Direction.DOWN;
		}

	}

	/**
	 * Computes next destination for the elevator.
	 * @return
	 */
	public ElevatorInfoContent computeNextDestination() {
		if (this.infoContents.size() == 0) { return null; }
		if (this.infoContents.size() == 1) { return this.infoContents.get(0); }

		if (this.infoContents.get(0).destination == this.elevatorFloor) {
			if(infoContents.get(0).passenger != null) {
				return this.infoContents.remove(0);
			}
			else {
				this.infoContents.remove(0);
			}
			return this.computeNextDestination();
		}

		// compute direction
		Direction direction = this.computeDirection();

		//List<Integer> copyOfDest = new ArrayList<>(this.destinations);
		List<ElevatorInfoContent> copyOfDest = new ArrayList<>(this.infoContents);

		if (direction == Direction.UP) {
			Collections.sort(copyOfDest, new ElevatorInfoComparator());
			return copyOfDest.get(0);
		} else if (direction == Direction.DOWN) {
			Collections.sort(copyOfDest, new ElevatorInfoComparator());
			Collections.reverse(copyOfDest);
			return copyOfDest.get(0);
		}

		throw new IllegalArgumentException("Invalid state");
	}

	/**
	 * Balance passenger load amongst elevators by approximating through the number
	 * destinations for an elevator.
	 */
	public boolean isFree() {
		return this.infoContents.size() < MAX_PASSENGERS;
		//return this.destinations.size() < MAX_PASSENGERS;
	}

}
