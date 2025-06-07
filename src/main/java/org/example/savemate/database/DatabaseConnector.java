package org.example.savemate.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnector {
    // Conexión usando Session Pooler (compatible con IPv4 y soporta PreparedStatement necesario para la presentacion)
    private static final String URL = "jdbc:postgresql://aws-0-eu-west-3.pooler.supabase.com:5432/postgres?sslmode=require&sslfactory=org.postgresql.ssl.NonValidatingFactory";
    private static final String USER = "postgres.uxpnfaobgwseqxihmcuu";
    private static final String PASSWORD = "SuyC_i8uGy88.#J";

    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}