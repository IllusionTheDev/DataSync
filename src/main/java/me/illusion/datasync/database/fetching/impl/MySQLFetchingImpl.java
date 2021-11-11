package me.illusion.datasync.database.fetching.impl;

import me.illusion.datasync.util.SQLConnectionProvider;
import me.illusion.datasync.database.fetching.FetchingDatabase;
import me.illusion.datasync.handler.data.StoredData;
import org.bukkit.configuration.ConfigurationSection;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class MySQLFetchingImpl extends SQLConnectionProvider implements FetchingDatabase {

    private static final String TABLE_NAME = "data_sync";

    private static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (uuid VARCHAR(36), data MEDIUMBLOB, PRIMARY KEY (uuid));";
    private static final String SERIALIZE_STRING = "INSERT INTO " + TABLE_NAME + " (uuid, data) VALUES (?, ?) ON DUPLICATE KEY UPDATE uuid=VALUES(uuid), data=VALUES(data);";
    private static final String DESERIALIZE_STRING = "SELECT data FROM " + TABLE_NAME + " WHERE uuid = ?";

    private String host;
    private int port;
    private String database;
    private String username;
    private String password;

    @Override
    public String getName() {
        return "mysql";
    }

    @Override
    public CompletableFuture<Boolean> enable(ConfigurationSection section) {
        return CompletableFuture.supplyAsync(() -> {
            loadConfig(section);

            return loadConnection();
        });
    }

    @Override
    public CompletableFuture<StoredData> fetch(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> fetchSync(uuid, false));
    }

    @Override
    public CompletableFuture<Void> store(UUID uuid, StoredData data) {
        return CompletableFuture.runAsync(() -> {
            connection = get();

            try(PreparedStatement statement = connection.prepareStatement(SERIALIZE_STRING)) {
                statement.setString(1, uuid.toString());
                statement.setObject(2, data);

                statement.executeUpdate();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        });
    }

    @Override
    public CompletableFuture<Void> wipe() {
        return CompletableFuture.runAsync(() -> {
            connection = get();

            try(PreparedStatement statement = connection.prepareStatement("DROP TABLE " + TABLE_NAME)) {
                statement.executeUpdate();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        });
    }

    private StoredData fetchSync(UUID uuid, boolean fetchedOnce) {
        connection = get();

        try(PreparedStatement statement = connection.prepareStatement(DESERIALIZE_STRING)) {
            statement.setString(1, uuid.toString());

            try(ResultSet result = statement.executeQuery()) {
                if (!result.next())
                    return null;

                // Object object = rs.getObject(1);

                byte[] buf = result.getBytes(1);
                if (buf != null)
                    try(ObjectInputStream stream = new ObjectInputStream(new ByteArrayInputStream(buf))) {
                        return (StoredData) stream.readObject();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                ;
            }
        } catch (SQLException | IOException throwables) {
            if(fetchedOnce) { // Prevent infinite loop, while still reviving connections
                throwables.printStackTrace();
                return null;
            }

            loadConnection();
            return fetchSync(uuid, true);
        }

        return null;
    }
    private void loadConfig(ConfigurationSection config) {
        host = config.getString("host");
        port = config.getInt("port");
        database = config.getString("database");
        username = config.getString("username");
        password = config.getString("password");
    }

    private boolean loadConnection() {
        try {
            connection = null;

            Class.forName("com.mysql.jdbc.Driver");

            connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database + "?autoreconnect=true", username, password);
            connection.createStatement().execute(CREATE_TABLE);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    @Override
    public void load() {
        loadConnection();
    }
}
