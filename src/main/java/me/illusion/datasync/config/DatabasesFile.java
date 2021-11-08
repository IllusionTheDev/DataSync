package me.illusion.datasync.config;

import me.illusion.utilities.storage.YMLBase;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

public class DatabasesFile extends YMLBase {

    public DatabasesFile(JavaPlugin plugin) {
        super(plugin, "databases.yml");
    }

    public ConfigurationSection getFetchingSection() {
        return getConfiguration().getConfigurationSection("fetching");
    }

    public ConfigurationSection getMessagingSection() {
        return getConfiguration().getConfigurationSection("messaging");
    }
}
