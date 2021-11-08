package me.illusion.datasync.database.fetching;

import me.illusion.datasync.database.Database;
import me.illusion.datasync.handler.data.StoredData;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface FetchingDatabase extends Database {

    CompletableFuture<StoredData> fetch(UUID uuid);

    CompletableFuture<Void> store(UUID uuid, StoredData data);

}
