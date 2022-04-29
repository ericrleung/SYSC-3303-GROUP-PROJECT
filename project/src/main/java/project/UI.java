package project;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Date;
import java.util.Scanner;

import javax.swing.*;
import javax.swing.border.Border;

import project.communication.Connection;
import project.messages.ConsoleOutputStream;
import project.messages.ReadFile;

/**
 * Java class for building the UI
 * @author caleb
 *
 */
public class UI {

	/**
	 * scheduler: Our Scheduler Object
	 * elevator: Our Elevator Object
	 * floor: Our Floor Object
	 * eventsList: the JCombobox that contains all events.txt files
	 * console: Output console for all print and Util.log statements
	 * elevatorThread: Thread for our Elevator runnable
	 * schedulerThread: Thread for our Scheduler runnable
	 * floorThread: Thread for our Floor runnable
	 */
	private Scheduler scheduler;
	private JComboBox eventsList;
	private JTextArea console;
	private Thread schedulerThread;
	private UI_Messenger messenger;
	private UI_Elevator_Messenger elevatorMessenger;
	private JPanel elevatorPlaceholder;
	private JPanel floorPlaceholder;
	private String lastMoveTime, lastDoorTime;
	
	/**
	 * Initialized GUI UI.
	 * @param args
	 */
    public static void main(String args[]){
    	UI ui = new UI();
    	
    	
     }
    
    /**
     * constructor for our UI
     */
    public UI() {
    	messenger = new UI_Messenger(5090);
    	elevatorMessenger = new UI_Elevator_Messenger(5092);
    	Thread messengerThread = new Thread(messenger, "UIMessenger");
    	messengerThread.start();
    	Thread elevatorMessengerThread = new Thread(elevatorMessenger, "UIElevatorMessenger");
    	elevatorMessengerThread.start();
    	//use port 5073 for UI messages
        JFrame frame = new JFrame("Elevator Manager");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1300, 700);
        console = new JTextArea();
        console.setEditable(false);
        JScrollPane jsp = new JScrollPane(console);
        lastMoveTime = "500";
        lastDoorTime = "1000";
        //create our menu
        createMenu(frame);
        
        createJPanel(frame, jsp);

        //initialize all events in our resources folder
        createComboBox(frame);
        //define the PrintStream
    	redefinePrintStream(jsp);
		
		//initialize the threads/window
		resetProgram();
		
		//kill threads once the window is closed
		frame.addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
            	killThreads();
                e.getWindow().dispose();
            }
        });

        frame.setVisible(true);
    }
    
    /**
     * Create and populate a JPanel which contains our main UI elements
     * @param frame: Main window frame
     * @param jsp: Jave ScrollPane for the console output
     */
    private void createJPanel(JFrame frame, JScrollPane jsp) {
    	// create a line border with the specified color and width
        Border border = BorderFactory.createLineBorder(Color.BLACK, 3);
        JLabel schedulerPlaceholder = new JLabel("Scheduler Placeholder");
        schedulerPlaceholder.setBorder(border);
        // This will be done in iteration 5
        schedulerPlaceholder.setVisible(false);
        elevatorPlaceholder = new JPanel();
        elevatorPlaceholder.setLayout(new GridLayout(0,2));
        elevatorPlaceholder.setBorder(border);
        floorPlaceholder = new JPanel();
        floorPlaceholder.setLayout(new BoxLayout(floorPlaceholder, BoxLayout.Y_AXIS));
        floorPlaceholder.setBorder(border);
        floorPlaceholder.setSize(200, 100);
        JPanel panel = new JPanel();
        GroupLayout layout = new GroupLayout(panel);
        panel.setLayout(layout);
        layout.setHorizontalGroup(layout
        	    .createParallelGroup(GroupLayout.Alignment.LEADING)
        	    .addGroup(layout.createSequentialGroup()
        	        .addComponent(floorPlaceholder, 0, GroupLayout.DEFAULT_SIZE / 2, Short.MAX_VALUE)
        	        .addComponent(schedulerPlaceholder, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        	        .addComponent(elevatorPlaceholder, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    	    .addComponent(jsp, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));

        layout.setVerticalGroup(layout.createSequentialGroup()
        	    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
        	    .addComponent(floorPlaceholder).addComponent(schedulerPlaceholder)
        	    .addComponent(elevatorPlaceholder))
        	    .addComponent(jsp));
        	
        panel.setVisible(true);
    	
		//add elements to our frame
        frame.add(panel);
    }
    
    /**
     * Add an elevator to our elevatorPlaceholder panel
     * @param elevatorId The id of the elevator to create
     */
    public void addFloorUI(int floorId) {
    	JPanel tempFloor = new JPanel();
    	GridBagLayout gridBagLayout = new GridBagLayout();
    	GridBagConstraints c = new GridBagConstraints();
    	c.fill = GridBagConstraints.BOTH;
    	tempFloor.setSize(100, 25);
    	BoxLayout boxLayout = new BoxLayout(tempFloor, BoxLayout.X_AXIS);
        tempFloor.setLayout(gridBagLayout);
        JLabel floorName;
        if (floorId < 10) {
        	floorName = new JLabel("Floor " + floorId, SwingConstants.CENTER);
        } else {
        	floorName = new JLabel("Floor " + floorId, SwingConstants.CENTER);
        }
    	
    	JPanel buttonLayout = new JPanel();
    	//potentially change buttons on floors being added in a later iteration to be done automatically
    	//buttonLayout.setLayout(new BoxLayout(buttonLayout, BoxLayout.X_AXIS));
    	JLabel floorButton1, floorButton2;
    	floorButton1 = new JLabel("UP", SwingConstants.CENTER);
    	floorButton2 = new JLabel("DOWN", SwingConstants.CENTER);
		Border border = BorderFactory.createLineBorder(Color.DARK_GRAY, 1);
		floorButton1.setBorder(border);
		floorButton1.setOpaque(true);
		floorButton1.setBackground(Color.GRAY);
		floorButton2.setBorder(border);
		floorButton2.setOpaque(true);
		floorButton2.setBackground(Color.GRAY);
    	c.gridx = 0;
    	c.gridy = 0;
    	c.weightx = 1.0;
        gridBagLayout.setConstraints(floorName, c);
        tempFloor.add(floorName);
          
      	c.gridx = 1;
      	c.gridy = 0;
        gridBagLayout.setConstraints(floorButton1, c);
        tempFloor.add(floorButton1);
        c.gridx = 2;
      	c.gridy = 0;
      	
        gridBagLayout.setConstraints(floorButton2, c);
        tempFloor.add(floorButton2);
    	//Border border = BorderFactory.createLineBorder(Color.RED, 2);
    	tempFloor.setBorder(border);
    	buttonLayout.setBorder(border);
    	
    	floorPlaceholder.add(tempFloor);
    	floorPlaceholder.revalidate();
    	floorPlaceholder.repaint();
    }
    
    /**
     * Update the floor button to turn it off or on.
     * @param floorNumber 	the floor number associated with a floor
     * @param status 		the status of the button
     * @param button 		the button that is being updated
     */
    public void updateFloorButton(int floorNumber, boolean status, boolean button) {
    	JPanel tempPanel = (JPanel) floorPlaceholder.getComponent(floorNumber-1);
    	JLabel match = null;
    	GridBagLayout layout = (GridBagLayout) tempPanel.getLayout();
    	for (Component comp : tempPanel.getComponents()) {
    	    GridBagConstraints gbc = layout.getConstraints(comp);
    	    // up button = false
    	    // down button = true
    	    if (!button) {
    	    	if (gbc.gridx == 1 && gbc.gridy == 0) {
        	        match = (JLabel)comp;
        	    }
    	    } else {
    	    	if (gbc.gridx == 2 && gbc.gridy == 0) {
        	        match = (JLabel)comp;
        	    }
    	    }
    	    
    	}
    	if(match != null) {
    		if (status) {
    			match.setBackground(Color.GREEN);
    		} else {
    			match.setBackground(Color.GRAY);
    		}
    	}
    }
    
    /**
     * Add an elevator to our elevatorPlaceholder panel
     * @param elevatorId The id of the elevator to create
     */
    public void addElevatorUI(int elevatorId) {
    	JPanel tempElevator = new JPanel();
    	GridBagLayout gridBagLayout = new GridBagLayout();
    	GridBagConstraints c = new GridBagConstraints();
    	c.fill = GridBagConstraints.BOTH;
        c.weightx = 1.0;
    	tempElevator.setLayout(gridBagLayout);
    	JLabel elevatorName = new JLabel("Elevator" + elevatorId, SwingConstants.CENTER);
    	JLabel currentFloor = new JLabel("Current Floor:1", SwingConstants.CENTER);
    	JLabel destinationFloor = new JLabel("Destination Floor:", SwingConstants.CENTER);
    	JLabel currentStatus = new JLabel("Elevator Status: Running");
    	
    	JPanel buttonLayout = new JPanel();
    	//potentially change buttons on floors being added in a later iteration to be done automatically
    	buttonLayout.setLayout(new GridLayout(0,3));
    	
    	for(int i = 22; i > 0; i--) {
    		JLabel carButton = new JLabel(""+i, SwingConstants.CENTER);
    		Border border = BorderFactory.createLineBorder(Color.DARK_GRAY, 1);
    		carButton.setBorder(border);
    		carButton.setOpaque(true);
    		carButton.setBackground(Color.GRAY);
    		buttonLayout.add(carButton);
    	}
    	
    	c.gridx = 0;
    	c.gridy = 0;
        c.weighty = 1.0;
        c.weightx = 1.0;
        gridBagLayout.setConstraints(elevatorName, c);
        tempElevator.add(elevatorName);
        
    	c.gridx = 0;
    	c.gridy = 1;
        gridBagLayout.setConstraints(currentFloor, c);
        tempElevator.add(currentFloor);
        
    	c.gridx = 0;
    	c.gridy = 2;
        gridBagLayout.setConstraints(destinationFloor, c);
        tempElevator.add(destinationFloor);
        
        c.gridx = 0;
        c.gridy = 3;
        gridBagLayout.setConstraints(currentStatus, c);
        tempElevator.add(currentStatus);
        
        
    	c.gridx = 1;
    	c.gridy = 0;
        c.gridheight = 4;

        gridBagLayout.setConstraints(buttonLayout, c);
    	tempElevator.add(buttonLayout);
    	//JLabel tempElevator = new JLabel("Elevator" + elevatorId);
    	Border border = BorderFactory.createLineBorder(Color.BLACK, 2);
    	tempElevator.setBorder(border);
    	buttonLayout.setBorder(border);
    	
    	elevatorPlaceholder.add(tempElevator);
    	elevatorPlaceholder.revalidate();
    	elevatorPlaceholder.repaint();
    }
    
    /**
     * Update the JLabel to show the current floor of the Elevator
     * Resets the car button it is currently on
     * @param elevatorId the Id of the elevator to change
     * @param newFloor the new Floor that the Elevator is on
     */
    public void updateElevatorFloor(int elevatorId, int newFloor) {
    	JPanel tempPanel = (JPanel) elevatorPlaceholder.getComponent(elevatorId-1);
    	JLabel match = null;
    	GridBagLayout layout = (GridBagLayout) tempPanel.getLayout();
    	for (Component comp : tempPanel.getComponents()) {
    	    GridBagConstraints gbc = layout.getConstraints(comp);
    	    if (gbc.gridx == 0 && gbc.gridy == 1) {
    	        match = (JLabel)comp;
    	    }
    	    else if(gbc.gridx == 0 && gbc.gridy == 3) {
    	    	JLabel label = (JLabel)comp;
    	    	label.setText("Elevator Status: Running");
    	    	label.setOpaque(false);
    	    }
    	    else if (gbc.gridx == 1 && gbc.gridy == 0) {
    	        JPanel buttonPanel = (JPanel)comp;
    	        JLabel button = (JLabel) buttonPanel.getComponent(Math.abs(newFloor - 22));
    	        button.setBackground(Color.GRAY);
    	    }
    	}
    	if(match != null) {
    		match.setText("Current Floor:"+newFloor);
    	}
    	updateFloorButton(newFloor, false, false);
    	updateFloorButton(newFloor, false, true);
    }
    
    
    /**
     * Updates the destinationFloor for the Selected Elevator
     * @param elevator The selected elevator
     * @param destinationFloor the destination for this elevator
     */
    public void updateDestination(int elevatorId, int destinationFloor) {
    	JPanel tempPanel = (JPanel) elevatorPlaceholder.getComponent(elevatorId-1);
    	JLabel match = null;
    	GridBagLayout layout = (GridBagLayout) tempPanel.getLayout();
    	for (Component comp : tempPanel.getComponents()) {
    	    GridBagConstraints gbc = layout.getConstraints(comp);
    	    if (gbc.gridx == 0 && gbc.gridy == 2) {
    	        match = (JLabel)comp;
    	        break;
    	    }
    	}
    	if(match != null) {
    		match.setText("Destination Floor:"+ destinationFloor);
    	}
    }
    
    /**
     * Updates the car button to turn it on when a press made for floor
     * @param elevatorId
     * @param carButton
     */
    public void updateElevatorCarButton(int elevatorId, int carButton) {
    	JPanel tempPanel = (JPanel) elevatorPlaceholder.getComponent(elevatorId-1);
    	JPanel match = null;
    	GridBagLayout layout = (GridBagLayout) tempPanel.getLayout();
    	for (Component comp : tempPanel.getComponents()) {
    	    GridBagConstraints gbc = layout.getConstraints(comp);
    	    if (gbc.gridx == 1 && gbc.gridy == 0) {
    	        match = (JPanel)comp;
    	    }
    	}
    	if(match != null) {
    		JLabel button = (JLabel) match.getComponent(Math.abs(carButton-22));
    		button.setBackground(Color.GREEN);
    	}
    }
    
    /**
     * Display the fault on an elevator
     * @param faultType the Type of fault
     * @param elevatorId the Id of the elevator
     */
    public void faultElevator(int faultType, int elevatorId) {
    	JPanel tempPanel = (JPanel) elevatorPlaceholder.getComponent(elevatorId-1);
    	JLabel match = null;
    	GridBagLayout layout = (GridBagLayout) tempPanel.getLayout();
    	for (Component comp : tempPanel.getComponents()) {
    	    GridBagConstraints gbc = layout.getConstraints(comp);
    	    if (gbc.gridx == 0 && gbc.gridy == 3) {
    	        match = (JLabel)comp;
    	        break;
    	    }
    	}
    	if(match != null) {
    		if(faultType == 1) {
    			match.setBackground(Color.YELLOW);
        		match.setText("Elevator Status: Door Fault");
        		match.setOpaque(true);
    		}
    		else if(faultType == 2) {
    			match.setBackground(Color.RED);
        		match.setText("Elevator Status: Elevator Fault");
        		match.setOpaque(true);
    		}

    	}
    }
    
    /**
     *  Create and add all menu items and actions
     * @param frame: The Jframe of our window
     */
    private void createMenu(JFrame frame) {
        JMenuBar mb = new JMenuBar();
        JMenu m1 = new JMenu("FILE");
        mb.add(m1);
        JMenuItem m11 = new JMenuItem("Run");
        m1.add(m11);
        JMenuItem m2 = new JMenuItem("EDIT");
        mb.add(m2);
        
        m11.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
              selectFile();
            }
          });
        
        m2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
            	editTimes(frame);
            }
          });
        
        frame.getContentPane().add(BorderLayout.NORTH, mb);
    }
    
    /**
     * Helper function to process user input for editing move and door times.
     * @param frame: Our UI window JFrame
     */
    private void editTimes(JFrame frame) {
    	JPanel panel = new JPanel(new BorderLayout(5, 5));

        JPanel label = new JPanel(new GridLayout(0, 1, 2, 2));
        label.add(new JLabel("Move Time (ms)", SwingConstants.RIGHT));
        label.add(new JLabel("Door Time (ms)", SwingConstants.RIGHT));
        panel.add(label, BorderLayout.WEST);

        JPanel controls = new JPanel(new GridLayout(0, 1, 2, 2));
        JTextField moveTimeField = new JTextField();
        moveTimeField.setText(lastMoveTime);
        controls.add(moveTimeField);
        JTextField doorTimeField = new JTextField();
        doorTimeField.setText(lastDoorTime);
        controls.add(doorTimeField);
        panel.add(controls, BorderLayout.CENTER);

        int result = JOptionPane.showConfirmDialog(frame, panel, "Edit Elevator Values", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
        	try {
        		elevatorMessenger.setTimes(Integer.parseInt(moveTimeField.getText()), Integer.parseInt(doorTimeField.getText()));
        		lastMoveTime = moveTimeField.getText();
        		lastDoorTime = doorTimeField.getText();
        	} catch (NumberFormatException e) {
        		e.printStackTrace();
        	}
        }
    }
    
    /**
     * Initialize our ComboBox with all filenames of event inputs
     * @param frame: Our UI window JFrame
     */
    private void createComboBox(JFrame frame) {
    	File file = new File(getClass().getClassLoader().getResource("events").getFile());
    	String absolutePath = file.getAbsolutePath();
    	
    	String[] txtList = file.list();
    	
    	eventsList = new JComboBox(txtList);
        frame.getContentPane().add(BorderLayout.WEST, eventsList);
    }
    
    /**
     * Redefines the PrintStream to print to our UI console 
     */
    private void redefinePrintStream(JScrollPane jsp) {
        PrintStream printStream = new PrintStream(new ConsoleOutputStream(this,console,jsp));
        System.setOut(printStream);
        System.setErr(printStream);
    }
    
    /**
     * select an events file to run that simulates button presses on a floor
     */
    private void selectFile() {
    	//read events from selected file
    	//if the combobox is empty then do nothing
    	if(eventsList.getItemCount() <= 0) {
    		return;
    	}
    	
    	String toSend = eventsList.getSelectedItem().toString();
    	this.messenger.setFileToSend(toSend);
    	BufferedReader reader;
		try {
			File myObj = new File(getClass().getClassLoader().getResource("events/" + toSend).getFile());
			Scanner myReader = new Scanner(myObj);
			int lines = 0;
			while (myReader.hasNextLine()) {
				myReader.nextLine();
				lines++;
			}
			myReader.close();
	    	scheduler.setNanoTime(lines);
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    /**
     * kills all created threads
     */
    private void killThreads() {
    	try {
			scheduler.kill();
			messenger.kill();
			elevatorMessenger.kill();
			schedulerThread.join();
    	}
    	catch (InterruptedException error) {
			error.printStackTrace();
		}
    }
    
    /**
     * stops all threads, clears the console window, and resets the program
     */
    private void resetProgram() {
        //name our current thread
		Thread.currentThread().setName("World");
		// enable to true if we want more information
		Util.LOG = true;
    	//check if this is the first time that objects were initialized
    	if(scheduler != null) {
    		killThreads();
        	console.setText("");
    	}
    	scheduler = new Scheduler(5076);
		schedulerThread = new Thread(scheduler, "Scheduler1");

		schedulerThread.start();
    	
    }
   
}
