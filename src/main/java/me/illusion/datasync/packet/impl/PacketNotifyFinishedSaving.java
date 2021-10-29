package me.illusion.datasync.packet.impl;

import lombok.Getter;
import me.illusion.datasync.packet.Packet;

import java.util.UUID;

@Getter
public class PacketNotifyFinishedSaving extends Packet {

    private final UUID uuid;

    public PacketNotifyFinishedSaving(byte[] bytes) {
        super(bytes);

        uuid = readUUID();
    }

    public PacketNotifyFinishedSaving(UUID uuid) {
        this.uuid = uuid;

        writeUUID(uuid);
    }
}
