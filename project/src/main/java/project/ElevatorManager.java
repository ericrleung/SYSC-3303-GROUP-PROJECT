package project;

import java.net.DatagramPacket;
import java.util.ArrayList;

import project.communication.Connection;
import project.messages.EditElevatorTimes;
import project.messages.Message;

/**
 * Class to keep track of Elevator threads.
 *
 */
public class ElevatorManager implements Runnable {
	private ArrayList<Elevator> elevators;
	private Connection connection;
	private int moveTime;
	private int doorTime;
	
	/**
	 * Constructor to keep track of all elevator threads
	 */
	public ElevatorManager() {
		//use port 5092 for UI messages
		this.connection = new Connection(5092);
		Util.LOG = true;
		this.elevators = new ArrayList<Elevator>();
		// Default wait time of 1000ms for elevator moving.
		moveTime = 500;
		doorTime = 1000;
	}
	
	/**
	 * create and start a new elevator Thread
	 *  
	 */
	public void createElevatorThread() {
		//all floors have the same port number thanks to our connection class
		Elevator e1 = new Elevator(5076, moveTime, doorTime);
		Thread et1 = new Thread(e1, "Elevator:n");
		elevators.add(e1);
		et1.start();
	}
	
	/**
     * Receives and acknowledges the events.txt file request from the UI
     */
    public void receiving(){
        DatagramPacket packet = connection.receiveWithTimeout();
        if(packet == null) {
            return;	
        }

        byte[] sign = connection.getSignature(packet.getData());
        Message m = connection.deserializeMessage(packet.getData());
        try{
            if(m instanceof EditElevatorTimes){
            	EditElevatorTimes editElevatorTimes = (EditElevatorTimes) m;
            	Util.log("Updating elevator params "+ editElevatorTimes.moveTime + " " + editElevatorTimes.doorTime);
            	this.moveTime = editElevatorTimes.moveTime;
            	this.doorTime = editElevatorTimes.doorTime;
                connection.acknowledge(sign,editElevatorTimes);
                for (Elevator e: elevators) {
                	e.setTimes(moveTime, doorTime);
                }
            }
        }catch (Exception e){
        	e.printStackTrace();
            Util.log("Generic Message Exception");
        }

    }
	
    /**
	 * Main method for the ElevatorManager. Creates a elevator manager.
	 * @param args
	 */
	public static void main(String[] args) {
		Util.LOG = true;
		Thread.currentThread().setName("ElevatorManager");
		ElevatorManager elevatorManager = new ElevatorManager();
		Thread elevatorManagerThread = new Thread(elevatorManager, "ElevatorManagerMessager");
		elevatorManagerThread.start();
	}
	/**
	 * Loop for our Elevatormanager
	 */
	@Override
	public void run() {
		connection.init();
		for (int i = 0; i < 4; i++) {
			createElevatorThread();
		}
        while (true) {
        	receiving();
        }	
	}

}
