package project;

/**
 * Class Used to contain information stored and sorted by the ElevatorInfo class
 * 
 *
 */
public class ElevatorInfoContent {
	/**
	 * destination: the destination queued in the Elevator
	 * passenger: the passenger associated with the destination (can be null)
	 */
	public final Integer destination;
	public final Passenger passenger;
	
	
	public ElevatorInfoContent(Integer destination, Passenger passenger) {
		this.destination = destination;
		this.passenger = passenger;
	}

}
