package me.illusion.datasync.packet.impl;

import lombok.Getter;
import me.illusion.datasync.packet.Packet;

import java.util.UUID;

@Getter
public class PacketNotifySaving extends Packet {

    private final UUID uuid;

    public PacketNotifySaving(byte[] bytes) {
        super(bytes);

        uuid = readUUID();
    }

    public PacketNotifySaving(UUID uuid) {
        this.uuid = uuid;

        writeUUID(uuid);
    }
}