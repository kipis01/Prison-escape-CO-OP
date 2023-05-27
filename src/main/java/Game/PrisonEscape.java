package Game;

import Networking.NetWorker;
import Networking.Packets.DataPacket;

import java.io.*;
import java.net.InetAddress;
import java.util.List;

import java.net.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class PrisonEscape {
    public static void main(String[] args) throws IOException {
        System.out.println("Starting...");

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        NetWorker net;

        if (args.length == 3){
            net = new NetWorker(InetAddress.getByName(args[0]), Integer.parseInt(args[1]), args[2]);
        } else if (args.length == 2){
            net = new NetWorker(Integer.parseInt(args[0]), args[1]);
        } else return;

        net.startNetWorker();

        boolean cont = true;
        while (cont){
            System.out.print("> ");
            String command = reader.readLine();

            switch (command){
                case "status":
                    System.out.println(net.getStatus());
                    break;
                case "send":
                    command = reader.readLine();
                    net.SendPacket(command);
                    break;
                case "ListReceived":
                    ConcurrentLinkedQueue<DataPacket> received = net.getReceivedPackets();
                    for(DataPacket packet = received.poll(); packet != null; packet = received.poll()){
                        System.out.println(packet.data);
                    }
                    break;
                case "stop":
                    cont = false;
                    break;
                default:
                    System.out.println("Invalid command!");
            }
        }

        net.stopNetWorker(true);

        System.out.println("Stopped.");
    }
}
