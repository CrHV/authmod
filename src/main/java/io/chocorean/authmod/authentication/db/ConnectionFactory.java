package io.chocorean.authmod.authentication.db;

import io.chocorean.authmod.AuthMod;
import java.sql.*;

public class ConnectionFactory {
    /**
     * Get a connection to database
     * @return Connection object
     */
    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(String.format("jdbc:%s://%s/%s",
                    AuthMod.getConfig().getDatabaseConfig().getDialect(),
                    AuthMod.getConfig().getDatabaseConfig().getHost(),
                    AuthMod.getConfig().getDatabaseConfig().getName()),
                    AuthMod.getConfig().getDatabaseConfig().getUser(),
                    AuthMod.getConfig().getDatabaseConfig().getPassword());
        } catch (SQLException ex) {
            throw new RuntimeException("Error connecting to the database", ex);
        }
    }

}