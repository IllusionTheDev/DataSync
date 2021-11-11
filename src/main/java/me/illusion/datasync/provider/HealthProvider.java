package me.illusion.datasync.provider;

import me.illusion.datasync.handler.data.DataProvider;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class HealthProvider implements DataProvider<Double> {

    @Override
    public String getIdentifier() {
        return "default-health";
    }

    @Override
    public CompletableFuture<Double> get(Player player) {
        return CompletableFuture.supplyAsync(player::getHealth);
    }

    @Override
    public CompletableFuture<Void> apply(UUID uuid, Object object) {
        Player player = Bukkit.getPlayer(uuid);
        player.setHealth((Double) object);

        return CompletableFuture.completedFuture(null);
    }
}
