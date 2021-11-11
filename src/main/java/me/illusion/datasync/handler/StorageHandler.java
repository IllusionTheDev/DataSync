package me.illusion.datasync.handler;

import me.illusion.datasync.DataSyncPlugin;
import me.illusion.datasync.handler.data.DataProvider;
import me.illusion.datasync.handler.data.StoredData;
import me.illusion.datasync.packet.impl.PacketNotifyFinishedSaving;
import me.illusion.datasync.packet.impl.PacketNotifySaving;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;

public class StorageHandler {

    private final DataSyncPlugin main;

    private final Map<UUID, StoredData> data = new HashMap<>();

    private final Map<String, DataProvider<?>> providers = new HashMap<>();
    private final Set<String> invalidProviders = new HashSet<>();

    public StorageHandler(DataSyncPlugin main) {
        this.main = main;
    }

    public void registerProvider(DataProvider<?> provider) {
        String id = provider.getIdentifier();

        if(main.getSettings().shouldEnable(id))
            providers.put(id, provider);
    }

    public CompletableFuture<Void> load(UUID uuid) {
        return main.getPacketCache()
                .query(uuid)
                .thenAccept(data -> {
                    synchronized (this.data) {
                        if(data == null) {
                            this.data.put(uuid, createNewData(uuid));
                            return;
                        }

                        this.data.put(uuid, data);
                        applyData(uuid, data);
                    }
                });
    }

    private void applyData(UUID uuid, StoredData data) {
        Set<CompletableFuture<Void>> futures = new HashSet<>();
        for (Map.Entry<String, Object> entry : data.getData().entrySet()) {
            String identifier = entry.getKey();
            Object value = entry.getValue();

            DataProvider<?> provider = getProvider(identifier);

            if (provider == null) {
                if (!invalidProviders.contains(identifier)) {
                    invalidProviders.add(identifier);
                    main.getLogger().warning("Invalid DataProvider: " + identifier + " (perhaps not loaded?)");
                }

                continue;
            }

            futures.add(provider.apply(uuid, value));
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
    }

    public CompletableFuture<Void> save(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);

        return CompletableFuture.runAsync(() -> {
            synchronized (data) {
                StoredData storedData = data.get(uuid);

                if (storedData == null)
                    return;

                // --- SECTION START - Saving Data ---
                Set<CompletableFuture<Void>> futures = new HashSet<>(); // Makes a collection of futures

                for (Map.Entry<String, Object> entry : storedData.getData().entrySet()) { // Loops through all data, and their providers
                    String identifier = entry.getKey(); // Obtains provider identifier

                    DataProvider<?> provider = getProvider(identifier); // Obtain provider instance

                    if (provider == null) { // Validate provider
                        if (!invalidProviders.contains(identifier)) {
                            invalidProviders.add(identifier);
                            main.getLogger().warning("Invalid DataProvider: " + identifier + " (perhaps not loaded?)");
                        }

                        continue;
                    }


                    // Wait for provider to eventually return data
                    CompletableFuture<Void> future = provider.get(player)
                            .thenAccept(currentValue -> { // Which is then updated internally
                                storedData.getData().put(identifier, currentValue);
                            });

                    futures.add(future);
                }

                CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join(); // Wait for all futures to finish

                CountDownLatch latch = new CountDownLatch(1); // Create a latch


                // --- SECTION END - Saving Data ---

                // --- SECTION START - Updating all caches ---
                PacketNotifySaving packet = new PacketNotifySaving(uuid, storedData);
                main.getPacketManager().send(packet);
                // --- SECTION END - Updating all caches ---

                // --- SECTION START - Saving Data ---

                main.getDatabaseManager().getFetchingDatabase().store(uuid, storedData)
                        .thenRun(() -> {
                            PacketNotifyFinishedSaving packet2 = new PacketNotifyFinishedSaving(uuid);
                            main.getPacketManager().send(packet2)
                                    .thenRun(latch::countDown);
                        }); // When the database is done, count down the latch

                try {
                    latch.await(); // Wait for the latch to count down
                } catch (InterruptedException ignored) {

                }


            }
        }).exceptionally(throwable -> {
            main.getLogger().warning("Failed to save data for " + uuid.toString() + ": " + throwable.getMessage());
            throwable.printStackTrace();
            return null;
        });
    }

    public void quit(UUID uuid) {
        save(uuid)
                .thenRun(() -> {
                    synchronized (data) {
                        data.remove(uuid);
                    }
                });
    }

    public StoredData createNewData(UUID uuid) {
        //UUID uuid = player.getUniqueId();

        Map<String, Object> map = new HashMap<>();
        try {
            for(Map.Entry<String, DataProvider<?>> entry : providers.entrySet()) {
                map.put(entry.getKey(), entry.getValue().get(Bukkit.getPlayer(uuid)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }



        return new StoredData(uuid, map);
    }

    private DataProvider<?> getProvider(String name) {
        return providers.get(name);
    }
}
