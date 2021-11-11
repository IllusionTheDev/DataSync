package me.illusion.datasync.provider;

import me.illusion.datasync.handler.data.DataProvider;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class SaturationProvider implements DataProvider<Float> {
    @Override
    public String getIdentifier() {
        return "default-saturation";
    }

    @Override
    public CompletableFuture<Float> get(Player player) {
        return CompletableFuture.supplyAsync(player::getSaturation);
    }

    @Override
    public CompletableFuture<Void> apply(UUID uuid, Object object) {
        Player player = Bukkit.getPlayer(uuid);
        player.setSaturation((Float) object);

        return CompletableFuture.completedFuture(null);
    }
}
