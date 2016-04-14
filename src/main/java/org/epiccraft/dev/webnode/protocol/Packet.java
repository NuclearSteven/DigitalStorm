package org.epiccraft.dev.webnode.protocol;

import org.epiccraft.dev.webnode.structure.Node;

import java.io.Serializable;

/**
 * Project WebNode
 */
public abstract class Packet implements Serializable {

    private Node sender;

    public Node getSender() {
        return sender;
    }

    public void setSender(Node sender) {
        this.sender = sender;
    }
}
