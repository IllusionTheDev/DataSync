package me.illusion.datasync.util;

import java.sql.Connection;
import java.sql.SQLException;

public abstract class SQLConnectionProvider {

    protected Connection connection;

    public Connection get() {
        try {
            if (connection == null || !connection.isValid(150))
                load();
        } catch (SQLException e) {
            load();
        }

        return connection;
    }

    public abstract void load();
}