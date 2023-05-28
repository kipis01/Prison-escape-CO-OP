package Networking;

import Networking.Packets.HandshakePacket;
import java.io.*;
import java.net.ServerSocket;
import java.util.Objects;

class Server extends Communicator {
    private ServerSocket serverSocket = null;

    public Server (int localPort, String password) {
        super(localPort, password);
    }

    @Override
    protected boolean establishConnection() {
        try {
            if (serverSocket == null) {
                serverSocket = new ServerSocket(localPort);
                serverSocket.setSoTimeout(2000);
            }
            socket = serverSocket.accept();
            socket.setKeepAlive(true);
            socket.setSoTimeout(1000);
            outputStream = socket.getOutputStream();
            objectOutput = new ObjectOutputStream(outputStream);
            TcpReader = new PacketReader(socket.getInputStream());
            TcpReaderThread = new Thread(TcpReader);
            TcpReaderThread.start();

            HandshakePacket hPacket = awaitHandshake(5);
            if (hPacket != null && Objects.equals(hPacket.password, password)) {
                sendSinglePacket(new HandshakePacket(password, localPort));
                objectOutput.writeObject(new HandshakePacket(password, localPort));//Accepts
                remoteAddress = socket.getInetAddress();
                remotePort = socket.getPort();
                status = Constants.Status.Connected;
                return true;
            }
        } catch (Exception ignored) {}

        initiateDisconnect();
        return false;
    }

    protected void performShutdown() {
        try { serverSocket.close(); } catch (Exception ignored) {}
        super.performShutdown();
    }
}
