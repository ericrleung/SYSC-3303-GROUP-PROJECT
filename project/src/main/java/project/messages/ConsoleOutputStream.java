package project.messages;

import java.io.IOException;
import java.io.OutputStream;

import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import project.UI;

/**
 * OutputStream used to redirect console output into our JTextPane used in the UI
 * @author caleb
 *
 */
public class ConsoleOutputStream extends OutputStream {
	private JTextArea textArea;
	private JScrollPane jsp;
	private UI gui;
	private String currentLine;

    public ConsoleOutputStream(UI gui,JTextArea textArea, JScrollPane jsp) {
        this.textArea = textArea;
        this.jsp = jsp;
        this.gui = gui;
        currentLine = "";
    }

    /**
     * outputstream write method
     */
    @Override
    public void write(int b) throws IOException {
        // redirects data to the text area
    	String contents = String.valueOf((char)b);
    	currentLine += contents;
    	if(contents.contains("\n")) {
        	if(currentLine.contains("number of e queue:")) {
        		String[] arrayContents = currentLine.split(" ");
        		gui.addElevatorUI(Integer.parseInt(arrayContents[arrayContents.length-1].replace("\n", "").replace("\r", "")));
        	}
        	else if(currentLine.contains("Elevator") && currentLine.contains("has arrived on floor")) {
        		String[] arrayContents = currentLine.split(" ");
        		gui.updateElevatorFloor(Integer.parseInt(arrayContents[arrayContents.length-6]),
        				Integer.parseInt(arrayContents[arrayContents.length-1].replace("\n", "").replace("\r", "")));
        	}
        	else if(currentLine.contains("Type 1 Fault Elevator Doors Stuck ID: ")) {
        		//Door stuck fault
        		//Type 1 Fault Elevator Doors Stuck ID: 1 Floor number: 1
        		String[] arrayContents = currentLine.split(" ");
        		gui.faultElevator(1,Integer.parseInt(arrayContents[arrayContents.length-4]));
        	}
        	else if(currentLine.contains("Type 2 Fault Elevator Got Stuck ID: ")) {
        		//Door stuck fault
        		String[] arrayContents = currentLine.split(" ");
        		gui.faultElevator(2,Integer.parseInt(arrayContents[arrayContents.length-4]));
        	}
        	else if(currentLine.contains("Received car button request from elevatorId")) {
        		//Received car button request from elevatorId= 2 for floor= 2
        		String[] arrayContents = currentLine.split(" ");
        		gui.updateElevatorCarButton(Integer.parseInt(arrayContents[arrayContents.length-4]),
        				Integer.parseInt(arrayContents[arrayContents.length-1].replace("\n", "").replace("\r", "")));
        	}
        	else if(currentLine.contains("number of f queue:")) {
        		String[] arrayContents = currentLine.split(" ");
        		gui.addFloorUI(Integer.parseInt(arrayContents[arrayContents.length-1].replace("\n", "").replace("\r", "")));
        	}
        	else if (currentLine.contains("Floor button request direction:")) {
        		String[] arrayContents = currentLine.split(" ");
        		gui.updateFloorButton(Integer.parseInt(arrayContents[arrayContents.length-2]), true, 
        				Boolean.parseBoolean(arrayContents[arrayContents.length-1].replace("\n", "").replace("\r", "")));
        	}
        	else if (currentLine.contains("Scheduler is sending elevator")) {
        		// 4 to the floor 3
        		String[] arrayContents = currentLine.split(" ");
        		gui.updateDestination(Integer.parseInt(arrayContents[arrayContents.length-5]), 
        				Integer.parseInt(arrayContents[arrayContents.length-1].replace("\n", "").replace("\r", "")));
        	}
        		//Floor button request direction: 2 true
        		//Elevator 1 has arrived on floor 2
        	//reset tracker for currentLine
        	currentLine = "";
    	}

        textArea.append(String.valueOf((char)b));
        textArea.revalidate();
        JScrollBar sb = jsp.getVerticalScrollBar();
        sb.setValue( sb.getMaximum() );
    }
}
