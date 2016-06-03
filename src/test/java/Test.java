import org.epiccraft.dev.digitalstorm.DigitalStorm;
import org.epiccraft.dev.digitalstorm.NodeConfig;
import org.epiccraft.dev.digitalstorm.event.Event;
import org.epiccraft.dev.digitalstorm.event.RawDisconnectedEvent;
import org.epiccraft.dev.digitalstorm.event.handler.Interests;
import org.epiccraft.dev.digitalstorm.event.handler.NetworkHandler;
import org.epiccraft.dev.digitalstorm.protocol.Packet;
import org.epiccraft.dev.digitalstorm.protocol.system.channel.ChannelDataPacket;
import org.epiccraft.dev.digitalstorm.protocol.system.heartbeat.ACK;
import org.epiccraft.dev.digitalstorm.structure.Node;

import java.net.InetSocketAddress;
import java.util.Map;
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
        type = input.equals("c");
        NodeConfig nodeConfig = new NodeConfig();
        nodeConfig.connectionPassword = "000";
        nodeConfig.localNodeNetworkAddress = new InetSocketAddress("127.0.0.1", 1234);
        nodeConfig.interfaceNodeNetworkAddress = new InetSocketAddress("127.0.0.1", 1234);
        nodeConfig.SSL = true;
        nodeConfig.clientSideTraffic = type;
        nodeConfig.serverSideTraffic = !type;
        nodeConfig.checkValid();
        DigitalStorm digitalStorm = new DigitalStorm(nodeConfig);
        digitalStorm.getEventFactory().registerHandler(this);
        digitalStorm.getNetworkManager().getProtocolManager().loadCustomPacket(TestPacket.class);
        digitalStorm.getNetworkManager().getProtocolManager().loadCustomPacket(TestPacket2.class);
        digitalStorm.initializeNetwork();
        System.out.println(digitalStorm.getNetworkManager().getProtocolManager().getCustomPacketHashCode());
        String s;
        while (true) {
            s = scanner.nextLine();
            if (s == null) {
                continue;
            }
            if (s.equals("break"))
                break;
            for (Map.Entry<UUID, Node> uuidNodeEntry : digitalStorm.getNetworkManager().getNodeMap().entrySet()) {
                System.out.println("send");
                uuidNodeEntry.getValue().sendPacket(new ACK());
            }
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
        if (event instanceof RawDisconnectedEvent) {
            ((RawDisconnectedEvent) event).reconnect();
        }
    }

    @Override
    public Interests getInterests() {
        return new Interests().setAllInterests(true);
    }

}
