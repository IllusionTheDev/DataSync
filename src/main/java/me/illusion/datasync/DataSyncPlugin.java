package me.illusion.datasync;

import lombok.Getter;
import me.illusion.datasync.config.DatabasesFile;
import me.illusion.datasync.config.SettingsFile;
import me.illusion.datasync.database.DatabaseManager;
import me.illusion.datasync.database.fetching.impl.MySQLFetchingImpl;
import me.illusion.datasync.database.messaging.impl.RedisMessagingImpl;
import me.illusion.datasync.packet.PacketManager;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class DataSyncPlugin extends JavaPlugin {

    private SettingsFile settings;
    private DatabasesFile databaseConfig;

    private DatabaseManager databaseManager;

    private PacketManager packetManager;

    @Override
    public void onEnable() {
        settings = new SettingsFile(this);
        databaseConfig = new DatabasesFile(this);

        databaseManager = new DatabaseManager(this);

        packetManager = new PacketManager();
    }

    private void registerDatabases() {
        databaseManager.registerDatabase(new MySQLFetchingImpl());
        databaseManager.registerDatabase(new RedisMessagingImpl(this));
    }
}
