package me.illusion.datasync.database.messaging.impl;

import me.illusion.datasync.DataSyncPlugin;
import me.illusion.datasync.database.messaging.MessagingDatabase;
import me.illusion.datasync.packet.Packet;
import me.illusion.datasync.util.JedisUtil;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import redis.clients.jedis.BinaryJedisPubSub;
import redis.clients.jedis.Jedis;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class RedisMessagingImpl extends BinaryJedisPubSub implements MessagingDatabase {

    private final List<Consumer<byte[]>> callbacks = new ArrayList<>();

    private byte[] channel;

    private JedisUtil jedisUtil;
    private final DataSyncPlugin main;

    public RedisMessagingImpl(DataSyncPlugin main) {
        this.main = main;
    }

    @Override
    public CompletableFuture<Void> send(Packet packet) {
        return CompletableFuture.runAsync(() -> {
            Jedis jedis = jedisUtil.getJedis();

            jedis.publish(channel, packet.getAllBytes());

            jedisUtil.getPool().returnResource(jedis);
        });
    }

    @Override
    public void onMessage(byte[] channel, byte[] message) {
        if (!Arrays.equals(channel, this.channel))
            return;

        for (Consumer<byte[]> callback : callbacks) {
            callback.accept(message);
        }
    }


    private static byte[] getBytes(String string) {
        return string.getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public String getName() {
        return "redis";
    }

    @Override
    public CompletableFuture<Boolean> enable(ConfigurationSection section, String group) {
        jedisUtil = new JedisUtil();
        channel = ("datasync-" + group).getBytes(StandardCharsets.UTF_8);
        return CompletableFuture.supplyAsync(() -> {
            String ip = section.getString("host");
            String port = section.getString("port");
            String password = section.getString("password");

            if (!jedisUtil.connect(ip, port, password)) {
                return false;
            }

            new Thread(() -> jedisUtil.getJedis().subscribe(this, channel)).start(); // Locks thread entirely
            return true;
        }).exceptionally((throwable -> {
            throwable.printStackTrace();
            return false;
        }));
    }

    @Override
    public void addCallback(Consumer<byte[]> receivedPacket) {
        callbacks.add(receivedPacket);
    }
}
