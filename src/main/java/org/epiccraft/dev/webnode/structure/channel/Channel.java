package org.epiccraft.dev.webnode.structure.channel;

import org.epiccraft.dev.webnode.protocol.Packet;
import org.epiccraft.dev.webnode.structure.Node;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * Project WebNode
 */
public class Channel implements Serializable {

    private String name;
    private transient List<Node> joinedNode;

    public Channel(String name) {
        this.name = name;
        joinedNode = new LinkedList<>();
    }

    public void broadcast(Packet packet) {
        for (Node node : joinedNode) {
            node.sendPacket(packet);
        }
    }

    public void broadcast(Packet packet, Class type) {
        for (Node node : joinedNode) {
            if (node.getClass().getName().equals(type.getName())) {
                node.sendPacket(packet);
            }
        }
    }

}
