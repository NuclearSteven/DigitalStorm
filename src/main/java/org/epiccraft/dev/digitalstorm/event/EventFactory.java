package org.epiccraft.dev.digitalstorm.event;

import org.epiccraft.dev.digitalstorm.event.handler.NetworkHandler;
import org.epiccraft.dev.digitalstorm.protocol.Packet;
import org.epiccraft.dev.digitalstorm.protocol.channel.ChannelDataPacket;
import org.epiccraft.dev.digitalstorm.runtime.network.NetworkManager;
import org.epiccraft.dev.digitalstorm.runtime.network.PacketHandler;
import org.epiccraft.dev.digitalstorm.runtime.network.server.ServerHandler;
import org.epiccraft.dev.digitalstorm.structure.Node;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Project DigitalStorm
 */
public class EventFactory {

    private NetworkManager networkManager;
    private List<NetworkHandler> networkHandlers;

    public EventFactory(NetworkManager networkManager) {
        this.networkManager = networkManager;
        networkHandlers = new LinkedList<>();
    }

    public void registerHandler(NetworkHandler packetHandler) {
        networkHandlers.add(packetHandler);
    }

    public void broadcastEvent(Event event) {
        networkHandlers.stream().filter(networkHandler -> networkHandler.getInterests().includeInterest(event)).forEach(networkHandler -> {
            networkHandler.onEvent(event);
        });
    }

    public void broadcastPacket(Packet packet, PacketHandler handler) {
        for (NetworkHandler networkHandler : networkHandlers) {
            System.out.println("sethanlder");
            for (Map.Entry<UUID, Node> uuidNodeEntry : networkManager.getNodeMap().entrySet()) {
                if (uuidNodeEntry.getValue().getHandler() == handler) {
                    packet.setSender(uuidNodeEntry.getValue());
                }
            }
            if (networkHandler.getInterests().includeInterest(packet.getClass())) {
                if (packet instanceof ChannelDataPacket) {
                    networkHandler.channelPacketReceived((ChannelDataPacket) packet);
                } else {
                    networkHandler.packetReceived(packet);
                }
            }
        }
    }

    public List<NetworkHandler> getNetworkHandlers() {
        return networkHandlers;
    }

}
