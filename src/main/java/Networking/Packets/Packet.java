package Networking.Packets;

public class Packet {
    public Object data;
    public String password;


    public Packet(Object data){ this(data, null); }
    public Packet(Object data, String password){
        this.data = data;
        this.password = password;
    }
}
