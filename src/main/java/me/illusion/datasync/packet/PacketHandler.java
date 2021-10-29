package me.illusion.datasync.packet;

public interface PacketHandler<T extends Packet> {

    default void onSend(T packet) {
    }

    default void onReceive(T packet) {
    }
}
