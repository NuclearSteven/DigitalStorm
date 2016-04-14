package org.epiccraft.dev.webnode.runtime.network.handler;

import org.epiccraft.dev.webnode.protocol.Packet;
import org.epiccraft.dev.webnode.structure.Node;
import org.epiccraft.dev.webnode.structure.channel.Channel;

import java.net.InetSocketAddress;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

/**
 * Project WebNode
 */
public class Interests {

    private List<UUID> nodeUUIDs;
    private List<Node> nodes;
    private List<InetSocketAddress> nodeAddresses;
    private List<Channel> channels;
    private List<Class<? extends Packet>> packets;

    public Interests() {
        nodeUUIDs = new LinkedList<>();
        nodeAddresses = new LinkedList<>();
        nodes = new LinkedList<>();
        channels = new LinkedList<>();
        packets = new LinkedList<>();
    }

    public void addInterest(Object o) {
        if (o instanceof UUID) {
            nodeUUIDs.add((UUID) o);
        } else if (o instanceof Node) {
            nodes.add((Node) o);
        } else if (o instanceof InetSocketAddress) {
            nodeAddresses.add((InetSocketAddress) o);
        } else if (o instanceof Channel) {
            channels.add((Channel) o);
        } else if (o instanceof Class) {
            if (((Class) o).isAssignableFrom(Packet.class)) {
                packets.add((Class<? extends Packet>) o);
            }
        }
    }

    @SuppressWarnings("")
    public void deleteInterest(Object o) {
        try {
            for (UUID nodeUUID : nodeUUIDs) {
                if (nodeUUID.equals(o)) {
                    nodeUUIDs.remove(nodeUUID);
                }
            }
            nodes.remove(o);
            for (InetSocketAddress nodeAddress : nodeAddresses) {
                if (nodeAddress.equals(o)) {
                    nodeAddresses.remove(nodeAddress);
                }
            }
            channels.remove(o);
            packets.remove(o);
        } catch (Exception e) {

        }
    }

    public boolean includeInterest(Object o) {
        try {
            for (UUID nodeUUID : nodeUUIDs) {
                if (nodeUUID.equals(o)) {
                    return true;
                }
            }
            if (nodes.contains(o)) {
                return true;
            }
            for (InetSocketAddress nodeAddress : nodeAddresses) {
                if (nodeAddress.equals(o)) {
                    return true;
                }
            }
            if (channels.contains(o)) {
                return true;
            }
            if (packets.contains(o)) {
                return true;
            }
        } catch (Exception e) {

        }
        return false;
    }

}
