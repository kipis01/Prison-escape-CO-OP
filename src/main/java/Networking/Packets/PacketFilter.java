package Networking.Packets;

import java.io.ObjectInputFilter;

public class PacketFilter implements ObjectInputFilter {
    @Override
    public Status checkInput(FilterInfo filterInfo) {
        return filterInfo.serialClass() == DataPacket.class
                || filterInfo.serialClass() == HandshakePacket.class
                || filterInfo.serialClass() == DataPacket.class
                ? Status.ALLOWED : Status.REJECTED;
    }
}
