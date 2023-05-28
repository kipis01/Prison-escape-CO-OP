package Networking;

import Networking.Packets.HandshakePacket;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

class Client extends Communicator {
    public Client (InetAddress serverAddress, int serverPort, String password){
        super(password);
        remoteAddress = serverAddress;
        remotePort = serverPort;
    }

    @Override
    protected boolean establishConnection() {
        try {
            socket = new Socket(remoteAddress, remotePort);
            socket.setKeepAlive(true);
            socket.setSoTimeout(500);
            outputStream = socket.getOutputStream();
            objectOutput = new ObjectOutputStream(outputStream);
            TcpReader = new PacketReader(socket.getInputStream());
            TcpReaderThread = new Thread(TcpReader);
            TcpReaderThread.start();
            localPort = socket.getLocalPort();

            sendSinglePacket(new HandshakePacket(password, localPort));

            HandshakePacket hPacket = awaitHandshake(5);
            if (hPacket != null) {
                status = Constants.Status.Connected;
                return true;
            }
        } catch (Exception ignored) {}

        initiateDisconnect();
        return false;
    }
}
