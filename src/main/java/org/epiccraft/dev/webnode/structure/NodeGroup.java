package org.epiccraft.dev.webnode.structure;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Project WebNode
 */
public class NodeGroup {

    public String name;
    public List<Node> nodeList;

    public static ConcurrentHashMap<String, NodeGroup> nodeGroups = new ConcurrentHashMap<>();

    public NodeGroup(String name) {
        this.name = name;
    }

    public Node getNode(UUID id) {
        for (Node node : nodeList) {
            if (node.id == id) {
                return node;
            }
        }
        return null;
    }

    public static NodeGroup getNodeGroup(String name) {
        return nodeGroups.get(name);
    }

    public static NodeGroup group(Node node, String nodeGroup) {
        if (!nodeGroups.containsKey(nodeGroup)) {
            NodeGroup nng = new NodeGroup(nodeGroup);
            nodeGroups.put(nodeGroup, nng);
        }
        nodeGroups.get(nodeGroup).nodeList.add(node);
        return nodeGroups.get(nodeGroup);
    }
    
}
