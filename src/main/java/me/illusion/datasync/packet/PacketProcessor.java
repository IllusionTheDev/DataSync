package me.illusion.datasync.packet;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public interface PacketProcessor {

    CompletableFuture<Void> send(Packet packet);
    void addCallback(Consumer<byte[]> receivedPacket);


}
