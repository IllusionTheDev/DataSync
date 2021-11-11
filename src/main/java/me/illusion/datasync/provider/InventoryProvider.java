package me.illusion.datasync.provider;

import me.illusion.datasync.handler.data.DataProvider;
import me.illusion.datasync.provider.serializable.SerializedItemStackArray;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class InventoryProvider implements DataProvider<SerializedItemStackArray> {
    @Override
    public String getIdentifier() {
        return "default-inventory";
    }

    @Override
    public CompletableFuture<SerializedItemStackArray> get(Player player) {
        PlayerInventory inventory = player.getInventory();
        SerializedItemStackArray array = new SerializedItemStackArray();
        array.updateArray(inventory.getStorageContents());

        return CompletableFuture.supplyAsync(() -> array);
    }

    @Override
    public CompletableFuture<Void> apply(UUID uuid, Object object) {
        SerializedItemStackArray array = (SerializedItemStackArray) object;

        Player player = Bukkit.getPlayer(uuid);
        PlayerInventory inventory = player.getInventory();

        inventory.setStorageContents(array.getArray());


        return CompletableFuture.completedFuture(null);
    }
}
