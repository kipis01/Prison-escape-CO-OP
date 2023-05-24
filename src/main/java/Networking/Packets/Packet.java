package Networking.Packets;

public class Packet {
    public String password = "";

    public Packet(String password) {
        if (password != null)
            this.password = password;
    }
}
