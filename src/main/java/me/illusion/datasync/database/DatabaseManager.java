package me.illusion.datasync.database;

import me.illusion.datasync.DataSyncPlugin;
import me.illusion.datasync.config.DatabasesFile;
import me.illusion.datasync.database.fetching.FetchingDatabase;
import me.illusion.datasync.database.messaging.MessagingDatabase;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.Map;

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

    public boolean load(DatabasesFile file) {
        ConfigurationSection fetchingSection = file.getFetchingSection();
        ConfigurationSection messagingSection = file.getMessagingSection();

        fetchingDatabase = getDatabase(FetchingDatabase.class, fetchingSection.getString("type"));
        messagingDatabase = getDatabase(MessagingDatabase.class, messagingSection.getString("type"));

        if(fetchingDatabase == null || messagingDatabase == null)
            return false;

        fetchingDatabase.enable(fetchingSection.getConfigurationSection("login"));
        messagingDatabase.enable(messagingSection.getConfigurationSection("login"));

        main.getPacketManager().registerProcessor(messagingDatabase);
        return true;
    }

    private <T extends Database> T getDatabase(Class<T> clazz, String name) {
        Database database = availableDatabases.get(name);

        if(!clazz.isInstance(database)) {
            System.out.println("[DataSync] Database " + name + " is not of type " + clazz.getSimpleName());
            return null;
        }

        return clazz.cast(database);
    }
}
