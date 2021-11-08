package me.illusion.datasync.provider;

import me.illusion.datasync.handler.data.DataProvider;
import me.illusion.datasync.provider.serializable.SerializedPotionList;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class PotionProvider implements DataProvider<SerializedPotionList> {
    @Override
    public String getIdentifier() {
        return "default-potion";
    }

    @Override
    public CompletableFuture<SerializedPotionList> get(Player player) {
        SerializedPotionList list = new SerializedPotionList();
        list.serialize(player);

        return CompletableFuture.completedFuture(list);
    }

    @Override
    public CompletableFuture<Void> apply(UUID uuid, Object object) {
        ((SerializedPotionList) object).apply(Bukkit.getPlayer(uuid));

        return CompletableFuture.completedFuture(null);
    }
}
