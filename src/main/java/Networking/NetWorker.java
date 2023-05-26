package Networking;

import Networking.Packets.DataPacket;

import java.net.InetAddress;
import java.util.List;

public class NetWorker {
    private Communicator communicator;
    private Thread thread;

    /**
     * Initiates a server
     * @param serverPort listen on this port
     * @param password Only allow connections using this password
     */
    public NetWorker(int serverPort, String password) {
        communicator = new Server(serverPort, password);
    }

    /**
     * Initiates a client
     * @param serverAddress Which server to connect to
     * @param serverPort Server port
     * @param password The password that the server is using
     */
    public NetWorker(InetAddress serverAddress, int serverPort, String password) {
        communicator = new Client(serverAddress, serverPort, password);
    }

    /**
     * Starts the server
     */
    public void startNetWorker() {
        if (communicator.getStatus() != Constants.Status.Initialized)
            return;
        thread = new Thread(communicator);
        thread.start();
    }

    /**
     * Stops the server
     * @param halting If true, waits for the server to shut down, otherwise shutdown is performed in background and serverStatus needs to be polled to ensure graceful shutdown
     */
    public void stopNetWorker(boolean halting) {//FIXME:Halts on initialized, never run
        communicator.shutDown(halting);
    }

    /**
     * Queues up a packet for send
     * @param packet the object that needs to be sent
     */
    public void SendPacket(Object packet) { communicator.queuePacket(packet); }
    public List<DataPacket> getReceivedPackets() { return communicator.getReceivedDataPackets(); }
    public Constants.Status getStatus(){ return communicator.getStatus(); }
}
