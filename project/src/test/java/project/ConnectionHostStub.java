package project;

import project.communication.Connection;
import project.messages.Message;
import project.messages.Ready;
import project.messages.Setup;

import java.net.DatagramPacket;
import java.util.Date;

public class ConnectionHostStub implements Runnable{

    private Connection con;

    public ConnectionHostStub(int port){
        this.con = new Connection(port);
    }

    public boolean notDead(){
        return true;
    }

    /**
     * Tries to receive
     */
    public void receiving(){
        DatagramPacket packet = con.receiveWithTimeout();

        if(packet == null)
            return;
        byte[] sign = con.getSignature(packet.getData());

        Message m = con.deserializeMessage(packet.getData());
        try{
            if(m instanceof Ready){
                con.acknowledge(sign,new Setup(new Date(),1));
            }
        }catch (Exception e){
        	e.printStackTrace();
            Util.log("Generic Message Exception");
        }




    }
    @Override
    public void run() {
        con.init();
        while (true)
            receiving();

    }
}
