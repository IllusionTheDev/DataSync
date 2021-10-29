package me.illusion.datasync.handler;

import me.illusion.datasync.DataSyncPlugin;
import me.illusion.datasync.handler.data.DataProvider;
import me.illusion.datasync.handler.data.StoredData;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class StorageHandler {

    private final DataSyncPlugin main;

    private final Map<String, DataProvider<Object>> providers = new HashMap<>();

    public StorageHandler(DataSyncPlugin main) {
        this.main = main;
    }

    public void registerProvider(DataProvider<Object> provider) {
        String id = provider.getIdentifier();

        providers.put(id, provider);
    }

    public CompletableFuture<Void> applyData(UUID uuid, StoredData data) {
        Set<CompletableFuture<Void>> futures = new HashSet<>();
        for(Map.Entry<String, Object> entry : data.getData().entrySet()) {
            String identifier = entry.getKey();
            Object value = entry.getValue();

            DataProvider<Object> provider = getProvider(identifier);

            if(provider == null)
                continue;

            futures.add(provider.apply(uuid, value));
        }

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
    }

    private DataProvider<Object> getProvider(String name) {
        return providers.get(name);
    }
}
