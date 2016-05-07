package org.epiccraft.dev.digitalstorm.event;

import org.epiccraft.dev.digitalstorm.runtime.network.NetworkManager;

/**
 * Project DigitalStorm
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
