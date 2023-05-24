package Networking;

import Networking.Packets.DataPacket;
import Networking.Packets.HandshakePacket;
import Networking.Packets.HeartbeatPacket;
import Networking.Packets.PacketFilter;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputFilter;
import java.io.ObjectInputStream;
import java.net.SocketTimeoutException;
import java.util.LinkedList;
import java.util.List;

class PacketReader implements Runnable {
    private ObjectInputStream objectInput = null;
    private volatile List<Object> receivedPackets;
    private boolean read = true;

    public PacketReader(InputStream inputStream) throws IOException {
        objectInput = new ObjectInputStream(inputStream);
        objectInput.setObjectInputFilter(new PacketFilter());
        receivedPackets = new LinkedList<>();
    }

    public void CloseReader() {
        read = false;
    }

    public List<Object> pop() {
        List<Object> list = receivedPackets;
        receivedPackets = new LinkedList<>();
        return list;
    }

    @Override
    public void run() {//TODO:Decide on exception handling
        while (read) {
            try {
                //objectInput.reset();//TODO:Find out if this is needed after a timeout
                receivedPackets.add(objectInput.readObject());
            } catch (IOException e) {
                if (e instanceof SocketTimeoutException)
                    continue;
                throw new RuntimeException(e);
            } catch (ClassNotFoundException ignored) { }
        }
    }
}
