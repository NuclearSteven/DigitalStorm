package org.epiccraft.dev.digitalstorm;

import java.lang.reflect.Field;
import java.net.InetSocketAddress;

/**
 * Project DigitalStorm
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
     * Try to reconnect the server.
     */
    public boolean autoRetry = true;

    /**
     * Secured connection.
     * This option must be enabled or disabled on all nodes to create a network.
     */
    public boolean SSL = false;

    /**
     * Type of this server
     */
    public String type = "Default";

    /**
     * DEBUG & Advanced Settings
     */
    public boolean clientSideTraffic = true;
    public boolean serverSideTraffic = true;

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
