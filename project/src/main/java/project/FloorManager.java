package project;

import java.io.File;
import java.net.DatagramPacket;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;

import project.communication.Connection;
import project.messages.Direction;
import project.messages.Message;
import project.messages.ReadFile;
import project.messages.Ready;
import project.messages.Setup;

/**
 * Class to keep track of Floor threads.
 *
 */
public class FloorManager implements Runnable{
	private ArrayList<Floor> floors;
	private Connection connection;
	private int passengerCount;

	private Clock clock;
	private Map<String, Date> sourceEvents;

	/**
	 * Constructor to keep track of all floor threads
	 */
	public FloorManager() {
		//use port 5090 for UI messages
		this.connection = new Connection(5090);
		Util.LOG = true;
		this.floors = new ArrayList<Floor>();
		this.passengerCount = 0;
		this.clock = new Clock();
		this.sourceEvents = new HashMap<String, Date>();
	}
	
	/**
	 * create and start a new Floor Thread
	 */
	public void createFloorThread() {
		//all floors have the same port number thanks to our connection class
		Floor f1 = new Floor(5076);
		Thread ft1 = new Thread(f1, "Floor:n");
		floors.add(f1);
		ft1.start();
	}


    // Runs any events if the time has arrived, and increments the time.
    public void tick() {
        for (Map.Entry<String, Date> event : sourceEvents.entrySet()) {
            if (clock.isTime(event.getValue().getTime())) {
				try {
					processEvent(event.getKey());
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
        }

        clock.increment();
    }


    void processEvent(String s) throws ParseException {
		Util.log("processEvent()");
        String[] curr = s.split(" ");
        //reset the clock when we get a new file

        Date eventTimeStamp = new SimpleDateFormat("HH:mm:ss").parse(curr[0]);
        String floor = curr[1];
        if (floor.equals("n"))
            return;
        Util.log("Parsed event: " + s);

//				floors.get(Integer.parseInt(floor) - 1).pushButton(Direction.toDirection(curr[2]),
//						Integer.parseInt(floor), eventTimeStamp);
        //iterate to find requested floor id
        for (Floor tempfloor : floors) {
            if(tempfloor.getNum() == Integer.parseInt(floor)) {
                Passenger passenger = new Passenger(Integer.parseInt(curr[3]) ,0, Integer.parseInt(floor), false, this.passengerCount);
                this.passengerCount++;
                if(curr.length > 4) {
                    passenger.setFaultNum(Integer.parseInt(curr[4]));;
                }
                tempfloor.simulatedPress(Direction.toDirection(curr[2]), eventTimeStamp, passenger);
                break;
            }
        }
    }

	/**
	 * Read in the input file. This contents of the file will be parsed and will
	 * tell the floor to send a message requesting for an elevator.
	 * 
	 * @param inputText the name of the input file to be opened and read
	 */
	public void readEvents(String inputText) {
		// check if all floor threads have been assigned an id yet
		for(Floor tempfloor : floors) {
			if(tempfloor.getNum() == -1) {
				//sleep for 2 seconds before trying to read the file again
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				readEvents(inputText);
				return;
			}
		}
		
		ArrayList<String> lines = new ArrayList<>();
		try {
			// used to help get path for input file location
			File myObj = new File(getClass().getClassLoader().getResource("events/" + inputText).getFile());
			Scanner myReader = new Scanner(myObj);
			while (myReader.hasNextLine()) {
				lines.add(myReader.nextLine());
				Util.debug("Read line from file: " + lines.get(lines.size() - 1));
			}

			myReader.close();


            for (String s : lines) {
                String[] curr = s.split(" ");

                Date eventTimeStamp = new SimpleDateFormat("HH:mm:ss").parse(curr[0]);
                sourceEvents.put(s, eventTimeStamp);
            }
		} catch (Exception e) {
			Util.log("An error occurred.");
			e.printStackTrace();
		}

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
            if(m instanceof ReadFile){
            	ReadFile readFile = (ReadFile) m;
        		Util.log("Simulating events"+ readFile.eventsFile);
        		this.clock = new Clock();
        		this.sourceEvents = new HashMap<String, Date>();
        		this.readEvents(readFile.eventsFile);
                connection.acknowledge(sign,readFile);
            } else if (m instanceof Ready) {
            	processReadyMessage(m, sign);
            }
        }catch (Exception e){
        	e.printStackTrace();
            Util.log("Generic Message Exception");
        }

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

                connection.acknowledge(sign,new Setup(new Date(),1));
        	}
        	else { // only other option is a scheduler

                connection.acknowledge(sign,new Setup(new Date(),2));
        	}
    	}
    	catch (Exception e) {
    		e.printStackTrace();
    	}

    	
    }
    
	/**
	 * Main method for the FloorManager. Creates a floor manager and reads events from a file.
	 * @param args
	 */
	public static void main(String[] args) {
		Thread.currentThread().setName("FloorManager");
		FloorManager floorManager = new FloorManager();
		Thread floorManagerThread = new Thread(floorManager, "FloorManagerMessager");
		floorManagerThread.start();
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		
		//floorManager.readEvents("events.txt");
	}

	/**
	 * Loop for our Floormanager
	 */
	@Override
	public void run() {
		connection.init();
		for (int i = 0; i < 22; i++) {
			createFloorThread();
		}
        while (true) {
            receiving();
			if (this.sourceEvents.size() == 0) { continue; }

			tick();
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
