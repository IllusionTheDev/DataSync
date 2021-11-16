package me.illusion.datasync.database;

import org.bukkit.configuration.ConfigurationSection;

import java.util.concurrent.CompletableFuture;

public interface Database {

    String getName();

    CompletableFuture<Boolean> enable(ConfigurationSection section, String group);
}
