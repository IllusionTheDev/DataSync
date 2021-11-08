package me.illusion.datasync.listener;

import me.illusion.datasync.DataSyncPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    private final DataSyncPlugin main;

    public PlayerJoinListener(DataSyncPlugin main) {
        this.main = main;
    }

    @EventHandler
    private void onJoin(PlayerJoinEvent event) {
        main.getStorageHandler().load(event.getPlayer().getUniqueId());
    }
}
