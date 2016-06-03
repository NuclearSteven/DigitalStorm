package org.epiccraft.dev.digitalstorm.network;

import io.netty.channel.socket.SocketChannel;
import org.epiccraft.dev.digitalstorm.event.RawConnetedEvent;
import org.epiccraft.dev.digitalstorm.event.RawDisconnectedEvent;
import org.epiccraft.dev.digitalstorm.protocol.NodeInfomation;
import org.epiccraft.dev.digitalstorm.protocol.Packet;
import org.epiccraft.dev.digitalstorm.runtime.exception.ConnectionException;
import org.epiccraft.dev.digitalstorm.structure.Node;

import java.net.SocketAddress;
import java.util.Map;
import java.util.UUID;

/**
 * Project DigitalStorm
 */
public interface PacketHandler {

    enum NetworkStatus {
        ACTIVE, INACTIVE
    }

    void shutdown(Exception e);

    NetworkStatus getNetworkStatus();

    SocketChannel getSocketChannel();

    class ConnectionAdaptor {

        public static Node newNodeConnected(NetworkManager networkManager, NodeInfomation nodeInfomation, SocketAddress socketAddress) throws ConnectionException {
            for (Map.Entry<UUID, Node> uuidNodeEntry : networkManager.getNodeMap().entrySet()) {
                if (uuidNodeEntry.getKey().equals(nodeInfomation.nodeUUID)) {
                    throw new ConnectionException("Node has already connected.", null);
                }
            }

            Node node = new Node(networkManager, nodeInfomation);
            networkManager.getNodeMap().put(nodeInfomation.nodeUUID, node);

            networkManager.getDigitalStorm().getEventFactory().broadcastEvent(new RawConnetedEvent(socketAddress));

            return node;
        }

        public static RawDisconnectedEvent nodeDisconnected(NetworkManager networkManager, SocketAddress socketAddress) {
            networkManager.getDigitalStorm().getLogger().info("Node Disconnected: " + socketAddress);
            networkManager.getNodeMap().entrySet().stream().filter(uuidNodeEntry -> uuidNodeEntry.getValue().getPacketHandler().getSocketChannel().remoteAddress().equals(socketAddress)).forEach(uuidNodeEntry -> {
                networkManager.getNodeMap().remove(uuidNodeEntry.getKey());
            });

            RawDisconnectedEvent rawDisconnectedEvent = new RawDisconnectedEvent(socketAddress);
            networkManager.getDigitalStorm().getEventFactory().broadcastEvent(rawDisconnectedEvent);
            return rawDisconnectedEvent;
        }

        public static void packetReceived(NetworkManager networkManager, Packet msg, PacketHandler packetHandler) {
            networkManager.getDigitalStorm().getEventFactory().broadcastPacket(msg, packetHandler);
        }

    }

}
