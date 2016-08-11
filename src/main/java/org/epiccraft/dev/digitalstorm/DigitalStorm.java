package org.epiccraft.dev.digitalstorm;

import org.epiccraft.dev.digitalstorm.event.EventFactory;
import org.epiccraft.dev.digitalstorm.runtime.exception.ConfigInvalidException;
import org.epiccraft.dev.digitalstorm.network.NetworkManager;

import java.util.logging.Handler;
import java.util.logging.Logger;

/**
 * Project DigitalStorm
 */
public class DigitalStorm {

    public final static String VERSION = "1.0";
    private static DigitalStorm instance;
    private Logger logger;
    private NodeConfig config;
    private NetworkManager networkManager;

    private EventFactory eventFactory;

    public DigitalStorm(NodeConfig nodeConfig) {
        instance = this;
        getLogger().info("Loading DigitalStorm[v" + VERSION + "]");
        config = nodeConfig;
        networkManager = new NetworkManager(this);
        eventFactory = new EventFactory(networkManager);
    }

    public void initializeNetwork() {
        networkManager.start();
    }

    public static DigitalStorm create(NodeConfig nodeConfig) throws ConfigInvalidException {
        if (!nodeConfig.checkValid()) {
            throw new ConfigInvalidException();
        }

        return new DigitalStorm(nodeConfig);
    }

    //Getters

    public static DigitalStorm getInstance() {
        return instance;
    }

    public NodeConfig getConfig() {
        return config;
    }

    public Logger getLogger() {
        if (logger == null) {
            logger = Logger.getLogger("Main");
            DigitalFormatter digitalFormatter = new DigitalFormatter();
            for (Handler handler : logger.getParent().getHandlers()) {
                handler.setFormatter(digitalFormatter);
            }
        }

        return logger;
    }

    public NetworkManager getNetworkManager() {
        return networkManager;
    }

    public EventFactory getEventFactory() {
        return eventFactory;
    }

}
