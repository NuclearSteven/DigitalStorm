package org.epiccraft.dev.digitalstorm.network;

import org.epiccraft.dev.digitalstorm.protocol.Packet;
import org.epiccraft.dev.digitalstorm.structure.Node;

import java.util.Map;
import java.util.UUID;

/**
 * Created by Steven on 2016/5/31.
 */
public class TrafficManager {

    private NetworkManager networkManager;

    public TrafficManager(NetworkManager networkManager) {
        this.networkManager = networkManager;
    }

    public void broadcast(Packet packet) {
        for (Map.Entry<UUID, Node> uuidNodeEntry : networkManager.getNodeMap().entrySet()) {
            uuidNodeEntry.getValue().sendPacket(packet);
        }
    }

}
