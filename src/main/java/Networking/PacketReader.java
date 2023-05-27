package Networking;

import Networking.Packets.DataPacket;
import Networking.Packets.HandshakePacket;
import Networking.Packets.HeartbeatPacket;
import Networking.Packets.PacketFilter;

import javax.xml.crypto.Data;
import java.io.*;
import java.net.SocketTimeoutException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

class PacketReader implements Runnable {
    private ObjectInputStream objectInput = null;
    private InputStream inputStream = null;
    private volatile ConcurrentLinkedQueue<Object> receivedPackets;
    private volatile boolean read = true, isRunning = false;

    public PacketReader(InputStream inputStream) throws IOException {
        this.inputStream = inputStream;
        objectInput = new ObjectInputStream(this.inputStream);
        //objectInput.setObjectInputFilter(new PacketFilter());//FIXME
        receivedPackets = new ConcurrentLinkedQueue<>();
    }

    public void CloseReader(){ CloseReader(true); }

    /**
     * Attempts to gracefully close the reader
     * @param halting if true, waits until the reader is closed, otherwise isRunning needs to be polled to wait for full shutdown
     */
    public void CloseReader(boolean halting) {
        read = false;
        if (!halting) return;

        try {
            while (isRunning())
                Thread.sleep(10);
        } catch (InterruptedException ignored){}
    }

    public boolean isRunning(){ return isRunning; }
    public ConcurrentLinkedQueue<Object> get() { return receivedPackets; }

    public ConcurrentLinkedQueue<Object> pop() {
        ConcurrentLinkedQueue<Object> list = receivedPackets;
        receivedPackets = new ConcurrentLinkedQueue<>();
        return list;
    }

    public ConcurrentLinkedQueue<DataPacket> popDataPackets() {
        ConcurrentLinkedQueue<DataPacket> list = new ConcurrentLinkedQueue<>(receivedPackets.stream().filter(s -> s instanceof DataPacket).map(m -> new DataPacket((DataPacket)m)).toList());
        receivedPackets = new ConcurrentLinkedQueue<>(receivedPackets.stream().filter(s -> !(s instanceof DataPacket)).toList());//FIXME:Non-atomic operation
        return list;
    }

    public ConcurrentLinkedQueue<Object> popNonDataPackets() {
        ConcurrentLinkedQueue<Object> list = new ConcurrentLinkedQueue<>(receivedPackets.stream().filter(s -> !(s instanceof DataPacket)).toList());
        receivedPackets = new ConcurrentLinkedQueue<>(receivedPackets.stream().filter(s -> s instanceof DataPacket).toList());//FIXME:Non-atomic operation
        return list;
    }

    @Override
    public void run() {//TODO:Decide on exception handling
        isRunning = true;
        while (read) {
            try {
                //objectInput.reset();//TODO:Find out if this is needed after a timeout
                Object receivedPacket = objectInput.readObject();
                if (!(receivedPacket instanceof HeartbeatPacket))
                    receivedPackets.add(receivedPacket);
            } catch (EOFException e){
                CloseReader(false);//TODO:Account for this in Communicator
            } catch (IOException e) {
                if (e instanceof SocketTimeoutException)
                    continue;
                //e.printStackTrace(); //TODO:Handle this if needed
            } catch (ClassNotFoundException ignored) {}
        }
        try {
            objectInput.close();
            inputStream.close();
        } catch (Exception ignored){}
        objectInput = null;
        isRunning = false;
    }
}
