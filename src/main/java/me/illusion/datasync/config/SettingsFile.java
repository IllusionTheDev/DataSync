package me.illusion.datasync.config;

import lombok.Getter;
import me.illusion.utilities.storage.YMLBase;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.CompletableFuture;

@Getter
public class SettingsFile extends YMLBase {

    private final ConfigurationSection cacheSection;

    public SettingsFile(JavaPlugin plugin) {
        super(plugin, "settings.yml");

        FileConfiguration config = getConfiguration();

        ConfigurationSection dataSection = config.getConfigurationSection("data");

        cacheSection = dataSection.getConfigurationSection("caching");
    }

    public boolean shouldEnable(String providerId) {
        FileConfiguration configuration = getConfiguration();

        ConfigurationSection dataSection = configuration.getConfigurationSection("data");
        ConfigurationSection enabledProvidersSection = dataSection.getConfigurationSection("saving.enabled-providers");

        if(!enabledProvidersSection.contains(providerId)) {
            enabledProvidersSection.addDefault(providerId, true);
        }
        return enabledProvidersSection.getBoolean(providerId, true);
    }

    public void allowSave() {
        CompletableFuture.runAsync(this::save);
    }


}
