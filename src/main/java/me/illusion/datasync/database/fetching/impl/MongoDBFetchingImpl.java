package me.illusion.datasync.database.fetching.impl;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientOptions;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import me.illusion.datasync.database.fetching.FetchingDatabase;
import me.illusion.datasync.handler.data.StoredData;
import org.bson.Document;
import org.bukkit.configuration.ConfigurationSection;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class MongoDBFetchingImpl implements FetchingDatabase {

    private String ip;
    private int port;
    private String database;

    private MongoClient mongoClient;
    private MongoDatabase mongoDatabase;
    private MongoCollection<Document> mongoCollection;

    @Override
    public String getName() {
        return "mongodb";
    }

    @Override
    public CompletableFuture<Boolean> enable(ConfigurationSection section) {
        return CompletableFuture.supplyAsync(() -> {
            ip = section.getString("ip");
            port = section.getInt("port");
            database = section.getString("database");

            try {
                mongoClient = MongoClients.create(
                        new ConnectionString("mongodb://" + ip + ":" + port + "/" + database));

                mongoDatabase = mongoClient.getDatabase(database);
                mongoDatabase.createCollection("datasync");
                mongoCollection = mongoDatabase.getCollection("datasync");
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }

            return true;
        });
    }

    @Override
    public CompletableFuture<StoredData> fetch(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            Document document = new Document("data-uuid", uuid.toString());

            Document found = mongoCollection.find(document).first();

            if (found == null)
                return null;

            return (StoredData) found.get("value");
        });
    }

    @Override
    public CompletableFuture<Void> store(UUID uuid, StoredData data) {
        return CompletableFuture.runAsync(() -> {
            Document document = new Document("data-uuid", uuid.toString())
                    .append("value", data);

            mongoCollection.updateOne(document, document);
        });
    }

    @Override
    public CompletableFuture<Void> wipe() {
        return CompletableFuture.runAsync(() -> {
            mongoCollection.drop();
            mongoDatabase.createCollection("datasync");
        });
    }


}
