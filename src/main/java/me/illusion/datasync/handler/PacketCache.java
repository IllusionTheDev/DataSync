package me.illusion.datasync.handler;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import me.illusion.datasync.DataSyncPlugin;
import me.illusion.datasync.handler.data.StoredData;
import me.illusion.datasync.packet.PacketHandler;
import me.illusion.datasync.packet.impl.PacketNotifyFinishedSaving;
import me.illusion.datasync.packet.impl.PacketNotifySaving;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class PacketCache {

    private final DataSyncPlugin main;
    private Cache<UUID, StoredData> cache = null;

    private final Set<UUID> unavailableIds = new HashSet<>();

    public PacketCache(DataSyncPlugin main) {
        this.main = main;
        load(main.getSettings().getCacheSection());

        main.getPacketManager()
                .subscribe(PacketNotifySaving.class, new PacketHandler<PacketNotifySaving>() {
                    @Override
                    public void onReceive(PacketNotifySaving packet) {
                        save(packet.getUuid(), packet.getData());
                        unavailableIds.add(packet.getUuid());
                    }
                });
    }

    private void load(ConfigurationSection cacheSection) {
        if (!cacheSection.contains("enabled"))
            return;

        TimeUnit timeUnit = TimeUnit.valueOf(cacheSection.getString("time.unit", "seconds").toUpperCase(Locale.ROOT));
        long time = cacheSection.getLong("time.time");

        cache = CacheBuilder.newBuilder()
                .expireAfterWrite(time, timeUnit)
                .build();
    }

    public CompletableFuture<StoredData> query(UUID uuid) {
        if (cache != null) {
            StoredData data = cache.getIfPresent(uuid);

            if (data != null)
                return CompletableFuture.completedFuture(data);
        }

        return CompletableFuture.supplyAsync(() -> {
            if (unavailableIds.contains(uuid))

                main.getPacketManager().await(PacketNotifyFinishedSaving.class, packet -> packet.getUuid().equals(uuid));


            return main.getDatabaseManager().getFetchingDatabase().fetch(uuid)
                    .thenApply(data -> {
                        if (cache != null)
                            cache.put(uuid, data);

                        return data;
                    }).join();

        });

    }

    public void save(UUID uuid, StoredData data) {
        cache.put(uuid, data);
    }
}
