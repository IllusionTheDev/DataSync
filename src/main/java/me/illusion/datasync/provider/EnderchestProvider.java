package me.illusion.datasync.provider;

import me.illusion.datasync.handler.data.DataProvider;
import me.illusion.datasync.provider.serializable.SerializedItemStackArray;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class EnderchestProvider implements DataProvider<SerializedItemStackArray> {
    @Override
    public String getIdentifier() {
        return "default-enderchest";
    }

    @Override
    public CompletableFuture<SerializedItemStackArray> get(Player player) {
        Inventory enderchest = player.getEnderChest();
        SerializedItemStackArray array = new SerializedItemStackArray();
        array.updateArray(enderchest.getStorageContents());

        return CompletableFuture.completedFuture(array);
    }

    @Override
    public CompletableFuture<Void> apply(UUID uuid, Object object) {
        SerializedItemStackArray array = (SerializedItemStackArray) object;

        Player player = Bukkit.getPlayer(uuid);
        Inventory enderchest = player.getEnderChest();

        enderchest.setStorageContents(array.getArray());


        return CompletableFuture.completedFuture(null);
    }
}
