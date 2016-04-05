package org.epiccraft.dev.webnode.structure;

import java.util.List;

/**
 * Project WebNode
 */
public class NodeGroup implements NodeUnit {

    public String name;
    public List<Node> nodeList;

    public Node getNode(long id) {
        for (Node node : nodeList) {
            if (node.id == id) {
                return node;
            }
        }
        return null;
    }

}
