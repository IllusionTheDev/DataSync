package me.illusion.datasync.database.messaging.impl;

import me.illusion.datasync.DataSyncPlugin;
import me.illusion.datasync.database.messaging.MessagingDatabase;
import me.illusion.datasync.packet.Packet;
import me.illusion.datasync.util.JedisUtil;
import org.bukkit.Bukkit;
import redis.clients.jedis.BinaryJedisPubSub;
import redis.clients.jedis.Jedis;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

public class RedisMessagingImpl extends BinaryJedisPubSub implements MessagingDatabase {

    private static final byte[] CHANNEL = getBytes("datasync");

    private final JedisUtil jedisUtil;
    private final DataSyncPlugin main;

    public RedisMessagingImpl(DataSyncPlugin main) {
        this.main = main;
        jedisUtil = new JedisUtil();

        log("Getting details");
        String ip = main.getConfig().getString("jedis.ip");
        String port = main.getConfig().getString("jedis.port");
        String password = main.getConfig().getString("jedis.password");

        CompletableFuture.runAsync(() -> {
            log("Trying connection");
            if (!jedisUtil.connect(ip, port, password)) {
                main.getLogger().warning("Could not connect to Jedis");
                Bukkit.getPluginManager().disablePlugin(main);
                return;
            }

            log("Connection Obtained");
            jedisUtil.getJedis().subscribe(this, CHANNEL);
        }).exceptionally((throwable -> {
            throwable.printStackTrace();
            return null;
        }));
    }

    @Override
    public CompletableFuture<Void> send(Packet packet) {
        return CompletableFuture.runAsync(() -> {
            Jedis jedis = jedisUtil.getJedis();

            jedis.publish(CHANNEL, packet.getAllBytes());

            jedisUtil.getPool().returnResource(jedis);
        });
    }

    @Override
    public void onMessage(byte[] channel, byte[] message) {
        if(!Arrays.equals(channel, CHANNEL))
            return;

        main.getPacketManager().read(message);
    }

    private void log(String string) {
        main.getLogger().info(string);
    }

    private static byte[] getBytes(String string) {
        return string.getBytes(StandardCharsets.UTF_8);
    }
}
