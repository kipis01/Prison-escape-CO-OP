package Networking.Packets;

public class HandshakePacket extends Packet {
    public int clientPort;

    public HandshakePacket(String password, int clientPort) {
        super(password);
        this.clientPort = clientPort;
    }
}
