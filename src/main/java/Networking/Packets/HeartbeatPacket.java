package Networking.Packets;

public class HeartbeatPacket extends Packet {
    public boolean respond = true;

    public HeartbeatPacket(String password, boolean respond) {
        super(password);
        this.respond = respond;
    }
}
