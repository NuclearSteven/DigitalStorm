package org.epiccraft.dev.webnode;

import org.epiccraft.dev.webnode.event.EventFactory;
import org.epiccraft.dev.webnode.runtime.exception.ConfigInvalidException;
import org.epiccraft.dev.webnode.runtime.network.NetworkManager;

import java.util.logging.Logger;

/**
 * Project WebNode
 */
public class WebNode {

    private static WebNode instance;
    private Logger logger;
    private NodeConfig config;
    private NetworkManager networkManager;

    private EventFactory eventFactory;

    public WebNode(NodeConfig nodeConfig) {
        instance = this;
        config = nodeConfig;
        initialize();
    }

    private void initialize() {
        networkManager = new NetworkManager(this);
        eventFactory = new EventFactory(networkManager);
    }

    public static WebNode create(NodeConfig nodeConfig) throws ConfigInvalidException {
        if (!nodeConfig.checkValid()) {
            throw new ConfigInvalidException();
        }

        return new WebNode(nodeConfig);
    }

    //Getters

    public NodeConfig getConfig() {
        return config;
    }

    public Logger getLogger() {
        if (logger == null) {
            logger = Logger.getLogger("Main");
        }

        return logger;
    }

    public NetworkManager getNetworkManager() {
        return networkManager;
    }

    public static WebNode getInstance() {
        return instance;
    }

    public EventFactory getEventFactory() {
        return eventFactory;
    }

}
