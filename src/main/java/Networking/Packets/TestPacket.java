package Networking.Packets;

import java.io.Serializable;
import java.util.Objects;

public class TestPacket implements Serializable {
    public Float float1, float2;
    public String string1;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TestPacket that = (TestPacket) o;
        return Objects.equals(float1, that.float1) && Objects.equals(float2, that.float2) && Objects.equals(string1, that.string1);
    }

    @Override
    public int hashCode() {
        return Objects.hash(float1, float2, string1);
    }

    public TestPacket(float float1, float float2, String string1) {
        this.float1 = float1;
        this.float2 = float2;
        this.string1 = string1;
    }
}
