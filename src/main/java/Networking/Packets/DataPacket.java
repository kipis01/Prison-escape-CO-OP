package Networking.Packets;

public class DataPacket extends Packet {
    public Object data;

    public DataPacket(Object data, String password){
        super(password);
        this.data = data;
    }
}
