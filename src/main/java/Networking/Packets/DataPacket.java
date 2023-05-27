package Networking.Packets;

import java.io.Serializable;

public class DataPacket extends Packet implements Serializable {
    public Object data;

    public DataPacket(Object data, String password){
        super(password);
        this.data = data;
    }

    public DataPacket(Object packet) {
        this(((DataPacket) packet).data, ((DataPacket) packet).password);
    }
}
