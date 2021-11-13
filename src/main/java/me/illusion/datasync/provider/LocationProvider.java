package me.illusion.datasync.provider;

import me.illusion.datasync.handler.data.DataProvider;
import me.illusion.datasync.provider.serializable.SerializedLocation;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class LocationProvider implements DataProvider<SerializedLocation> {
    @Override
    public String getIdentifier() {
        return "default-location";
    }

    @Override
    public CompletableFuture<SerializedLocation> get(Player player) {
        SerializedLocation location = new SerializedLocation();
        location.getLocation(player);

        return CompletableFuture.supplyAsync(() -> location);
    }

    @Override
    public CompletableFuture<Void> apply(UUID uuid, Object object) {

        SerializedLocation location = new SerializedLocation();
        Player player = Bukkit.getPlayer(uuid);
        assert player != null;
        player.teleport(location.update((Location) player));
        return null;
    }
}
