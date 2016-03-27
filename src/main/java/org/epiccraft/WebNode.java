package org.epiccraft;

import org.epiccraft.runtime.exception.ConfigInvalidException;
import org.epiccraft.runtime.network.NodeNetworkManager;

/**
 * Project WebNode
 */
public class WebNode {

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

}
