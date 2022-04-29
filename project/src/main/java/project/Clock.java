package project;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * General clock utility  class
 */
public class Clock {

	//constant to increment by
    public final static int INCREMENT_MS = 1000;

    private Date date;
    private final String START_TIME = "14:06:13.0";

    /**
     * Clock constructor sets the date to a few seconds before initial message (14:06:13.0)
     */
    public Clock() {
        try {
        	date = new SimpleDateFormat("HH:mm:ss").parse(START_TIME);
		} catch (ParseException e) {
			e.printStackTrace();
		}
    }

    /**
     * Increment time by 1 second
     */
    public void increment() {
        date.setTime(date.getTime() + INCREMENT_MS);
    }


    /**
     * Compares the date time to a passed in time.
     * @param time
     * @return boolean if they are the same.
     */
    public boolean isTime(long time) {
       return date.getTime() == time;
    }


}
