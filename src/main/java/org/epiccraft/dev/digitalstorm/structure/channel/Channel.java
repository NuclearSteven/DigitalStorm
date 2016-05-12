package org.epiccraft.dev.digitalstorm.structure.channel;

import org.epiccraft.dev.digitalstorm.protocol.Packet;
import org.epiccraft.dev.digitalstorm.protocol.channel.ChannelDataPacket;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * Project DigitalStorm
 */
public class Channel implements Serializable {

    private String name;
    private transient List<ChannelMember> joinedNodes;

    public String getName() {
        return name;
    }

    public List<ChannelMember> getJoinedNode() {
        return joinedNodes;
    }

    public Channel(String name) {
        this.name = name;
        joinedNodes = new LinkedList<>();
    }

    public void broadcast(Packet packet) {
        for (ChannelMember node : joinedNodes) {
            ChannelDataPacket channelDataPacket = new ChannelDataPacket();
            channelDataPacket.channel = this;
            channelDataPacket.msg = packet;
            node.sendPacket(channelDataPacket);
        }
    }

    public void broadcast(Packet packet, Class type) {
        joinedNodes.stream().filter(node -> node.getClass().getName().equals(type.getName())).forEach(node -> {
            ChannelDataPacket channelDataPacket = new ChannelDataPacket();
            channelDataPacket.channel = this;
            channelDataPacket.msg = packet;
            node.sendPacket(channelDataPacket);
        });
    }

    public void join(ChannelMember node) {
        if (joinedNodes == null) {
            joinedNodes = new LinkedList<>();
        }
        for (ChannelMember node1 : joinedNodes) {
            if (node1.getId().equals(node)) {
                return;
            }
        }
        joinedNodes.add(node);
    }

}
