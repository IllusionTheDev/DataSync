package me.illusion.datasync.provider;

import me.illusion.datasync.handler.data.DataProvider;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class LevelProvider implements DataProvider<Integer> {

    @Override
    public String getIdentifier() {
        return "default-level";
    }

    @Override
    public CompletableFuture<Integer> get(Player player) {
        int level = player.getLevel();
        return CompletableFuture.completedFuture(level);
    }

    @Override
    public CompletableFuture<Void> apply(UUID uuid, Integer object) {
        Player player = Bukkit.getPlayer(uuid);
        player.setLevel(object);
        return null;
    }

}
