package me.illusion.datasync.provider;

import me.illusion.datasync.handler.data.DataProvider;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class HealthProvider implements DataProvider<Double> {

    @Override
    public String getIdentifier() {
        return "DataSync-DefaultHealth";
    }

    @Override
    public CompletableFuture<Double> get(Player player) {
        return CompletableFuture.completedFuture(player.getHealth());
    }

    @Override
    public CompletableFuture<Void> apply(UUID uuid, Double object) {
        Player player = Bukkit.getPlayer(uuid);
        player.setHealth(object);

        return CompletableFuture.completedFuture(null);
    }
}
