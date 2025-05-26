package org.example.savemate.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnector {
    private static final String URL = "jdbc:postgresql://db.uxpnfaobgwseqxihmcuu.supabase.co:5432/postgres";
    private static final String USER = "postgres";
    private static final String PASSWORD = "SuyC_i8uGy88.#J";

    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}


