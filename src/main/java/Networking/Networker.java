package Networking;

import Networking.Exceptions.ServerFailedException;

import javax.management.InstanceAlreadyExistsException;
import java.net.InetAddress;

public class Networker {
    Communicator communicator;

    /**
     * Initiates a server
     * @param serverPort listen on this port
     * @param password Only allow connections using this password
     */
    public Networker(int serverPort, String password) {
        communicator = new Server(serverPort, password);
    }

    /**
     * Initiates a client
     * @param serverAddress Which server to connect to
     * @param serverPort Server port
     * @param password The password that the server is using
     */
    public Networker (InetAddress serverAddress, int serverPort, String password) {
        communicator = new Client(serverAddress, serverPort, password);
    }

    public void startServer() {
        if (communicator.getStatus() == Constants.Status.Initialized)
            communicator.run();
    }

    public void stopServer() throws InterruptedException {//TODO:Continue
        server.shutDown();
        serverThread.join(1000);
    }

    //TODO:Add important and less important packet handling through TCP and UDP
    public void SendPacket(Object object) throws ServerFailedException {
        server.queuePacket(object);
    }
}
