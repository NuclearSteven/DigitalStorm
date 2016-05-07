package org.epiccraft.dev.digitalstorm.event;

import org.epiccraft.dev.digitalstorm.event.handler.NetworkHandler;
import org.epiccraft.dev.digitalstorm.protocol.Packet;
import org.epiccraft.dev.digitalstorm.protocol.channel.ChannelDataPacket;
import org.epiccraft.dev.digitalstorm.runtime.network.NetworkManager;
import org.epiccraft.dev.digitalstorm.runtime.network.server.ServerHandler;

import java.util.LinkedList;
import java.util.List;

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

    public void registerPacketHandler(NetworkHandler packetHandler) {
        networkHandlers.add(packetHandler);
    }

    public void broadcastEvent(Event event) {
        networkHandlers.stream().filter(networkHandler -> networkHandler.getInterests().includeInterest(event)).forEach(networkHandler -> {
            networkHandler.onEvent(event);
        });
    }

    public void broadcastPacket(Packet packet, ServerHandler serverHandler) {
        for (NetworkHandler networkHandler : networkHandlers) {
            networkManager.getNodeMap().entrySet().stream().filter(uuidNodeEntry -> uuidNodeEntry.getValue().getHandler().equals(serverHandler)).forEach(uuidNodeEntry -> {
                packet.setSender(uuidNodeEntry.getValue());
            });

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
