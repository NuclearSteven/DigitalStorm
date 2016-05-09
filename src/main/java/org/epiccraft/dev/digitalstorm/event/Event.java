package org.epiccraft.dev.digitalstorm.event;

/**
 * Project DigitalStorm
 */
public abstract class Event  {

    public Event() {

    }

    @Override
    public String toString() {
        return "Event{type=" +
                this.getClass().getSimpleName() +
                "}";
    }
}
