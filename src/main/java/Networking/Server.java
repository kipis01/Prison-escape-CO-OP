package Networking;

import Networking.Exceptions.ServerDisconnectException;
import Networking.Exceptions.ServerFailedException;
import Networking.Packets.HandshakePacket;
import Networking.Packets.DataPacket;
import Networking.Packets.Packet;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;

class Server implements Runnable {
    final private int serverPort;
    //TODO:Add UDP
    private Integer clientPort = null;
    private InetAddress clientAddress = null;
    private ServerSocket serverSocket = null;
    private InputStream inputStream = null;//TODO:Move to PacketReader
    private ObjectInputStream objectInput = null;//TODO:Move to PacketReader
    private OutputStream outputStream = null;
    private ObjectOutputStream objectOutput = null;
    private Socket socket = null;
    private String password = "";
    private volatile boolean finished = false;
    private volatile Queue<DataPacket> dataPackets = new LinkedList<DataPacket>();
    private LocalDateTime lastReceive = null;
    private ServerFailedException serverFailure = null;
    private ServerDisconnectException serverDisconnected = null;

    Server(int clientSidePort, String password){
        this.serverPort = clientSidePort;
        if (password != null)
            this.password = password;
    }

    public void run(){
        while (!finished){
            try {
                if (clientAddress == null)
                    establishConnection();
                else if (lastReceive.plusSeconds(3).isBefore(LocalDateTime.now())) {
                    sendHeartbeat();
                } else if (dataPackets.size() == 0){
                    Thread.sleep(10);
                } else {
                    sendPackets();
                }
            } catch (IOException e) {
                serverFailure = new ServerFailedException(e.toString());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        try {
            if (inputStream != null)
                inputStream.close();
            if (outputStream != null)
                outputStream.close();
            if (socket != null)
                socket.close();
            if (serverSocket != null)
                serverSocket.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    private void sendPackets(){
        Queue<DataPacket> packetsToSend = new LinkedList<>(dataPackets);
        for (int i = 0; i < 10 && !packetsToSend.isEmpty(); i++){
            try {
                objectOutput.writeObject(packetsToSend.peek());
                packetsToSend.poll();
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }
    }

    private boolean establishConnection() throws IOException {
        if (serverSocket == null) {
            serverSocket = new ServerSocket(serverPort);
            serverSocket.setSoTimeout(2000);
        }
        socket = serverSocket.accept();
        socket.setKeepAlive(true);
        socket.setSoTimeout(500);
        inputStream = socket.getInputStream();
        objectInput = new ObjectInputStream(inputStream);
        outputStream = socket.getOutputStream();
        objectOutput = new ObjectOutputStream(outputStream);

        try{
            Object rPacket = objectInput.readObject();
            if (rPacket instanceof HandshakePacket && Objects.equals(((HandshakePacket) rPacket).password, password)){
                clientAddress = socket.getInetAddress();
                clientPort = socket.getPort();
                return true;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    private boolean sendHeartbeat() {
        //TODO:Implement
        return false;
    }

    public void shutDown(){ this.finished = true; }

    public void queuePacket(Object packet) throws ServerFailedException {
        dataPackets.add(new DataPacket(packet, password));
    }

    public boolean serverStatus() throws ServerFailedException, ServerDisconnectException {
        if (serverFailure != null)
            throw serverFailure;
        if (serverDisconnected != null)
            throw serverDisconnected;
        return true;
    }
}
