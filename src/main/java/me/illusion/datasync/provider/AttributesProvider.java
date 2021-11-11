package me.illusion.datasync.provider;

import me.illusion.datasync.handler.data.DataProvider;
import me.illusion.datasync.provider.serializable.SerializedAttributeList;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class AttributesProvider implements DataProvider<SerializedAttributeList> {

    @Override
    public String getIdentifier() {
        return "default-attribute";
    }

    @Override
    public CompletableFuture<SerializedAttributeList> get(Player player) {
        SerializedAttributeList list = new SerializedAttributeList();
        list.save(player);

        return CompletableFuture.supplyAsync(() -> list);
    }

    @Override
    public CompletableFuture<Void> apply(UUID uuid, Object object) {
        ((SerializedAttributeList) object).apply(Bukkit.getPlayer(uuid));

        return CompletableFuture.completedFuture(null);
    }

}
