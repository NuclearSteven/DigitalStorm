package org.epiccraft.dev.webnode.runtime.network.modules;

import org.epiccraft.dev.webnode.runtime.network.NetworkManager;
import org.epiccraft.dev.webnode.runtime.network.client.ClientSocket;

public class AutoReconnectModule extends Module
{

    @Override
    public void onEnabled(NetworkManager networkManager) {
        for (ClientSocket clientSocket : networkManager.getClientSockets()) {
            try {
                clientSocket.initConnection().addListener(connectionFeature -> {
                    if (!connectionFeature.isSuccess()) {
                        clientSocket.initConnection();
                    }
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDisabled(NetworkManager networkManager) {

    }

}
