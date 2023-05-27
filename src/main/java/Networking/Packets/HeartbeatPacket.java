package Networking.Packets;

import java.io.Serializable;

public class HeartbeatPacket extends Packet implements Serializable {
    public boolean respond = true;

    public HeartbeatPacket(String password, boolean respond) {
        super(password);
        this.respond = respond;
    }
}
