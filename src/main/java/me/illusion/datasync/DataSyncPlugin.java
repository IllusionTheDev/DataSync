package me.illusion.datasync;

import lombok.Getter;
import me.illusion.datasync.packet.PacketManager;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class DataSyncPlugin extends JavaPlugin {

    private PacketManager packetManager;

    @Override
    public void onEnable() {
        packetManager = new PacketManager();
    }
}
