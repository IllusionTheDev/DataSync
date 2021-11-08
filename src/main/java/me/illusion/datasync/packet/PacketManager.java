package me.illusion.datasync.packet;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;

public class PacketManager {

    private static final Map<Byte, Class<? extends Packet>> identifiers = new HashMap<>();
    private final List<PacketProcessor> processors = new ArrayList<>();
    private final Map<Byte, List<PacketHandler<Packet>>> handlers = new HashMap<>();

    private final PacketWaiter waiter;

    public PacketManager() {
        waiter = new PacketWaiter(this);
    }

    public static void registerPacket(int packetId, Class<? extends Packet> packetClass) {
        registerPacket((byte) packetId, packetClass);
    }

    public static void registerPacket(byte packetId, Class<? extends Packet> packetClass) {
        if (identifiers.containsKey(packetId))
            throw new UnsupportedOperationException("Packet identifier for packet " + packetClass.getSimpleName() + " is already registered. ");

        identifiers.put(packetId, packetClass);
    }


    public static byte getIdentifier(Class<? extends Packet> clazz) {
        for (Map.Entry<Byte, Class<? extends Packet>> entry : identifiers.entrySet()) {
            if (clazz.equals(entry.getValue()))
                return entry.getKey();
        }

        return 0;
    }

    public Class<? extends Packet> getPacketClass(byte identifier) {
        return identifiers.get(identifier);
    }

    public void registerProcessor(PacketProcessor processor) {
        processors.add(processor);
    }

    public CompletableFuture<Void> send(Packet packet) {
        Set<CompletableFuture<Void>> futures = new HashSet<>();

        for (PacketProcessor processor : processors)
            futures.add(processor.send(packet));

        byte id = packet.getIdentifier();
        List<PacketHandler<Packet>> handler = handlers.get(id);

        if (handler == null)
            return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));

        for (PacketHandler<Packet> packetHandler : handler)
            packetHandler.onSend(packet);

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
    }

    public Packet read(byte[] bytes) {
        Class<? extends Packet> type = getPacketClass(bytes[0]);

        try {
            Packet packet = type.getConstructor(byte[].class).newInstance(bytes);
            List<PacketHandler<Packet>> handler = handlers.get(bytes[0]);

            if (handler != null)
                for (PacketHandler<Packet> packetHandler : handler)
                    packetHandler.onReceive(packet);

            return packet;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }

        return null;
    }

    public <T extends Packet> void subscribe(Class<T> packetClass, PacketHandler<T> handler) {
        byte identifier = getIdentifier(packetClass);

        handlers.putIfAbsent(identifier, new ArrayList<>());
        handlers.get(identifier).add((PacketHandler<Packet>) handler);
    }

    public <T extends Packet> T await(Class<T> clazz, Predicate<T> predicate) {
        return waiter.await(clazz, predicate);
    }
}
