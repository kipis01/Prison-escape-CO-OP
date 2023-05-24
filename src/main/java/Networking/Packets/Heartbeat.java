package Networking.Packets;

public class Heartbeat extends Packet {
    public boolean respond = true;

    public Heartbeat(String password, boolean respond) {
        super(password);
        this.respond = respond;
    }
}
