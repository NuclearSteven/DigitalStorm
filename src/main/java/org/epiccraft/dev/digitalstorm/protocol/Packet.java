package org.epiccraft.dev.digitalstorm.protocol;

import org.epiccraft.dev.digitalstorm.structure.Node;

import java.io.Serializable;

/**
 * Project DigitalStorm
 */
public abstract class Packet implements Serializable {

    public static final int PROTOCOL_VERSION = 1;

    private Node sender;

    public Node getSender() {
        return sender;
    }

    public void setSender(Node sender) {
        this.sender = sender;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "{" +
                "sender=" + (sender == null ? "unidentified" : sender.getUUID()) +
                '}';
    }

}
