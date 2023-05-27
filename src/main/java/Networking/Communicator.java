package Networking;

import Networking.Packets.DataPacket;
import Networking.Packets.HandshakePacket;
import Networking.Packets.HeartbeatPacket;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

abstract class Communicator implements Runnable {
    protected Integer localPort, remotePort;
    protected InetAddress remoteAddress;
    protected volatile PacketReader TcpReader;
    protected Thread TcpReaderThread;
    protected volatile OutputStream outputStream;
    protected volatile ObjectOutputStream objectOutput;
    protected volatile Socket socket = new Socket();
    protected String password = "";
    protected volatile boolean callForShutdown = false, active = false;
    protected volatile Queue<DataPacket> uploadQueue = new LinkedList<DataPacket>();
    protected volatile LocalDateTime lastSuccessfulSend;
    protected volatile Constants.Status status = Constants.Status.Initialized;

    protected Communicator(int localPort, String password){
        this(password);
        this.localPort = localPort;
    }

    protected Communicator(String password) {
        if (password != null)
            this.password = password;
    }

    @Override
    public void run() {
        active = true;
        while (!callForShutdown){
            try {
                if (status != Constants.Status.Connected)
                    establishConnection();
                else if (lastSuccessfulSend.plusSeconds(3).isBefore(LocalDateTime.now())) {
                    sendHeartbeat();
                } else if (uploadQueue.size() == 0){
                    Thread.sleep(10);
                } else {
                    sendPackets();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        performShutdown();
        active = false;
    }

    public void shutDown() { shutDown(true); }
    public void shutDown(boolean halting){
        this.callForShutdown = true;
        while (halting && status != Constants.Status.Stopped);
    }
    public void queuePacket(Object packet) { uploadQueue.add(new DataPacket(packet, password)); }
    public Constants.Status getStatus(){ return status; }


    public ConcurrentLinkedQueue<DataPacket> getReceivedDataPackets() {
        return TcpReader.popDataPackets();
    }

    protected abstract boolean establishConnection();

    protected void performShutdown() {
        try { TcpReader.CloseReader(true); } catch (Exception ignored) {}
        try { outputStream.close(); } catch (Exception ignored) {}
        try { socket.close(); } catch (Exception ignored) {}
        status = Constants.Status.Stopped;
    }

    private void sendHeartbeat() {
        if (!sendSinglePacket(new HeartbeatPacket(password, true)))
            initiateDisconnect();
    }

    protected boolean sendSinglePacket(Object packet) {
        try {
            objectOutput.writeObject(packet);
            lastSuccessfulSend = LocalDateTime.now();
            return true;
        } catch (Exception ignored) {}
        return false;
    }

    private void sendPackets(){
        Queue<DataPacket> packetsToSend = new LinkedList<>(uploadQueue);
        for (int i = 0; i < 10 && !packetsToSend.isEmpty(); i++){
            try {
                objectOutput.writeObject(packetsToSend.peek());
                packetsToSend.poll();
                uploadQueue.poll();//TODO:A bit of a dirty solution
            } catch (IOException e) {
                //e.printStackTrace();
                initiateDisconnect();
                break;
            }
        }
    }

    protected void initiateDisconnect() {
        try { objectOutput.close(); } catch (Exception ignored){} finally { objectOutput = null; }
        try { outputStream.close(); } catch (Exception ignored){} finally { outputStream = null; }
        try { TcpReader.CloseReader(true); } catch (Exception ignored){}
        try { TcpReaderThread.join(); } catch (Exception ignored){} finally { TcpReader = null; TcpReaderThread = null; }
        try { socket.close(); } catch (Exception ignored){} finally { socket = null; }
        status = Constants.Status.Disconnected;
    }

    protected HandshakePacket awaitHandshake(int awaitForInSeconds) {
        ConcurrentLinkedQueue<Object> packets = new ConcurrentLinkedQueue<>();
        for (LocalDateTime started = LocalDateTime.now(); LocalDateTime.now().isBefore(started.plusSeconds(awaitForInSeconds)) && packets.size() == 0; packets = TcpReader.popNonDataPackets()) {
            try { Thread.sleep(100); } catch (Exception ignored){}
        }
        return (HandshakePacket) packets.stream().filter(p -> p instanceof HandshakePacket).findFirst().orElse(null);
    }
}
