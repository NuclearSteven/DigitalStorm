package org.epiccraft.dev.digitalstorm.event;

import org.epiccraft.dev.digitalstorm.event.handler.NetworkHandler;
import org.epiccraft.dev.digitalstorm.network.NetworkManager;
import org.epiccraft.dev.digitalstorm.protocol.Packet;
import org.epiccraft.dev.digitalstorm.protocol.system.channel.ChannelDataPacket;
import org.epiccraft.dev.digitalstorm.structure.Node;

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

    public void registerHandler(NetworkHandler packetHandler) {
        networkHandlers.add(packetHandler);
    }

    public void broadcastEvent(Event event) {
        networkHandlers.stream().filter(networkHandler -> networkHandler.getInterests().includeInterest(event)).forEach(networkHandler -> networkHandler.onEvent(event));
    }

    public void broadcastPacket(Packet packet, Node node) {
        for (NetworkHandler networkHandler : networkHandlers) {
            packet.setSender(node);
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
