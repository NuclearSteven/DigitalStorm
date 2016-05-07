package org.epiccraft.dev.digitalstorm.structure.channel;

import org.epiccraft.dev.digitalstorm.DigitalStorm;
import org.epiccraft.dev.digitalstorm.protocol.Packet;
import org.epiccraft.dev.digitalstorm.structure.Node;

import java.util.Map;
import java.util.UUID;

/**
 * Project DigitalStorm
 */
public class SystemNetworkingChannel extends Channel {

    public SystemNetworkingChannel() {
        super("SystemNetworkingChannel");
    }

    @Override
    public void broadcast(Packet packet) {
        for (Map.Entry<UUID, Node> uuidNodeEntry : DigitalStorm.getInstance().getNetworkManager().getNodeMap().entrySet()) {
            uuidNodeEntry.getValue().sendPacket(packet);
        }
    }

    @Override
    public void broadcast(Packet packet, Class type) {
        broadcast(packet);
    }

}
