package org.epiccraft.dev.webnode.structure;

import java.util.List;

/**
 * Project WebNode
 */
public class NodeGroup implements NodeUnit {

    public String name;
    public List<Node> nodeList;

    public NodeGroup(String name) {
        this.name = name;
    }

    public Node getNode(long id) {
        for (Node node : nodeList) {
            if (node.id == id) {
                return node;
            }
        }
        return null;
    }

    public static NodeGroup group(Node node) {
        //// TODO: 4/5/2016
        return null;
    }
    
}
