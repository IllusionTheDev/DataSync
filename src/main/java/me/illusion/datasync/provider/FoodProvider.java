package me.illusion.datasync.provider;

import me.illusion.datasync.handler.data.DataProvider;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class FoodProvider implements DataProvider<Integer> {
    @Override
    public String getIdentifier() {
        return "default-food";
    }

    @Override
    public CompletableFuture<Integer> get(Player player) {
        return CompletableFuture.supplyAsync(player::getFoodLevel);
    }

    @Override
    public CompletableFuture<Void> apply(UUID uuid, Object object) {
        Player player = Bukkit.getPlayer(uuid);
        player.setFoodLevel((Integer) object);
        return CompletableFuture.completedFuture(null);
    }
}
