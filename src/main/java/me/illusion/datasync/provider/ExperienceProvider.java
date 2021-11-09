package me.illusion.datasync.provider;

import me.illusion.datasync.handler.data.DataProvider;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class ExperienceProvider implements DataProvider<Float> {

    @Override
    public String getIdentifier() {
        return "default-experience";
    }

    @Override
    public CompletableFuture<Float> get(Player player) {
        float experience = player.getExp();
        return CompletableFuture.completedFuture(experience);
    }

    @Override
    public CompletableFuture<Void> apply(UUID uuid, Object object) {
        Player player = Bukkit.getPlayer(uuid);
        player.setExp((Float) object);
        return null;
    }

}
