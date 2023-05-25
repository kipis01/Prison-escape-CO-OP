package Networking;

import Networking.Exceptions.ServerDisconnectException;
import Networking.Exceptions.ServerFailedException;
import Networking.Packets.HandshakePacket;
import Networking.Packets.DataPacket;
import Networking.Packets.HeartbeatPacket;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.*;

class Server implements Runnable {
    final private int serverPort;
    private Integer clientPort = null;
    private InetAddress clientAddress = null;
    private ServerSocket serverSocket = null;
    private PacketReader TcpReader = null;
    private OutputStream outputStream = null;
    private ObjectOutputStream objectOutput = null;
    private Socket socket = null;
    private String password = "";
    private volatile boolean finished = false;
    private volatile Queue<DataPacket> dataPackets = new LinkedList<DataPacket>();
    private LocalDateTime lastSuccessfulSend = null;
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
                else if (lastSuccessfulSend.plusSeconds(3).isBefore(LocalDateTime.now())) {
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
        try { TcpReader.CloseReader(); } catch (Exception ignored) {}
        try { outputStream.close(); } catch (Exception ignored) {}
        try { socket.close(); } catch (Exception ignored) {}
        try { serverSocket.close(); } catch (Exception ignored) {}
    }

    private boolean establishConnection() throws IOException {
        if (serverSocket == null) {
            serverSocket = new ServerSocket(serverPort);
            serverSocket.setSoTimeout(2000);
        }
        socket = serverSocket.accept();
        socket.setKeepAlive(true);
        socket.setSoTimeout(500);
        TcpReader = new PacketReader(socket.getInputStream());
        TcpReader.run();
        outputStream = socket.getOutputStream();
        objectOutput = new ObjectOutputStream(outputStream);

        try{
            List<Object> packets = new LinkedList<>();
            for (LocalDateTime started = LocalDateTime.now(); LocalDateTime.now().isBefore(started.plusSeconds(5)) && packets.size() == 0; packets = TcpReader.get()) {
                try { Thread.sleep(100); } catch (Exception ignored){}
            }

            HandshakePacket rPacket = (HandshakePacket) packets.stream().filter(p -> p instanceof HandshakePacket).findFirst().orElse(null);
            if (rPacket != null && password.equals(rPacket.password)) {
                clientAddress = socket.getInetAddress();
                clientPort = socket.getPort();
                return true;
            }
        } catch (Exception ignored) { }

        objectOutput.close(); objectOutput = null;
        outputStream.close(); outputStream = null;
        TcpReader.CloseReader(true); TcpReader = null;
        socket.close(); socket = null;
        return false;
    }

    private void sendHeartbeat() {//TODO:Check if a packet drop warrants a connection restart
        if (!sendSinglePacket(new HeartbeatPacket(password, true)))
            initiateDisconnect();
    }

    private boolean sendSinglePacket(Object packet) {
        try {
            objectOutput.writeObject(packet);
            lastSuccessfulSend = LocalDateTime.now();
            return true;
        } catch (IOException e) {
            e.printStackTrace();//TODO:Silence the output
        }
        return false;
    }

    private void initiateDisconnect() {
        try { objectOutput.close(); } catch (Exception ignored){} finally { objectOutput = null; }
        try { outputStream.close(); } catch (Exception ignored){} finally { outputStream = null; }
        try { TcpReader.CloseReader(true); } catch (Exception ignored){} finally { TcpReader = null; }
        try { socket.close(); } catch (Exception ignored){} finally { socket = null; }
        clientPort = null;
        clientAddress = null;
    }

    public void shutDown(){ this.finished = true; }

    public void queuePacket(Object packet) throws ServerFailedException {
        dataPackets.add(new DataPacket(packet, password));
    }

    private void sendPackets(){
        Queue<DataPacket> packetsToSend = new LinkedList<>(dataPackets);
        for (int i = 0; i < 10 && !packetsToSend.isEmpty(); i++){
            try {
                objectOutput.writeObject(packetsToSend.peek());
                packetsToSend.poll();
            } catch (IOException e) {
                e.printStackTrace();//TODO:Remove
                initiateDisconnect();
                break;
            }
        }
    }

    public List<Object> getReceivedPackets() {
        return TcpReader.pop().stream().filter(p -> !(p instanceof HeartbeatPacket || p instanceof HandshakePacket)).toList();
    }

    public boolean serverStatus() throws ServerFailedException, ServerDisconnectException {
        if (serverFailure != null)
            throw serverFailure;
        if (serverDisconnected != null)
            throw serverDisconnected;
        return true;
    }
}
