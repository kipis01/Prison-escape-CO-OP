package Networking;

import Networking.Packets.TestPacket;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.Queue;

import static org.junit.jupiter.api.Assertions.*;

class NetWorkerTest {
    NetWorker client, server;
    final String PASSWORD = "testing_123", SERVER_ADD = "localhost";
    final int SERVER_PORT = 8000, WaitForConnectionInMs = 500;//2000;


    final Float floatData1 = 5.45f, floatData2 = -838.156f;
    final String stringData1 = "testing";

    @BeforeEach
    void setUp() throws UnknownHostException, InterruptedException {
        client = new NetWorker(InetAddress.getByName(SERVER_ADD), SERVER_PORT, PASSWORD);
        server = new NetWorker(SERVER_PORT, PASSWORD);
        client.startNetWorker();
        server.startNetWorker();
        Thread.sleep(WaitForConnectionInMs);
    }

    @AfterEach
    void tearDown() {
        if (client != null) client.stopNetWorker(true);
        if (server != null) server.stopNetWorker(true);
        client = server = null;
    }

    @Test
    void connection() throws UnknownHostException, InterruptedException {
        assertEquals(Constants.Status.Connected, server.getStatus());
        assertEquals(Constants.Status.Connected, client.getStatus());

        server.stopNetWorker(true);
        client.stopNetWorker(true);
        Thread.sleep(WaitForConnectionInMs);
        assertEquals(Constants.Status.Stopped, server.getStatus());
        assertEquals(Constants.Status.Stopped, client.getStatus());

        server = new NetWorker(SERVER_PORT, PASSWORD);
        client = new NetWorker(InetAddress.getByName(SERVER_ADD), SERVER_PORT, "2");
        assertEquals(Constants.Status.Initialized, server.getStatus());
        assertEquals(Constants.Status.Initialized, client.getStatus());

        server.startNetWorker();
        client.startNetWorker();
        Thread.sleep(WaitForConnectionInMs);
        assertEquals(Constants.Status.Disconnected, server.getStatus());
        assertEquals(Constants.Status.Disconnected, client.getStatus());
    }

    private Queue<Object> waitForPackets(NetWorker net, int waitInS) throws InterruptedException {
        Queue<Object> recPackets = new LinkedList<>();
        for (LocalDateTime waitUntil = LocalDateTime.now().plusSeconds(waitInS); waitUntil.isAfter(LocalDateTime.now()) && recPackets.size() < 2; recPackets.addAll(net.getReceivedPackets()))
            Thread.sleep(100);
        return recPackets;
    }

    @Test
    void communication() throws InterruptedException {
        server.SendPacket(stringData1);
        server.SendPacket(floatData1);
        Queue<Object> recPackets = waitForPackets(client, 1);

        assertEquals(2, recPackets.size());
        Object data = recPackets.poll();
        assertNotNull(data);
        assertInstanceOf(String.class, data);
        assertEquals(stringData1, (String)data);
        data = recPackets.poll();
        assertInstanceOf(Float.class, data);
        assertNotNull(data);
        assertEquals(floatData1, (Float)data);
        assertEquals(0, recPackets.size());

        client.SendPacket(new TestPacket(floatData1, floatData2, stringData1));
        server.SendPacket(new TestPacket(floatData2, floatData1,  stringData1));

        recPackets = waitForPackets(server, 1);
        assertEquals(1, recPackets.size());
        data = recPackets.poll();
        assertNotNull(data);
        assertInstanceOf(TestPacket.class, data);
        TestPacket packet = (TestPacket)data;
        assertEquals(floatData1, packet.float1);
        assertEquals(floatData2, packet.float2);
        assertEquals(stringData1, packet.string1);
        assertEquals(0, recPackets.size());

        recPackets = waitForPackets(client, 1);
        assertEquals(1, recPackets.size());
        data = recPackets.poll();
        assertNotNull(data);
        assertInstanceOf(TestPacket.class, data);
        packet = (TestPacket)data;
        assertEquals(floatData2, packet.float1);
        assertEquals(floatData1, packet.float2);
        assertEquals(stringData1, packet.string1);
        assertEquals(0, recPackets.size());
    }

    /**
     * Ensures, that no inner packets are presented
     * @throws InterruptedException
     */
    @Test
    void packetFilter() throws InterruptedException {
        Thread.sleep(10000);
        assertEquals(0, server.getReceivedPackets().size());
        assertEquals(0, client.getReceivedPackets().size());
    }

    /**
     * Tests connection reacquire on sudden connection loss
     * @throws InterruptedException
     * @throws UnknownHostException
     */
    @Test
    void connectionLoss() throws InterruptedException, UnknownHostException {
        client.communicator.TcpReaderThread.interrupt();
        client.communicator.TcpReaderThread.join();
        client.thread.interrupt();
        client.thread.join();
        Thread.sleep(2000);
        assertEquals(Constants.Status.Disconnected, server.getStatus());

        client = new NetWorker(InetAddress.getByName(SERVER_ADD), SERVER_PORT, PASSWORD);
        client.startNetWorker();
        server.SendPacket(new TestPacket(floatData1, floatData2, stringData1));
        client.SendPacket(new TestPacket(floatData2, floatData1, stringData1));

        Queue<Object> rpackets = waitForPackets(client, 5);
        assertEquals(Constants.Status.Connected, server.getStatus());
        assertEquals(Constants.Status.Connected, client.getStatus());
        assertEquals(1, rpackets.size());
        Object packet = rpackets.poll();
        assertInstanceOf(TestPacket.class, packet);
        assertEquals(new TestPacket(floatData1, floatData2, stringData1), packet);

        rpackets = waitForPackets(server, 1);
        assertEquals(1, rpackets.size());
        packet = rpackets.poll();
        assertEquals(new TestPacket(floatData2, floatData1, stringData1), packet);
    }

    /**
     * Tests connection loss detection
     * @throws InterruptedException
     */
    @Test
    void heartBeat() throws InterruptedException {
        client.communicator.TcpReaderThread.interrupt();
        client.communicator.TcpReaderThread.join();
        client.thread.interrupt();
        client.thread.join();
        Thread.sleep(4000);
        client = null;

        assertEquals(Constants.Status.Disconnected, server.getStatus());
    }
}