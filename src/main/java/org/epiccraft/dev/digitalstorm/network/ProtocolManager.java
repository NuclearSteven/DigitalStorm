package org.epiccraft.dev.digitalstorm.network;

import org.epiccraft.dev.digitalstorm.protocol.custom.CustomPacket;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Steven on 2016/5/31.
 */
public class ProtocolManager {

    private NetworkManager networkManager;
    private List<Class<? extends CustomPacket>> customPackets = new LinkedList<>();
    private boolean locked;

    public ProtocolManager(NetworkManager networkManager) {
        this.networkManager = networkManager;
    }

    public void loadCustomPacket(Class packet) {
        if (locked) {
            return;
        }

        for (Class<? extends CustomPacket> customPacket : customPackets) {
            if (packet.equals(customPacket)) {
                return;
            }
        }

        customPackets.add(packet);
    }

    public boolean customPacketLoaded(String fullName) {
        for (Class<? extends CustomPacket> customPacket : customPackets) {
            if (fullName.equals(customPacket.getName())) {
                return true;
            }
        }
        return false;
    }

    public int getCustomPacketHashCode() {
        List<String> classNames = customPackets.stream().map(Class::getName).collect(Collectors.toCollection(LinkedList::new));
        Collections.sort(classNames);
        return classNames.hashCode();
    }

    public void lock() {
        locked = true;
    }

}
