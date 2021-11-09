package me.illusion.datasync.provider;

import me.illusion.datasync.handler.data.DataProvider;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class OperatorProvider implements DataProvider<Boolean> {

    @Override
    public String getIdentifier() {
        return "default-operator";
    }

    @Override
    public CompletableFuture<Boolean> get(Player player) {
        boolean operator = player.isOp();
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletableFuture<Void> apply(UUID uuid, Object object) {
        Player player = Bukkit.getPlayer(uuid);
        player.setOp((Boolean) object);
        return null;
    }

}
