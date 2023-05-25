package Networking;

import Networking.Packets.DataPacket;
import Networking.Packets.HandshakePacket;
import Networking.Packets.HeartbeatPacket;
import Networking.Packets.PacketFilter;

import java.io.*;
import java.net.SocketTimeoutException;
import java.util.LinkedList;
import java.util.List;

class PacketReader implements Runnable {
    private ObjectInputStream objectInput = null;
    private InputStream inputStream = null;
    private volatile List<Object> receivedPackets;
    private volatile boolean read = true, isRunning = false;

    public PacketReader(InputStream inputStream) throws IOException {
        this.inputStream = inputStream;
        objectInput = new ObjectInputStream(this.inputStream);
        objectInput.setObjectInputFilter(new PacketFilter());
        receivedPackets = new LinkedList<>();
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
    public List<Object> get() { return receivedPackets; }

    public List<Object> pop() {
        List<Object> list = receivedPackets;
        receivedPackets = new LinkedList<>();
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
                System.out.println("EOF on server input");//TODO:Test
                CloseReader();
            } catch (IOException e) {
                if (e instanceof SocketTimeoutException)
                    continue;
                e.printStackTrace();
                //TODO:Read needs to be reacquired probably
            } catch (ClassNotFoundException ignored) { ignored.printStackTrace(); }//TODO:Ignore
        }
        try {
            objectInput.close();
            inputStream.close();
        } catch (Exception ignored){}
        objectInput = null;
        isRunning = false;
    }
}
