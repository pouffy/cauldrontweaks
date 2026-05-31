package io.github.pouffy.cauldrontweaks.common.event;

import io.github.pouffy.cauldrontweaks.common.block.CauldronBlockEntity;
import net.neoforged.bus.api.Event;

public abstract class CauldronTickEvent extends Event {
    private final CauldronBlockEntity cauldron;

    public CauldronTickEvent(CauldronBlockEntity cauldron) {
        this.cauldron = cauldron;
    }

    public CauldronBlockEntity getCauldron() {
        return cauldron;
    }

    public static class Server extends CauldronTickEvent {

        public Server(CauldronBlockEntity cauldron) {
            super(cauldron);
        }
    }

    public static class Client extends CauldronTickEvent {

        public Client(CauldronBlockEntity cauldron) {
            super(cauldron);
        }
    }
}
