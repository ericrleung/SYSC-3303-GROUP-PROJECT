# SYSC-3303-GROUP-PROJECT

# Iteration 1 - Establish Connections between the three subsystems
Embedded Java Project Contributors:
  * John Grabkowski 101071591
    * Elevator Subsystem, Javadocs
  * Rafi Khan
    * Scheduler Subsystem, Javadocs
  * Gabriel Ciolac 101071319
    * Floor Subsystem, Javadocs
  * Caleb Turcotte 100929209
    * Scheduler Subsystem, Javadocs
  * Eric Leung 101032864
    * Elevator Subsystem, Javadocs

## Names of Files
* Scheduler: In the current the iteration, Scheduler is a thread which is responsible for facilitating communications between Elevators and Floors. In future
iterations, the scheduler will be responsible for scheduling Elevators responses to floor requests.
* Elevator: Elevator is a thread which is responsible for receiving requests to move from the scheduler and fulfilling those requests.
* Floor: Floor is a thread which is responsible for reading the events.txt file, and making requests to the scheduler to have the events acted upon.
* Messages extend an abstract Message.java object, this is done so all threads can communicate with the scheduler through one queue.
    * MoveTo : a message from the Scheduler to Elevator with the destination floor it should move to
    * Ready : a message sent from the Floor to notify the scheduler that it is ready to receive messages
    * RequestElevator : a message from the Floor which makes a request to the scheduler for an Elevator.
    * ArrivedAtFloor : message sent from Elevator to Scheduler to confirm it has received the MoveTo message
    * ElevatorArrived : message sent from Scheduler to Floor to confirm original "MoveTo" message was received
* Direction.java is an enum that contains information if the floor button is "Up" or "Down", will also signify direction of travel in future iterations.
* The Scheduler has an interface MessageQueue.java used for message buffers
* Util is our logging tool used to print displayed messages onto the console
* SystemTest.java is our Unit test to confirm that the System can properly be set up and send messages

## Set up Instructions
* Make sure you use Java 11 or later
1. Open Eclipse IDE
2. Navigate to File, Import, and select Project from Folder or Archive and click next
3. Under import source click Archive and select the source code project submission.
4. Once you press finish, the project will now be imported in eclipse
5. Jump to Steps to test to test the project.

## Steps to test:
1. Open project in eclipse
2. Open SystemTest.java
3. Right click on the testFloorRequestElevatorFileInput() on line 99 and click Run as JUnit Test .
4. View output in console
Information in console will be instructions read from file event.txt
The types of messages being sent (Different message types are created depending on who the intended sender and receiver are)
Floor sending message to the Scheduler
Scheduler relaying that message to Elevator
Elevator sending a message to Scheduler to inform that is has moved
Scheduler relaying that message to the Floor

# Iteration 2 - Adding the Scheduler and Elevator Subssytems
Embedded Java Project
Contributors:
  * John Grabkowski 101071591
    * Scheduler.java, Elevator.java
  * Rafi Khan
    * Scheduler.java, Elevator.java
  * Gabriel Ciolac 101071319
    * Scheduler.java, Elevator.java
    *
  * Caleb Turcotte 100929209
    * UI.java, ConsoleOutputStream.java
    * Class diagram
  * Eric Leung 101032864
    * SchedulerState.java, ElevatorState.java

  * Team: Documentation, State machine design, JavaDocs

## Names of Files
* Scheduler: In the current the iteration, Scheduler is a thread which is responsible for
facilitating communications between Elevators and Floors. In future iterations, the scheduler will be responsible
for scheduling Elevators responses to floor requests.
* Elevator: Elevator is a thread which is responsible for receiving requests to move from the scheduler and fulfilling
those requests.
* Floor: Floor is a thread which is responsible for reading the events.txt file, and making requests to the scheduler
to have the events acted upon.
* SchedulerState:  Enum to define the scheduler states.
* ElevatorState: Enum to define the Elevator states.
* Messages extend an abstract `Message.java` object, this is done so all threads can communicate with the scheduler through
* events.txt: This is the events file that was provided as a part of the project.
* events2.txt: This is a sample file with just one event.
one queue.
  * MoveTo : a message from the Scheduler to Elevator with the destination floor it should move to
  * Ready : a message sent from the Floor to notify the scheduler that it is ready to receive messages
  * RequestElevator : a message from the Floor which makes a request to the scheduler for an Elevator.
  * ArrivedAtFloor : message sent from Elevator to Scheduler to confirm it has received the MoveTo message
  * ElevatorArrived : message sent from Scheduler to Floor to confirm original "MoveTo" message was received
* `Direction.java` is an enum that contains information if the floor button is "Up" or "Down", will also signify
direction of travel in future iterations.
* The Scheduler has an interface `MessageQueue.java` used for message buffers
* Util is our logging tool used to print displayed messages onto the console
* `SystemTest.java` is our Unit test to confirm that the System can properly be set up and send messages

## Set up Instructions
* Make sure you use Java 11 or later
1. Open Eclipse IDE
2. Navigate to File, Import, and select Project from Folder or Archive and
click next
3. Under import source click Archive and select the source code project
submission.
4. Once you press finish, the project will now be imported in eclipse
5. Jump to `Steps to test` to test the project.

## Steps to test:
1. Open project in eclipse
2. Right click on the src/main/java/project/UI.java and and select `Run as Java Application`.
3. Now you can see the user interface that shows the logs of the different subsystems. Right now there are no events feeding so the subsytems simply cycle through their states.
4. To run the default events file which was loaded from src/main/resources/events. This runs events.txt which only has one event
5. For a more sophisticated test, click the selector at the left of the UI. This will give you a dropdown menu where you can choose "events2.txt". For more info see the PDF which has an instructional diagram.
6. Click File->Reset and File->Run
7. This clears the interface log component, and runs the events from "events2.txt"
8. The events streaming through the scheduler will now show the actions of the floors requesting elevators which will then go to the destination. Once arrived, the elevator will signal the floor.
9. Feel free to switch back to the events.txt if you want to run that one again.



# Iteration 3 - Multiple Cars and System Distribution
Embedded Java Project
Contributors:
  * John Grabkowski 101071591
    * Scheduler.java, Elevator.java
  * Rafi Khan
    * Scheduler.java, Elevator.java
  * Gabriel Ciolac 101071319
    * Connection.java
  * Caleb Turcotte 100929209
    * UI.java, FloorManager.java, Connection.java, UI_Messenger.java
    * Class diagram
  * Eric Leung 101032864
    * Elevator.java, Scheduler.java

  * Team: Documentation, State machine design, JavaDocs

## Names of Files
* Scheduler: In the current the iteration, Scheduler is a thread which is responsible for
facilitating communications between Elevators and Floors. In future iterations, the scheduler will be responsible
for scheduling Elevators responses to floor requests.
* Elevator: Elevator is a thread which is responsible for receiving requests to move from the scheduler and fulfilling
those requests.
* Floor: Floor is a thread which is responsible for reading the events.txt file, and making requests to the scheduler
to have the events acted upon.
* SchedulerState:  Enum to define the scheduler states.
* ElevatorState: Enum to define the Elevator states.
* Messages extend an abstract `Message.java` object, this is done so all threads can communicate with the scheduler through
* events.txt: This is the events file that was provided as a part of the project.
* events2.txt: This is a sample file with just one event.
one queue.
  * MoveTo : a message from the Scheduler to Elevator with the destination floor it should move to
  * Ready : a message sent from the Floor to notify the scheduler that it is ready to receive messages
  * RequestElevator : a message from the Floor which makes a request to the scheduler for an Elevator.
  * ArrivedAtFloor : message sent from Elevator to Scheduler to confirm it has received the MoveTo message
  * ElevatorArrived : message sent from Scheduler to Floor to confirm original "MoveTo" message was received
* `Direction.java` is an enum that contains information if the floor button is "Up" or "Down", will also signify
* UI_Messenger.java: UDP Client for UI Message
* UI.java: Swing UI
* Connection.java: UDP client
* The Scheduler has an interface `MessageQueue.java` used for message buffers
* Util is our logging tool used to print displayed messages onto the console
* `SystemTest.java` is our Unit test to confirm that the System can properly be set up and send messages

## Set up Instructions
* Make sure you use Java 11 or later
1. Open Eclipse IDE
2. Navigate to File, Import, and select Project from Folder or Archive and
click next
3. Under import source click Archive and select the source code project
submission.
4. Once you press finish, the project will now be imported in eclipse
5. Jump to `Steps to test` to test the project.

## Steps to test:
1. Open project in eclipse
2. Right click on the src/main/java/project/UI.java and and select `Run as Java Application`.
3. Right click on the src/main/java/project/FloorManager.java and and select `Run as Java Application`. This will create all the floor threads and simulate button presses.
4. Right click on the src/main/java/project/Elevator.java and and select `Run as Java Application`.
5. Now you can see the user interface that shows the logs of the different subsystems. Right now there are no events feeding so the subsytems simply cycle through their states.
6. To run the default events file which was loaded from src/main/resources/events. This runs events.txt which only has one event
7. For a more sophisticated test, click the selector at the left of the UI. This will give you a dropdown menu where you can choose "events2.txt". For more info see the PDF which has an instructional diagram.
8. Click File->Run
9. This Runs the events from "events2.txt"
10. The events streaming through the scheduler will now show the actions of the floors requesting elevators which will then go to the destination. Once arrived, the elevator will signal the floor.
11. Feel free to switch back to the events.txt if you want to run that one again.

# Iteration 4 – Adding Error detection and correction
Contributors:
  * John Grabkowski 101071591
    * Scheduler.java, Elevator.java, ElevatorErrorMessages, Timing diagram
  * Rafi Khan
    *
  * Gabriel Ciolac 101071319
    * connection.java
  * Caleb Turcotte 100929209
    * UI.java, FloorManager.java, ElevatorManager.java
  * Eric Leung 101032864
    * ElevatorManager.java, FloorManager.java

  * Team: Documentation, Fault design, State machine design, JavaDocs


  ## Names of Files
  * Scheduler: In the current the iteration, Scheduler is a thread which is responsible for
  facilitating communications between Elevators and Floors.
  * Elevator: Elevator is a thread which is responsible for receiving requests to move from the scheduler and fulfilling
  those requests.
  * Floor: Floor is a thread which is responsible for reading the events.txt file, and making requests to the scheduler
  to have the events acted upon.
  * FloorManager: The Manager class responsible for creating Floor threads. Will simulate floor button presses in our system.
  * ElevatorManager: The Manager class responsible for creating Elevator threads
  * SchedulerState:  Enum to define the scheduler states.
  * ElevatorState: Enum to define the Elevator states.
  * Messages extend an abstract `Message.java` object, this is done so all threads can communicate with the scheduler through
  * events.txt: This is the events file that was provided as a part of the project.
  * events2.txt: This is a sample file with just one event.
  * mainEvents.txt: Large number of event requests that simulates all fault cases
    with 22 floors and 4 elevators.
  * fault-door.txt: Sends a type 1 error that makes elevator doors temporarily
    stuck.
  * fault-elevator.txt: Sends a type 2 error that terminates an elevator.

    * MoveTo : a message from the Scheduler to Elevator with the destination floor it should move to
    * Ready : a message sent from the Floor to notify the scheduler that it is ready to receive messages
    * RequestElevator : a message from the Floor which makes a request to the scheduler for an Elevator.
    * ArrivedAtFloor : message sent from Elevator to Scheduler to confirm it has received the MoveTo message
    * ElevatorArrived : message sent from Scheduler to Floor to confirm original "MoveTo" message was received
    * RequestCarButton : message sent from Elevator to Scheduler created by a car button press in the elevator
    * ElevatorDoorsStuck : message sent from Elevator to Scheduler created by a type 1 fault
    * ElevatorStuck : message sent from Elevator to Scheduler created by a type 2 fault
  * `Direction.java` is an enum that contains information if the floor button is "Up" or "Down", will also signify
  * UI_Messenger.java: UDP Client for UI Message
  * UI.java: Swing UI
  * Connection.java: UDP client
  * Util is our logging tool used to print displayed messages onto the console
  * `SystemTest.java` is our Unit test to confirm that the System can properly be set up and send messages

  ## Set up Instructions
  * Make sure you use Java 11 or later
  1. Open Eclipse IDE
  2. Navigate to File, Import, and select Project from Folder or Archive and
  click next
  3. Under import source click Archive and select the source code project
  submission.
  4. Once you press finish, the project will now be imported in eclipse
  5. Jump to `Steps to test` to test the project.

  ## Steps to test:
  1. Open project in eclipse
  2. Right click on the src/main/java/project/UI.java and and select `Run as Java Application`.
  3. Right click on the src/main/java/project/FloorManager.java and and select `Run as Java Application`. This will create all the floor threads and simulate button presses.
  4. Right click on the src/main/java/project/ElevatorManager.java and and select `Run as Java Application`.
  5. Now you can see the user interface that shows the logs of the different subsystems. Right now there are no events feeding so the subsytems simply cycle through their states.
  6. To run the default events file which was loaded from src/main/resources/events. This runs events.txt which only has one event
  7. For a more sophisticated test, click the selector at the left of the UI. This will give you a dropdown menu where you can choose "mainEvents.txt".
        For more info see the PDF which has an instructional diagram.
  8. Click File->Run
  9. This Runs the events from "mainEvents.txt"
  10. The events streaming through the scheduler will now show the actions of the floors requesting elevators which will then go to the destination. Once arrived, the elevator will signal the floor.
  11. Feel free to switch back to the events.txt if you want to run that one again.


  ## Different Test Files:
  ### To test Door Fault:
  1. In UI run fault-door.txt

  ### To test Elevator Stuck between floors Fault:
  1. In UI run fault-elevator.txt

  ### To test entire system with 22 floors and 4 elevators:
  1. In UI run mainEvents.txt

# Iteration 5 – Measuring the Scheduler and predicting the performance.
Contributors:
  * John Grabkowski 101071591
    * Floor UI (making correct lamps turn on and off in the GUI), extended
      Scheduler and Floor logic to handle UI lamps correctly
  * Rafi Khan
    * Added test cases, Created initial clock for tracking event files
  * Gabriel Ciolac 101071319
    * Finalized class diagrams, Add proper timings for reading event files
  * Caleb Turcotte 100929209
    * Timing diagram, Measurement tracking, 
  * Eric Leung 101032864
    * UI option to edit elevator parameters, Measurement report

  * Team: Documentation, Fault design, State machine design, JavaDocs


  ## Names of Files
  * Scheduler: In the current the iteration, Scheduler is a thread which is responsible for
  facilitating communications between Elevators and Floors.
  * Elevator: Elevator is a thread which is responsible for receiving requests to move from the scheduler and fulfilling
  those requests.
  * Floor: Floor is a thread which is responsible for reading the events.txt file, and making requests to the scheduler
  to have the events acted upon.
  * FloorManager: The Manager class responsible for creating Floor threads. Will simulate floor button presses in our system.
  * ElevatorManager: The Manager class responsible for creating Elevator threads
  * SchedulerState:  Enum to define the scheduler states.
  * ElevatorState: Enum to define the Elevator states.
  * Messages extend an abstract `Message.java` object, this is done so all threads can communicate with the scheduler through
  * events.txt: This is the events file that was provided as a part of the project.
  * events2.txt: This is a sample file with just one event.
  * mainEvents.txt: Large number of event requests that simulates all fault cases
    with 22 floors and 4 elevators.
  * fault-door.txt: Sends a type 1 error that makes elevator doors temporarily
    stuck.
  * fault-elevator.txt: Sends a type 2 error that terminates an elevator.

    * MoveTo : a message from the Scheduler to Elevator with the destination floor it should move to
    * Ready : a message sent from the Floor to notify the scheduler that it is ready to receive messages
    * RequestElevator : a message from the Floor which makes a request to the scheduler for an Elevator.
    * ArrivedAtFloor : message sent from Elevator to Scheduler to confirm it has received the MoveTo message
    * ElevatorArrived : message sent from Scheduler to Floor to confirm original "MoveTo" message was received
    * RequestCarButton : message sent from Elevator to Scheduler created by a car button press in the elevator
    * ElevatorDoorsStuck : message sent from Elevator to Scheduler created by a type 1 fault
    * ElevatorStuck : message sent from Elevator to Scheduler created by a type 2 fault
  * `Direction.java` is an enum that contains information if the floor button is "Up" or "Down", will also signify
  * UI_Messenger.java: UDP Client for UI Message
  * UI.java: Swing UI
  * Connection.java: UDP client
  * Util is our logging tool used to print displayed messages onto the console
  * `SystemTest.java` is our Unit test to confirm that the System can properly be set up and send messages

  ## Set up Instructions
  * Make sure you use Java 11 or later
  1. Open Eclipse IDE
  2. Navigate to File, Import, and select Project from Folder or Archive and
  click next
  3. Under import source click Archive and select the source code project
  submission.
  4. Once you press finish, the project will now be imported in eclipse
  5. Jump to `Steps to test` to test the project.

  ## Steps to test:
  1. Open project in eclipse
  2. Right click on the src/main/java/project/UI.java and and select `Run as Java Application`.
  3. Right click on the src/main/java/project/FloorManager.java and and select `Run as Java Application`. This will create all the floor threads and simulate button presses.
  4. Right click on the src/main/java/project/ElevatorManager.java and and select `Run as Java Application`.
  5. Now you can see the user interface that shows the logs of the different subsystems. Right now there are no events feeding so the subsytems simply cycle through their states.
  6. To run the default events file which was loaded from src/main/resources/events. This runs events.txt which only has one event
  7. For a more sophisticated test, click the selector at the left of the UI. This will give you a dropdown menu where you can choose "mainEvents.txt".
        For more info see the PDF which has an instructional diagram.
  8. Click File->Run
  9. This Runs the events from "mainEvents.txt"
  10. The events streaming through the scheduler will now show the actions of the floors requesting elevators which will then go to the destination. Once arrived, the elevator will signal the floor.
  11. Feel free to switch back to the events.txt if you want to run that one again.
