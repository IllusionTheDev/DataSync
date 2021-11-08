package me.illusion.datasync.provider;

import me.illusion.datasync.handler.data.DataProvider;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class GamemodeProvider implements DataProvider<GameMode> {

    @Override
    public String getIdentifier() {
        return "default-gamemode";
    }

    @Override
    public CompletableFuture<GameMode> get(Player player) {
        return CompletableFuture.completedFuture(player.getGameMode());
    }

    @Override
    public CompletableFuture<Void> apply(UUID uuid, Object object) {
        Player player = Bukkit.getPlayer(uuid);
        player.setGameMode((GameMode) object);

        return CompletableFuture.completedFuture(null);
    }
}
