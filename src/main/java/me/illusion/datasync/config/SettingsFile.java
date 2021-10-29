package me.illusion.datasync.config;

import me.illusion.utilities.storage.YMLBase;
import org.bukkit.plugin.java.JavaPlugin;

public class SettingsFile extends YMLBase {

    public SettingsFile(JavaPlugin plugin) {
        super(plugin, "settings.yml");
    }


}
