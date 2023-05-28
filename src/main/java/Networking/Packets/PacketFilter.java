package Networking.Packets;

import java.io.ObjectInputFilter;

public class PacketFilter implements ObjectInputFilter {
    @Override
    public Status checkInput(FilterInfo filterInfo) {
        Class<?> serialClass = filterInfo.serialClass();
        if (serialClass != null) {
            Status status = serialClass.getName().equals(DataPacket.class.getName())
                    || serialClass.getName().equals(HandshakePacket.class.getName())
                    || serialClass.getName().equals(Packet.class.getName())
                    || serialClass.getName().equals(HeartbeatPacket.class.getName())
                    || serialClass.getName().equals(TestPacket.class.getName())
                    ? Status.ALLOWED : Status.REJECTED;
            if (status == Status.REJECTED)
                System.out.println(serialClass);
            return status;
        }
        return Status.UNDECIDED;
    }
}
