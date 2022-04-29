package project.communication;

import project.Util;
import project.messages.MessageCreator;
import project.messages.Message;
import project.messages.Poll;
import project.messages.Ready;
import project.messages.Setup;

import java.io.*;
import java.net.*;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.Signature;
import java.util.Arrays;
import java.util.Date;

/**
 * Utility which abstracts Datagram sockets and packets
 */
public class Connection {
    public static final int TIMEOUT = 2000;
    private int port; //the port which will be communicated on
    private DatagramSocket socket; //the socket
    private String hostAddr;
    private InetAddress addr;
    private int replyingPort; //the port of the client which made the request
    private final int SIGNATURE_SIZE = 128;
    private int lastMsgLength;

    /**
     * Client Constructor
     * @param hostPort, hostAddr
     */
    public Connection(String hostAddr, int hostPort) {
        this.port = hostPort;
        this.hostAddr = hostAddr;
    }

    /**
     * initializes Datagram sockets
     */
    public void init() {
        Util.debug("Connection.init()");

        if (this.hostAddr == null) {
            try {
                this.socket = new DatagramSocket(this.port);
            } catch (SocketException e) {
                Util.log("Failed to bind port: " + this.port);
                e.printStackTrace();
            }
        } else {
            try {
                if(hostAddr.equals("")) {
                    addr = InetAddress.getLocalHost();
                }
                else {
                    addr = InetAddress.getByName(hostAddr);
                }
                this.socket = new DatagramSocket();
            } catch (SocketException | UnknownHostException e) {
                Util.log("Failed to bind random port");
                e.printStackTrace();
            }
        }
    }

    /**
     * Host Constructor
     * @param bindedPort
     */
    public Connection(int bindedPort){
        this.port = bindedPort;

    }

    /**
     *  Creates a packet given data
     * @param data
     */
    private DatagramPacket getPacket(byte[] data) {
        return new DatagramPacket(data, data.length);
    }

    private DatagramPacket getPacket(byte[] data, int port){
        return new DatagramPacket(data,data.length,addr,port);
    }

    /**
     * Receives the packet with no timeout, and without checking
     * Can be dangerous use with caution
     * @return
     */
    private DatagramPacket receive(){
        //maximum data routed on any UDP request is 576 bytes by protocol
        DatagramPacket packet = getPacket(new byte[576]);

        try {
            socket.receive(packet);
        }catch (IOException e){
            return null;
        }
        this.replyingPort = packet.getPort();
        this.lastMsgLength = packet.getLength();
        return packet;
    }

    /**
     * A generic send which can be use for either synchronous or asynchronous requests
     * @param data
     * @param port
     */
    private void genericSend(byte[] data,int port) throws IOException {
    	DatagramPacket req = this.getPacket(data,port);
        socket.send(req);
    }

    /**
     *  sends a packet without awaiting a response, purely used when responding to a request
     * @param data
     */
    private void sendWithoutResponse(byte[] data) throws IOException {
        genericSend(data,this.replyingPort); //create a response to a request
    }


    /**
     * Used by host to respond to messages
     * @param signature
     * @param m
     */
    public void acknowledge(byte[] signature, Message m) throws IOException {
        byte[] data = serializeMessage(m);
        byte[] dataToSend = new byte[signature.length + data.length];
        System.arraycopy(signature,0,dataToSend,0,signature.length);
        System.arraycopy(data,0,dataToSend,signature.length,data.length);

        this.genericSend(dataToSend,this.replyingPort);
    }

    /**
     * Used by host to respond to messages
     * @param signature
     */
    public void acknowledge(byte[] signature) throws IOException {
        this.acknowledge(signature, null);
    }

    /**
     * Tries to receive a packet, if it times out, it returns null
     * @return
     */
    private DatagramPacket tryReceive(){
        try{
            
            DatagramPacket dat = this.receive();
            this.socket.setSoTimeout(0);
            return dat;
        }catch (Exception e){
            Util.log("Couldn't change timeout time, exiting");
        }
        return null;
    }

    /**
     * This is the hosts receive method, it sets the reply port
     * @return
     */
    public DatagramPacket receiveWithTimeout(){
    	try {
        	this.socket.setSoTimeout(TIMEOUT);//sets timeout to a second
            DatagramPacket packet = this.tryReceive();
            this.socket.setSoTimeout(0);//sets timeout to a second
            
            if(packet == null)
                return null;

            this.replyingPort = packet.getPort();
            this.addr = packet.getAddress();
            return packet;
    	} catch (Exception e) {
    		e.printStackTrace();
    	}

    	return null;

    }

    /**
     *  sends a packet which you expect a response to
     * @param data
     */
    private DatagramPacket send(byte[] signature,byte[] data) throws IOException {
        DatagramPacket packet = null;
        
        // Loop until we get ACK.
        while(packet == null) {
            genericSend(data,this.port);
            this.socket.setSoTimeout(10000);//sets timeout to a second
            packet = tryReceive();
            this.socket.setSoTimeout(0);//sets timeout to a second

            // Go pack if we didn't receive
            if(packet == null) {
                continue;
            }

            // Check if we got an ACK for this message.
            // This is done by checking the signature.
            byte[] received = packet.getData();

            if(isResponse(signature,received)) {
                break;
            }

            // Wrong message, reset to null.
            else {
                packet = null;
            }
        }

        return packet;
    }

    private boolean isResponse(byte[] signature, byte[] data){
        for(int i = 0; i < SIGNATURE_SIZE; i++)
            if(signature[i] != data[i])
                return false;
        return true;
    }

    /**
     * Add a signature to the front of our data so it can be identified
     * @param message Data that will receive a signature
     * @return updated message with a signature
     */
    private byte[] signData(byte message[]){
        byte[] outbuff = new byte[SIGNATURE_SIZE];
        try{
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(1024);

            KeyPair key = kpg.genKeyPair();
            Signature sr = Signature.getInstance("SHA1WithRSA");
            sr.initSign(key.getPrivate());
            sr.update(message);
            sr.sign(outbuff,0,SIGNATURE_SIZE);
        }catch (Exception e){
            Util.log("Couldn't sign message");
            e.printStackTrace();
        }
        return outbuff;
    }


    /**
     * Converts a message into a binary array
     * @param message
     * @return
     */
    private byte[] serializeMessage(Message message){
        try {
            Stream bos = new Stream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(message);
            oos.flush();

            byte[] data = Arrays.copyOf(bos.toByteArray(),bos.toByteArray().length);
            return data;

        } catch (Exception e){
            Util.log("Failed to convert object into a binary array");
            e.printStackTrace();
        }

        throw new Error("Failed to serialize message");
    }


    /**
     * Converts a binary array into a message
     * @param toDeserialize
     * @return
     */
    public Message deserializeMessage(byte[] toDeserialize){
        //removes the signature from the data
        if(isEmpty(toDeserialize))
            return null;
        byte[] data = getMessageBinary(toDeserialize);

        try{
            ByteArrayInputStream in = new ByteArrayInputStream(data);
            ObjectInputStream is = new ObjectInputStream(in);
            return (Message) is.readObject();
        }catch (Exception e){
        	e.printStackTrace();
            Util.log("Failed to Deserialize Data");
        }

        return null;
    }
    /**
     * Polls server for messages
     * @param c: currrent class
     * @param number: the number of the floor/elevator that is sending the message
     * @return
     */
    public Message pollMessages(MessageCreator c, int number) throws IOException {
        byte[] data = serializeMessage(new Poll(new Date(),c,number));

        byte[] signature = signData(data); //gets the signature of the message
        byte[] dataToSend = new byte[data.length + signature.length];

        if(dataToSend.length > 576) {
            Util.log("Message Length Exceeds UDP");
            System.exit(1);
        }

        //combines the array so that the first 128bytes is the signature and the rest is taken up by the object
        System.arraycopy(signature,0,dataToSend,0,signature.length);
        System.arraycopy(data,0,dataToSend,signature.length,data.length);

        return deserializeMessage(this.send(signature,dataToSend).getData());
    }

    /**
     * Sends a message to server
     * @param send
     */
    public void sendMessage(Message send) throws IOException {
        byte[] data = serializeMessage(send);

        byte[] signature = signData(data); //gets the signature of the message
        byte[] dataToSend = new byte[data.length + signature.length];

        if(dataToSend.length > 576) {
            Util.log("Message Lenght Exceeds UDP");
            System.exit(1);
        }


        //combines the array so that the first 128bytes is the signature and the rest is taken up by the object
        System.arraycopy(signature,0,dataToSend,0,signature.length);
        System.arraycopy(data,0,dataToSend,signature.length,data.length);
        this.send(signature,dataToSend);
    }

    /**
     * Registers with the server and returns the number for your class
     * @return number for that class
     */
    public int register(MessageCreator c) throws IOException {

        byte[] data = serializeMessage(new Ready(c,new Date()));

        byte[] signature = signData(data); //gets the signature of the message
        byte[] dataToSend = new byte[data.length + signature.length];

        Util.debug("Packet data length: " + dataToSend.length);

        if(dataToSend.length > 576) {
            Util.log("Message Length Exceeds UDP");
            System.exit(1);
        }

        //combines the array so that the first 128bytes is the signature and the rest is taken up by the object
        System.arraycopy(signature,0,dataToSend,0,signature.length);
        System.arraycopy(data,0,dataToSend,signature.length,data.length);

        DatagramPacket packet = this.send(signature,dataToSend);
        //byte[] responseData =  Arrays.copyOf(packet.getData(), packet.getLength());
        Message m = deserializeMessage(packet.getData());

        return ((Setup) m).number;
    }

    /**
     * Returns tre signature at the beginning of BYTE_ARR
     * @param data
     * @return
     */
    public byte[] getSignature(byte[] data){
        byte[] sig = new byte[SIGNATURE_SIZE];

        for(int i = 0; i < SIGNATURE_SIZE; i++)
            sig[i] = data[i];
        
        sig = Arrays.copyOf(data, SIGNATURE_SIZE);

        return sig;

    }

    /**
     * Returns the data at the end of BYTE_ARR
     * @param data
     * @return
     */
    public byte[] getMessageBinary(byte[] data){
        byte[] msg = new byte[data.length - SIGNATURE_SIZE];

        for(int i = data.length - 1; i >= SIGNATURE_SIZE; i--)
            msg[i-SIGNATURE_SIZE] = data[i];

        msg = Arrays.copyOfRange(data, SIGNATURE_SIZE, data.length);
        return msg;

    }



    /**
     * Closes socket
     */
    public void close(){
        socket.close();
    }

    /**
     * Checks if this was a polling message
     * @param data
     * @return
     */
    public boolean isEmpty(byte data[]){
        for(byte b: data){
            if(b != 0x00)
                return false;
        }
        return true;
    }


}
