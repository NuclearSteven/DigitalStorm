package org.epiccraft.dev.webnode.structure.channel;

import org.epiccraft.dev.webnode.WebNode;
import org.epiccraft.dev.webnode.protocol.Packet;
import org.epiccraft.dev.webnode.structure.Node;

import java.util.Map;
import java.util.UUID;

/**
 * Project WebNode
 */
public class SystemNetworkingChannel extends Channel {

    public SystemNetworkingChannel() {
        super("SystemNetworkingChannel");
    }

    @Override
    public void broadcast(Packet packet) {
        for (Map.Entry<UUID, Node> uuidNodeEntry : WebNode.getInstance().getNetworkManager().getNodeMap().entrySet()) {
            uuidNodeEntry.getValue().sendPacket(packet);
        }
    }

    @Override
    public void broadcast(Packet packet, Class type) {
        broadcast(packet);
    }

}
