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
    private transient List<Node> joinedNodes;

    public String getName() {
        return name;
    }

    public List<Node> getJoinedNode() {
        return joinedNodes;
    }

    public Channel(String name) {
        this.name = name;
        joinedNodes = new LinkedList<>();
    }

    public void broadcast(Packet packet) {
        for (Node node : joinedNodes) {
            node.sendPacket(packet);
        }
    }

    public void broadcast(Packet packet, Class type) {
        joinedNodes.stream().filter(node -> node.getClass().getName().equals(type.getName())).forEach(node -> {
            node.sendPacket(packet);
        });
    }

    public void join(Node node) {
        for (Node node1 : joinedNodes) {
            if (node1.getId().equals(node)) {
                return;
            }
        }
        joinedNodes.add(node);
    }

}
