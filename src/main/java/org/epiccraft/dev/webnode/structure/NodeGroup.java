package org.epiccraft.dev.webnode.structure;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Project WebNode
 */
public class NodeGroup implements NodeUnit {

    public String name;
    public List<Node> nodeList;

    public static ConcurrentHashMap<String, NodeGroup> nodeGroups = new ConcurrentHashMap<String, NodeGroup>();

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

    public static NodeGroup group(Node node, String nodeGroup) {
        if (nodeGroups.containsKey(nodeGroup)) {
            nodeGroups.get(nodeGroup).nodeList.add(node);
        } else {
            NodeGroup nng = new NodeGroup(nodeGroup);
            nng.nodeList.add(node);
        }
        return null;
    }
    
}
