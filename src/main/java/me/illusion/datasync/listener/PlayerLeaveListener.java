package me.illusion.datasync.listener;

import me.illusion.datasync.DataSyncPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerLeaveListener implements Listener {

    private final DataSyncPlugin main;

    public PlayerLeaveListener(DataSyncPlugin main) {
        this.main = main;
    }

    @EventHandler
    private void onLeave(PlayerQuitEvent event) {
        main.getStorageHandler().quit(event.getPlayer().getUniqueId());
    }
}
