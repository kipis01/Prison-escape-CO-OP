package Networking.Packets;

public class HandshakePacket {
    String password;
    int clientPort;

    public HandshakePacket(String password, int clientPort) {
        this.password = password;
        this.clientPort = clientPort;
    }
}
