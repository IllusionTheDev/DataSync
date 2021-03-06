package me.illusion.datasync;

import lombok.Getter;
import me.illusion.datasync.config.DatabasesFile;
import me.illusion.datasync.config.SettingsFile;
import me.illusion.datasync.database.DatabaseManager;
import me.illusion.datasync.database.fetching.impl.MongoDBFetchingImpl;
import me.illusion.datasync.database.fetching.impl.MySQLFetchingImpl;
import me.illusion.datasync.database.messaging.impl.RedisMessagingImpl;
import me.illusion.datasync.handler.PacketCache;
import me.illusion.datasync.handler.StorageHandler;
import me.illusion.datasync.listener.PlayerJoinListener;
import me.illusion.datasync.listener.PlayerLeaveListener;
import me.illusion.datasync.packet.PacketManager;
import me.illusion.datasync.packet.impl.PacketNotifyFinishedSaving;
import me.illusion.datasync.packet.impl.PacketNotifySaving;
import me.illusion.datasync.provider.*;
import org.bukkit.Bukkit;
import org.bukkit.advancement.Advancement;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.CompletableFuture;

@Getter
public class DataSyncPlugin extends JavaPlugin {

    private SettingsFile settings;
    private DatabasesFile databaseConfig;

    private DatabaseManager databaseManager;

    private PacketManager packetManager;
    private PacketCache packetCache;

    private StorageHandler storageHandler;

    @Override
    public void onEnable() {
        registerPackets();

        settings = new SettingsFile(this);
        databaseConfig = new DatabasesFile(this);

        databaseManager = new DatabaseManager(this);

        packetManager = new PacketManager();
        packetCache = new PacketCache(this);

        storageHandler = new StorageHandler(this);


        registerDatabases();
        registerProviders();
        registerListeners();

        // Load databases after 1 tick, ticks start counting after the server fully loads, allows time for plugins to hook into
        Bukkit.getScheduler().runTaskLater(this, this::loadDatabases, 1L);

    }

    private void registerDatabases() {
        databaseManager.registerDatabase(new MySQLFetchingImpl());
        databaseManager.registerDatabase(new MongoDBFetchingImpl());
        databaseManager.registerDatabase(new RedisMessagingImpl(this));
    }

    private void registerProviders() {
        storageHandler.registerProvider(new AttributesProvider());
        storageHandler.registerProvider(new EnderchestProvider());
        storageHandler.registerProvider(new ExperienceProvider());
        storageHandler.registerProvider(new FoodProvider());
        storageHandler.registerProvider(new GamemodeProvider());
        storageHandler.registerProvider(new HealthProvider());
        storageHandler.registerProvider(new InventoryProvider());
        storageHandler.registerProvider(new LevelProvider());
        storageHandler.registerProvider(new LocationProvider());
        storageHandler.registerProvider(new OperatorProvider());
        storageHandler.registerProvider(new PotionProvider());
        storageHandler.registerProvider(new SaturationProvider());

        settings.allowSave();

    }

    private void registerPackets() {
        PacketManager.registerPacket(0x01, PacketNotifySaving.class);
        PacketManager.registerPacket(0x02, PacketNotifyFinishedSaving.class);
    }

    private void registerListeners() {
        PluginManager pluginManager = Bukkit.getPluginManager();

        pluginManager.registerEvents(new PlayerJoinListener(this), this);
        pluginManager.registerEvents(new PlayerLeaveListener(this), this);
    }

    private void loadDatabases() {
        CompletableFuture<Boolean> success = databaseManager.load(databaseConfig);

        success.thenAccept(loaded -> {
            if (!loaded) {
                getLogger().warning("Failed to load databases!"); // made with copilot so don't bother me for this
                setEnabled(false);
            }
        });
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender.isOp())
            wipeFetching();
        return true;
    }

    private void wipeFetching() {
        databaseManager.wipeFetching();
    }
}
