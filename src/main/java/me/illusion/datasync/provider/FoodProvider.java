package me.illusion.datasync.provider;

import me.illusion.datasync.handler.data.DataProvider;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class FoodProvider implements DataProvider<Integer> {
    @Override
    public String getIdentifier() {
        return "DataSync-DefaultFood";
    }

    @Override
    public CompletableFuture<Integer> get(Player player) {
        return CompletableFuture.completedFuture(player.getFoodLevel());
    }

    @Override
    public CompletableFuture<Void> apply(UUID uuid, Integer object) {
        Player player = Bukkit.getPlayer(uuid);
        player.setFoodLevel(object);
        return CompletableFuture.completedFuture(null);
    }
}
