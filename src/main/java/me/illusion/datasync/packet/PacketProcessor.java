package me.illusion.datasync.packet;

import java.util.concurrent.CompletableFuture;

public interface PacketProcessor {

    CompletableFuture<Void> send(Packet packet);
}
