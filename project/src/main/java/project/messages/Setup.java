package project.messages;

import java.util.Date;

/**
 * Setup message class
 *
 */
public class Setup extends Message{

    public final int number;
    /**
     * Constructor that specifies what time this event was created.
     *
     * @param eventTimeStamp the event time stamp
     */
    public Setup(Date eventTimeStamp,int number) {
        super(eventTimeStamp);
        this.number = number;
    }
}
