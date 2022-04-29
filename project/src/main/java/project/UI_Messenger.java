package project;

import java.io.IOException;
import java.util.Date;

import project.communication.Connection;
import project.messages.ReadFile;

public class UI_Messenger implements Runnable {
	
	private Connection connection;
	private boolean shouldDie;
	String fileToSend;
	boolean fileSet;
	
	
	/**
	 * Initializes a messenger client for our UI
	 * @param port the Port number for our connection class
	 */
	public UI_Messenger(int port) {
		this.connection = new Connection("", port);
		this.shouldDie = false;
		Util.LOG = true;
		fileToSend = "";
		fileSet = false;
	}
	/**
	 * Run the connection
	 */
	@Override
	public void run() {
    	connection.init();
		while(!this.shouldDie) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			checkFileToSend();
		}
	}
	
	
	/**
	 * Checks if we have a file to send
	 */
	public void checkFileToSend() {
		if(!fileSet) {
			return;
		}
    	//send a message instead
    	try {
    		ReadFile readFile = new ReadFile(new Date(), fileToSend);
			this.connection.sendMessage(readFile);
			fileToSend = "";
			fileSet = false;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Externally set which file we want to send
	 * @param fileToSend: The file to send
	 */
	public void setFileToSend(String fileToSend) {

		this.fileToSend = fileToSend;
		this.fileSet = true;
	}
	
	
	public void kill() {
		this.shouldDie = true;
	}

}
