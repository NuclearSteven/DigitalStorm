package org.epiccraft.dev.webnode.event;

import org.epiccraft.dev.webnode.runtime.network.NetworkManager;

/**
 * Project WebNode
 */
public class EventFactory {

    private NetworkManager networkManager;

    public EventFactory(NetworkManager networkManager) {
        this.networkManager = networkManager;
    }

    public void broadcastEvent(Event event) {
        networkManager.getNetworkHandlers().stream().filter(networkHandler -> networkHandler.getInterests().includeInterest(event)).forEach(networkHandler -> {
            networkHandler.onEvent(event);
        });
    }

}
