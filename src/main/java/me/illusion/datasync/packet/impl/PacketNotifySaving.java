package me.illusion.datasync.packet.impl;

import lombok.Getter;
import me.illusion.datasync.handler.data.StoredData;
import me.illusion.datasync.packet.Packet;

import java.util.UUID;

@Getter
public class PacketNotifySaving extends Packet {

    private final UUID uuid;
    private final StoredData data;

    public PacketNotifySaving(byte[] bytes) {
        super(bytes);

        uuid = readUUID();
        data = (StoredData) readObject();
    }

    public PacketNotifySaving(UUID uuid, StoredData data) {
        this.uuid = uuid;
        this.data = data;

        writeUUID(uuid);
        writeObject(data);
    }
}