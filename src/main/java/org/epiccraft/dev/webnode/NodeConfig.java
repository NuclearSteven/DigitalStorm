package org.epiccraft.dev.webnode;

import java.lang.reflect.Field;
import java.net.InetSocketAddress;

/**
 * Project WebNode
 */
public class NodeConfig {

    /**
     * The remote node's address. Null to be the first server in the network.
     */
    public InetSocketAddress interfaceNodeNetworkAddress;

    /**
     * The local node's address. Null to be the first server in the network.
     */
    public InetSocketAddress localNodeNetworkAddress;

    /**
     * The password for connecting other nodes. Null to be none.
     * This option must be enabled or disabled on all nodes to create a network.
     */
    public String connectionPassword = null;

    /**
     * Node group
     */
    public String nodeGroup = null;

    /**
     * Secured connection.
     * This option must be enabled or disabled on all nodes to create a network.
     */
    public boolean SSL = false;

    public boolean checkValid() {
        for (Field field : getClass().getFields()) {
            if (!field.isAccessible()) {
                continue;
            }
            try {
                if (field.get(this) == null) {
                    return false;
                }
            } catch (IllegalAccessException e) {
                return false;
            }
        }
        return true;
    }

}
