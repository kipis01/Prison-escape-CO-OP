package Networking.Packets;

public class HandshakePacket extends Packet {
    public int clientPort;

    public HandshakePacket(String password, int clientPort) {
        super(password);
        this.clientPort = clientPort;
    }
    public HandshakePacket (HandshakePacket packet) {
        super(packet.password);
        clientPort = packet.clientPort;
    }
}
