package org.epiccraft.dev.digitalstorm.runtime.exception;

import org.epiccraft.dev.digitalstorm.structure.Node;

/**
 * Project DigitalStorm
 */
public class ConnectionException extends Exception {

    private Node node;

    public ConnectionException(String message, Node node) {
        super(message);
        this.node = node;
    }

    @Override
    public String toString() {
        return node == null ? "" : node + ": " + getLocalizedMessage();
    }

}
