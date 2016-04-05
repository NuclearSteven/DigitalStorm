package org.epiccraft.dev.webnode;

import org.epiccraft.dev.webnode.runtime.exception.ConfigInvalidException;
import org.epiccraft.dev.webnode.runtime.network.NodeNetworkManager;

import java.util.logging.Logger;

/**
 * Project WebNode
 */
public class WebNode {

    private Logger logger;
    private NodeConfig config;
    private NodeNetworkManager networkManager;

    public WebNode(NodeConfig nodeConfig) {
        config = nodeConfig;
        initialize();
    }

    private void initialize() {
        networkManager = new NodeNetworkManager(this);
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

    public NodeNetworkManager getNetworkManager() {
        return networkManager;
    }

}
