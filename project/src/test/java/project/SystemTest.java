package project;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import project.messages.ArrivedAtFloor;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;

/**
 * Tests project.System
 */
class SystemTest {

	
	@Test
	public void testElevatorStuck() throws Exception {
		Elevator elevator = new Elevator(-1, 1000, 1000);
		elevator.setStuck(true);
		elevator.nextState();
		assertEquals(elevator.getState(), ElevatorState.STUCK_ELEVATOR);
	}
	
}
