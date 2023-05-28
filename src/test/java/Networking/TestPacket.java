package Networking;

import java.io.Serializable;

public class TestPacket implements Serializable {
    public Float float1, float2;
    public String string1;

    public TestPacket(float float1, float float2, String string1) {
        this.float1 = float1;
        this.float2 = float2;
        this.string1 = string1;
    }
}
