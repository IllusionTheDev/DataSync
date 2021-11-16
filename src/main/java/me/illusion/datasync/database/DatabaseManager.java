package me.illusion.datasync.database;

import lombok.Getter;
import me.illusion.datasync.DataSyncPlugin;
import me.illusion.datasync.config.DatabasesFile;
import me.illusion.datasync.database.fetching.FetchingDatabase;
import me.illusion.datasync.database.messaging.MessagingDatabase;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;

@Getter
public class DatabaseManager {

    private FetchingDatabase fetchingDatabase;
    private MessagingDatabase messagingDatabase;

    private final Map<String, Database> availableDatabases = new HashMap<>();

    private final DataSyncPlugin main;

    public DatabaseManager(DataSyncPlugin main) {
        this.main = main;
    }

    public void registerDatabase(Database database) {
        availableDatabases.put(database.getName(), database);
    }

    public CompletableFuture<Boolean> load(DatabasesFile file) {
        ConfigurationSection fetchingSection = file.getFetchingSection();
        ConfigurationSection messagingSection = file.getMessagingSection();

        fetchingDatabase = getDatabase(FetchingDatabase.class, fetchingSection.getString("type"));
        messagingDatabase = getDatabase(MessagingDatabase.class, messagingSection.getString("type"));

        if(fetchingDatabase == null || messagingDatabase == null)
            return CompletableFuture.completedFuture(false);

        String group = file.getConfiguration().getString("group");

        CompletableFuture<Boolean> fetching = fetchingDatabase.enable(fetchingSection.getConfigurationSection("login"), group);
        CompletableFuture<Boolean> messaging = messagingDatabase.enable(messagingSection.getConfigurationSection("login"), group);

        messagingDatabase.addCallback(bytes -> main.getPacketManager().read(bytes));
        main.getPacketManager().registerProcessor(messagingDatabase);

        return CompletableFuture.supplyAsync(() -> {
            CountDownLatch latch = new CountDownLatch(2);
            final boolean[] successValues = {false, false};

            fetching.thenAccept(success -> {
                successValues[0] = true;
                latch.countDown();
            });

            messaging.thenAccept(success -> {
                successValues[1] = true;
                latch.countDown();
            });

            try {
                latch.await();
            }
            catch (InterruptedException ignored) {

            }

            return successValues[0] && successValues[1];
        });
    }

    private <T extends Database> T getDatabase(Class<T> clazz, String name) {
        Database database = availableDatabases.get(name);

        if(database == null)
            return null;

        if(!clazz.isInstance(database)) {
            System.out.println("[DataSync] Database " + name + " is not of type " + clazz.getSimpleName());
            return null;
        }

        return clazz.cast(database);
    }

    public void wipeFetching() {
        fetchingDatabase.wipe().thenRun(() -> {
            System.out.println("[DataSync] Fetching database wiped");
        });
    }
}
