package Networking;

import Networking.Exceptions.ServerFailedException;
import Networking.Packets.HandshakePacket;
import Networking.Packets.Packet;

import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;

public class Server implements Runnable{
    final private int serverPort;
    private Integer peerPort = null;
    private InetAddress clientAddress = null;
    private ServerSocket serverSocket = null;
    private Socket socket = null;
    private String password;
    private volatile boolean finished = false;
    private volatile Queue<Packet> packets = new LinkedList<Packet>();
    ServerFailedException serverFailure = null;

    Server(int clientSidePort, String password){
        this.serverPort = clientSidePort;
        this.password = password;
    }

    public void run(){
        InputStream input;
        ObjectInputStream objectInput;
        OutputStream output;
        ObjectOutputStream objectOutput;
        try {
            serverSocket = new ServerSocket(serverPort);
            socket = serverSocket.accept();
            input = socket.getInputStream();
            objectInput = new ObjectInputStream(input);
            output = socket.getOutputStream();
            objectOutput = new ObjectOutputStream(output);

            while(!finished || peerPort == null){
                Object rpacket = objectInput.read();
                InetAddress address = new InetSocketAddress(socket.getRemoteSocketAddress().toString().);
                if (rpacket instanceof HandshakePacket){
                    //TODO:Continue
                }
                //Thread.sleep(250);
            }
        } catch (IOException e) {
            serverFailure = new ServerFailedException(e.toString());
            return;
        }

        while (!finished){
            Queue<Packet> packetsToSend = new LinkedList<Packet>(packets);//Cloning in case this list gets altered during send
            for (Packet packet : packetsToSend) {
                //TODO
            }
            //Thread.sleep(250);
        }
    }

    public void shutDown(){ this.finished = true; }

    public void queuePacket(Object packet) throws ServerFailedException {
        if (serverFailure != null)
            throw serverFailure;
        packets.add(new Packet(packet, password));
    }
}
