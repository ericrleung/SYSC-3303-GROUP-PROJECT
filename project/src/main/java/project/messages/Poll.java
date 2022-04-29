package project.messages;

import java.util.Date;

/**
 * Poll message class
 *
 */
public class Poll extends Message {

    public final boolean isElevator;
    public final int number;
    /**
     * Constructor that specifies what time this event was created.
     *
     * @param eventTimeStamp the event time stamp
     */
    public Poll(Date eventTimeStamp, MessageCreator c, int number) {
        super(eventTimeStamp);
        if(c == MessageCreator.ELEVATOR)
            isElevator = true;
        else
            isElevator = false;

        this.number = number;


    }
}
