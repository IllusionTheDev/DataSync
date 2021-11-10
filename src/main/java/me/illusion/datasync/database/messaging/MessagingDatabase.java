package me.illusion.datasync.database.messaging;

import me.illusion.datasync.database.Database;
import me.illusion.datasync.packet.PacketProcessor;

import java.util.function.Consumer;

public interface MessagingDatabase extends PacketProcessor, Database {

}
