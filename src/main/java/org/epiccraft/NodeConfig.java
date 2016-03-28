package org.epiccraft;

import java.lang.reflect.Field;
import java.net.InetSocketAddress;

/**
 * Project {PROJECT_NAME}
 */
public class NodeConfig {

    /**
     * The remote node's address. Null to be the first server in the network.
     */
    public InetSocketAddress interfaceNodeNetworkAddress;

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
