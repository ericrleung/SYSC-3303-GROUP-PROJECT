package project;

import project.communication.Connection;
import project.messages.MessageCreator;

import java.io.IOException;

public class ConnectionClientStub implements Runnable{
    private Connection con;
    protected int setup_num;

    public ConnectionClientStub(int port){
        this.con = new Connection("",port);
    }
    @Override
    public void run() {
        con.init();
        try {
            this.setup_num = con.register(MessageCreator.ELEVATOR);
        } catch (IOException e) {
            Util.log("Generic Send Error");
        }
        Util.log(""+setup_num);
        

    }
}
