package project;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ConnectionTest {


    public static void main(String args[]){
        ConnectionClientStub cl = new ConnectionClientStub(5076);
        ConnectionHostStub hs = new ConnectionHostStub(5076);
        Thread clThread = new Thread(cl, "Client");
        Thread hsThread = new Thread(hs,"Host");

        clThread.start();
        hsThread.start();

    }
}
