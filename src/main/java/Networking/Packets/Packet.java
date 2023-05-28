package Networking.Packets;

import java.io.Serializable;

public class Packet implements Serializable {
    public String password = "";

    public Packet(String password) {
        if (password != null)
            this.password = password;
    }
}
