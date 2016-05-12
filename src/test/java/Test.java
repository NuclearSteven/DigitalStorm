import com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ;
import org.epiccraft.dev.digitalstorm.DigitalStorm;
import org.epiccraft.dev.digitalstorm.NodeConfig;
import org.epiccraft.dev.digitalstorm.event.Event;
import org.epiccraft.dev.digitalstorm.event.handler.Interests;
import org.epiccraft.dev.digitalstorm.event.handler.NetworkHandler;
import org.epiccraft.dev.digitalstorm.protocol.Packet;
import org.epiccraft.dev.digitalstorm.protocol.channel.ChannelDataPacket;
import org.epiccraft.dev.digitalstorm.protocol.heartbeat.ACK;
import org.epiccraft.dev.digitalstorm.runtime.network.client.ClientSocket;
import org.epiccraft.dev.digitalstorm.structure.channel.Channel;
import org.epiccraft.dev.digitalstorm.structure.channel.ChannelMember;
import org.epiccraft.dev.digitalstorm.structure.channel.LocalMachine;

import java.net.InetSocketAddress;
import java.util.Scanner;
import java.util.UUID;

/**
 * Project DigitalStorm
 */
public class Test extends NetworkHandler {

    public static void main(String[] args) throws InterruptedException {
        new Test();
    }

    public Test() throws InterruptedException {
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        boolean type;
        if (input.equals("c")) {
            type = true;
        } else {
            type = false;
        }
        NodeConfig nodeConfig = new NodeConfig();
        nodeConfig.connectionPassword = "000";
        nodeConfig.localNodeNetworkAddress = new InetSocketAddress("127.0.0.1", 1234);
        nodeConfig.interfaceNodeNetworkAddress = new InetSocketAddress("127.0.0.1", 1234);
        nodeConfig.SSL = true;
        nodeConfig.clientSideTraffic = type;
        nodeConfig.serverSideTraffic = !type;
        nodeConfig.checkValid();
        DigitalStorm digitalStorm = new DigitalStorm(nodeConfig);

        String s;
        while (true) {
            s = scanner.nextLine();
            if (s == null) {
                continue;
            }
            if (s.equals("break"))
                break;
            digitalStorm.getNetworkManager().getNodeMap().forEach((uuid, node) -> {
                node.sendPacket(new ACK());
            });
        }
    }

    @Override
    public void packetReceived(Packet packet) {
        System.out.println(packet);
    }

    @Override
    public void channelPacketReceived(ChannelDataPacket channelDataPacket) {
        System.out.println(channelDataPacket);
    }

    @Override
    public void exceptionCaught(Exception e) {
        e.printStackTrace();
    }

    @Override
    public void onEvent(Event event) {
        System.out.println(event);
    }

    @Override
    public Interests getInterests() {
        return new Interests().setAllInterests(true);
    }

}
