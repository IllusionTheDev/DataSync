package me.illusion.datasync.handler.data;

import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface DataProvider<T> {

    String getIdentifier();

    default CompletableFuture<T> get(Player player) {
        return get(player.getUniqueId());
    }

    default CompletableFuture<T> get(UUID uuid) {
        return CompletableFuture.completedFuture(null);
    }

    CompletableFuture<Void> apply(UUID uuid, Object object);
}
