package me.illusion.datasync.util;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class JedisUtil {

    private JedisPool jedisPool;

    private String password; // NOT SAFE but it's a configurable value so

    public boolean connect(String ip, String port, String password) {
        System.out.println("Connecting");


        if (port.isEmpty())
            jedisPool = new JedisPool(ip);
        else
            jedisPool = new JedisPool(ip, Integer.parseInt(port));

        System.out.println("Loaded pool");
        System.out.println(jedisPool);

        this.password = password;

        try {
            getJedis();
            System.out.println("Connected jedis");
        } catch (Exception exception) {
            System.out.println("Couldn't connect what the fuck");
            return false;
        }
        return true;
    }

    public Jedis getJedis() {
        Jedis j = jedisPool.getResource();

        if (!password.isEmpty())
            j.auth(password);

        j.select(2);
        return j;
    }

    public JedisPool getPool() {
        return jedisPool;
    }
}