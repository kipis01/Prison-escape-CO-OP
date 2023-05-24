package Networking;

import Networking.Exceptions.ServerFailedException;

import javax.management.InstanceAlreadyExistsException;
import java.net.InetAddress;
import java.net.ServerSocket;

public class ServerManager {
    Server server;
    private Thread serverThread = null;

    public ServerManager(){ this(2055, null); }
    public ServerManager(int clientSidePort, String password) {
        server = new Server(clientSidePort, password);
    }

    public void startServer() throws InstanceAlreadyExistsException {
        if (serverThread == null)
            throw new InstanceAlreadyExistsException();
        serverThread = new Thread(server);
    }

    public void stopServer() throws InterruptedException {
        server.shutDown();
        serverThread.join(1000);
    }

    //TODO:Add important and less important packet handling through TCP and UDP
    public void SendPacket(Object object) throws ServerFailedException {
        server.queuePacket(object);
    }
}
