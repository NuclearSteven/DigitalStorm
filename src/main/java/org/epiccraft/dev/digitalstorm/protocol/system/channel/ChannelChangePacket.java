package org.epiccraft.dev.digitalstorm.protocol.system.channel;

import java.util.LinkedList;
import java.util.List;

/**
 * Project DigitalStorm
 */
public class ChannelChangePacket extends ChannelPacket {

    public List<Action> list = new LinkedList<>();

    public static class Action {

        public enum ActionType {
            JOIN, QUIT
        }

        public ActionType actionType;
//        public Channel targetChannel;

    }

}
