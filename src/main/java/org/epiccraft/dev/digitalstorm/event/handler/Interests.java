package org.epiccraft.dev.digitalstorm.event.handler;

import org.epiccraft.dev.digitalstorm.event.Event;
import org.epiccraft.dev.digitalstorm.protocol.Packet;
import org.epiccraft.dev.digitalstorm.structure.Node;
import org.epiccraft.dev.digitalstorm.structure.channel.Channel;

import java.net.InetSocketAddress;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

/**
 * Project DigitalStorm
 */
public class Interests {

    private List<UUID> nodeUUIDs;
    private List<Node> nodes;
    private List<InetSocketAddress> nodeAddresses;
    private List<Channel> channels;
    private List<Class<? extends Packet>> packets;
    private List<Class<? extends Event>> events;

    private boolean allInterests = true;

    public Interests() {
        nodeUUIDs = new LinkedList<>();
        nodeAddresses = new LinkedList<>();
        nodes = new LinkedList<>();
        channels = new LinkedList<>();
        packets = new LinkedList<>();
        events = new LinkedList<>();
    }

    public Interests addInterest(Object o) {
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
            if (((Class) o).isAssignableFrom(Event.class)) {
                events.add((Class<? extends Event>) o);
            }
        }

        return this;
    }

    public Interests deleteInterest(Object o) {
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
            events.remove(o);
        } catch (Exception e) {

        }

        return this;
    }

    public boolean includeInterest(Object o) {
        if (allInterests) {
            return true;
        }
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
            if (events.contains(o)) {
                return true;
            }
        } catch (Exception e) {

        }
        return false;
    }

    public boolean isAllInterests() {
        return allInterests;
    }

    public Interests setAllInterests(boolean allInterests) {
        this.allInterests = allInterests;

        return this;
    }

}
