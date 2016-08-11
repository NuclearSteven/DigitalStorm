package org.epiccraft.dev.digitalstorm.network;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.SocketChannel;
import org.epiccraft.dev.digitalstorm.event.RawConnetedEvent;
import org.epiccraft.dev.digitalstorm.event.RawDisconnectedEvent;
import org.epiccraft.dev.digitalstorm.protocol.NodeInformation;
import org.epiccraft.dev.digitalstorm.protocol.Packet;
import org.epiccraft.dev.digitalstorm.protocol.system.channel.ChannelDataPacket;
import org.epiccraft.dev.digitalstorm.protocol.system.channel.ChannelPacket;
import org.epiccraft.dev.digitalstorm.protocol.system.channel.JoinChannelPacket;
import org.epiccraft.dev.digitalstorm.protocol.system.channel.LeaveChannelPacket;
import org.epiccraft.dev.digitalstorm.protocol.system.heartbeat.PING;
import org.epiccraft.dev.digitalstorm.protocol.system.heartbeat.PONG;
import org.epiccraft.dev.digitalstorm.runtime.exception.ConnectionException;
import org.epiccraft.dev.digitalstorm.structure.Channel;
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

        public static void handleUniversal(NetworkManager networkManager, Packet msg, ChannelHandlerContext ctx, Node node) {
            if (msg instanceof PING) {
                ctx.write(new PONG());
            } else if (msg instanceof ChannelPacket) {
                if (msg instanceof JoinChannelPacket) {
                    Channel.join(((JoinChannelPacket) msg).channelIDs, node);
                } else if (msg instanceof LeaveChannelPacket) {
                    Channel.leave(((LeaveChannelPacket) msg).channelIDs, node);
                } else if (msg instanceof ChannelDataPacket) {
                    Channel channel;
                    if ((channel = Channel.getChannel(((ChannelDataPacket) msg).channelID)) != null && Channel.getLocalChannelList().contains(channel.getChannelID())) {
                        channelPacketReceived(networkManager, msg, node, channel);
                    }
                }
            } else {
                packetReceived(networkManager, msg, node);
            }
        }

        private static void channelPacketReceived(NetworkManager networkManager, Packet msg, Node node, Channel c) {
            networkManager.getDigitalStorm().getEventFactory().broadcastChannelPacket(msg, node, c);
        }

        public static Node newNodeConnected(NetworkManager networkManager, NodeInformation nodeInformation, SocketAddress socketAddress) throws ConnectionException {
            for (Map.Entry<UUID, Node> uuidNodeEntry : networkManager.getNodeMap().entrySet()) {
                if (uuidNodeEntry.getKey().equals(nodeInformation.nodeUUID)) {
                    throw new ConnectionException("Node has already connected.", null);
                }
            }

            Node node = new Node(networkManager, nodeInformation);
            networkManager.getNodeMap().put(nodeInformation.nodeUUID, node);

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

        public static void packetReceived(NetworkManager networkManager, Packet msg, Node node) {
            networkManager.getDigitalStorm().getEventFactory().broadcastPacket(msg, node);
        }

    }

}
