package project;

import java.util.Comparator;

/**
 * Class used by Collections.sort to determine how destinations are compared
 *
 */
public class ElevatorInfoComparator implements Comparator<ElevatorInfoContent>{

	/**
	 * Overrides the compare method used by Collections.sort
	 */
	@Override
	public int compare(ElevatorInfoContent o1, ElevatorInfoContent o2) {
		
		return o1.destination - o2.destination;
	}

}
